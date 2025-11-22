package com.homeexpress.home_express_api.service.booking;

import com.homeexpress.home_express_api.dto.estimation.AutoEstimationRequest;
import com.homeexpress.home_express_api.dto.estimation.AutoEstimationResponse;
import com.homeexpress.home_express_api.entity.Booking;
import com.homeexpress.home_express_api.entity.BookingItem;
import com.homeexpress.home_express_api.entity.UserRole;
import com.homeexpress.home_express_api.exception.ResourceNotFoundException;
import com.homeexpress.home_express_api.exception.UnauthorizedException;
import com.homeexpress.home_express_api.repository.BookingItemRepository;
import com.homeexpress.home_express_api.repository.BookingRepository;
import com.homeexpress.home_express_api.service.EstimationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Service dựng request ước tính giá cho một booking hiện có.
 * Giữ tách biệt với BookingService để tránh vòng phụ thuộc.
 */
@Service
public class BookingEstimateService {

    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final EstimationService estimationService;

    public BookingEstimateService(BookingRepository bookingRepository,
                                  BookingItemRepository bookingItemRepository,
                                  EstimationService estimationService) {
        this.bookingRepository = bookingRepository;
        this.bookingItemRepository = bookingItemRepository;
        this.estimationService = estimationService;
    }

    /**
     * Tính bảng giá dự tính cho booking, có kiểm tra quyền truy cập.
     */
    public BookingEstimateResult estimateForBooking(Long bookingId, Long requesterId, UserRole role, Query query) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        // Chặn khách lạ xem booking người khác
        if (role == UserRole.CUSTOMER && !booking.getCustomerId().equals(requesterId)) {
            throw new UnauthorizedException("You are not authorized to view this booking");
        }

        // Map entity -> request estimation
        AutoEstimationRequest req = buildRequestFromBooking(booking);

        List<BookingItem> items = bookingItemRepository.findByBookingId(bookingId);
        req.setItems(items.stream().map(this::toItem).collect(Collectors.toList()));

        AutoEstimationResponse response = estimationService.generateAutoEstimation(req);

        // Lọc/sắp xếp/phân trang ở tầng service để FE chỉ việc render
        List<AutoEstimationResponse.TransportEstimate> estimations = new ArrayList<>();
        if (response.getEstimations() != null) {
            estimations.addAll(response.getEstimations());
        }

        // Lọc rating tối thiểu
        if (query.ratingGte != null) {
            estimations = estimations.stream()
                    .filter(e -> e.getRating() >= query.ratingGte)
                    .collect(Collectors.toList());
        }
        // Lọc giá trần
        if (query.priceCap != null) {
            estimations = estimations.stream()
                    .filter(e -> e.getTotalPrice() <= query.priceCap)
                    .collect(Collectors.toList());
        }
        // Lọc loại xe
        if (query.vehicleType != null && !query.vehicleType.isBlank()) {
            String vt = query.vehicleType.toLowerCase(Locale.ROOT);
            estimations = estimations.stream()
                    .filter(e -> e.getVehicleType() != null && e.getVehicleType().toLowerCase(Locale.ROOT).contains(vt))
                    .collect(Collectors.toList());
        }

        // Sắp xếp
        Comparator<AutoEstimationResponse.TransportEstimate> comparator = Comparator.comparingLong(AutoEstimationResponse.TransportEstimate::getTotalPrice);
        if ("rating".equalsIgnoreCase(query.sort)) {
            comparator = Comparator.comparingDouble(AutoEstimationResponse.TransportEstimate::getRating);
        } else if ("score".equalsIgnoreCase(query.sort)) {
            comparator = Comparator.comparingDouble(AutoEstimationResponse.TransportEstimate::getRankScore);
        }
        if ("desc".equalsIgnoreCase(query.order)) {
            comparator = comparator.reversed();
        }
        estimations.sort(comparator);

        // Summary top 3
        Summary summary = buildSummary(estimations);

        // Phân trang đơn giản
        int total = estimations.size();
        int from = Math.max(0, (query.page - 1) * query.size);
        int to = Math.min(total, from + query.size);
        List<AutoEstimationResponse.TransportEstimate> pageContent = estimations.subList(from, to);

        response.setEstimations(pageContent);

        Pagination pagination = new Pagination();
        pagination.setPage(query.page);
        pagination.setSize(query.size);
        pagination.setTotal(total);
        pagination.setHasMore(to < total);

        return new BookingEstimateResult(response, summary, pagination, query);
    }

    private AutoEstimationRequest buildRequestFromBooking(Booking booking) {
        AutoEstimationRequest req = new AutoEstimationRequest();
        req.setPickupAddress(booking.getPickupAddress());
        req.setDeliveryAddress(booking.getDeliveryAddress());
        req.setPickupFloor(booking.getPickupFloor());
        req.setDeliveryFloor(booking.getDeliveryFloor());
        req.setHasElevatorPickup(Boolean.TRUE.equals(booking.getPickupHasElevator()));
        req.setHasElevatorDelivery(Boolean.TRUE.equals(booking.getDeliveryHasElevator()));
        req.setPickupDatetime(booking.getPreferredDate() != null ? booking.getPreferredDate().toString() : null);

        if (booking.getPickupLatitude() != null && booking.getPickupLongitude() != null) {
            req.setPickupLat(booking.getPickupLatitude());
            req.setPickupLng(booking.getPickupLongitude());
        }
        if (booking.getDeliveryLatitude() != null && booking.getDeliveryLongitude() != null) {
            req.setDeliveryLat(booking.getDeliveryLatitude());
            req.setDeliveryLng(booking.getDeliveryLongitude());
        }
        return req;
    }

    private AutoEstimationRequest.Item toItem(BookingItem item) {
        AutoEstimationRequest.Item dto = new AutoEstimationRequest.Item();
        dto.setCategoryId(item.getCategoryId()); // rate card dựa vào category
        dto.setName(item.getName());
        dto.setQuantity(item.getQuantity());
        dto.setWeight(toDouble(item.getWeightKg()));
        dto.setFragile(Boolean.TRUE.equals(item.getIsFragile()));
        dto.setRequiresDisassembly(Boolean.TRUE.equals(item.getRequiresDisassembly()));
        dto.setRequiresPackaging(Boolean.FALSE); // chưa lưu flag này ở booking item
        return dto;
    }

    private Double toDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }

    private Summary buildSummary(List<AutoEstimationResponse.TransportEstimate> estimations) {
        if (estimations == null || estimations.isEmpty()) return null;
        AutoEstimationResponse.TransportEstimate cheapest = estimations.stream()
                .min(Comparator.comparingLong(AutoEstimationResponse.TransportEstimate::getTotalPrice))
                .orElse(null);
        AutoEstimationResponse.TransportEstimate topRated = estimations.stream()
                .max(Comparator.comparingDouble(AutoEstimationResponse.TransportEstimate::getRating))
                .orElse(null);
        AutoEstimationResponse.TransportEstimate bestScore = estimations.stream()
                .max(Comparator.comparingDouble(AutoEstimationResponse.TransportEstimate::getRankScore))
                .orElse(null);
        return new Summary(cheapest, topRated, bestScore);
    }

    public static class Query {
        public int page = 1;
        public int size = 10;
        public String sort = "price"; // price|rating|score
        public String order = "asc";
        public Double ratingGte;
        public Long priceCap;
        public String vehicleType;
    }

    public static class BookingEstimateResult {
        private final AutoEstimationResponse estimates;
        private final Summary summary;
        private final Pagination pagination;
        private final Query appliedFilters;

        public BookingEstimateResult(AutoEstimationResponse estimates, Summary summary, Pagination pagination, Query appliedFilters) {
            this.estimates = estimates;
            this.summary = summary;
            this.pagination = pagination;
            this.appliedFilters = appliedFilters;
        }

        public AutoEstimationResponse getEstimates() {
            return estimates;
        }

        public Summary getSummary() {
            return summary;
        }

        public Pagination getPagination() {
            return pagination;
        }

        public Query getAppliedFilters() {
            return appliedFilters;
        }
    }

    public static class Summary {
        private AutoEstimationResponse.TransportEstimate cheapest;
        private AutoEstimationResponse.TransportEstimate topRated;
        private AutoEstimationResponse.TransportEstimate bestScore;

        public Summary(AutoEstimationResponse.TransportEstimate cheapest,
                       AutoEstimationResponse.TransportEstimate topRated,
                       AutoEstimationResponse.TransportEstimate bestScore) {
            this.cheapest = cheapest;
            this.topRated = topRated;
            this.bestScore = bestScore;
        }

        public AutoEstimationResponse.TransportEstimate getCheapest() {
            return cheapest;
        }

        public AutoEstimationResponse.TransportEstimate getTopRated() {
            return topRated;
        }

        public AutoEstimationResponse.TransportEstimate getBestScore() {
            return bestScore;
        }
    }

    public static class Pagination {
        private int page;
        private int size;
        private int total;
        private boolean hasMore;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public boolean isHasMore() {
            return hasMore;
        }

        public void setHasMore(boolean hasMore) {
            this.hasMore = hasMore;
        }
    }
}
