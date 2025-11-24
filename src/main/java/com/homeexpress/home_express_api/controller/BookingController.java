package com.homeexpress.home_express_api.controller;

import com.homeexpress.home_express_api.dto.booking.AssignTransportRequest;
import com.homeexpress.home_express_api.dto.booking.BookingRequest;
import com.homeexpress.home_express_api.dto.booking.BookingResponse;
import com.homeexpress.home_express_api.dto.booking.BookingStatusHistoryResponse;
import com.homeexpress.home_express_api.dto.booking.BookingUpdateRequest;
import com.homeexpress.home_express_api.dto.request.ConfirmCompletionRequest;
import com.homeexpress.home_express_api.dto.request.UploadBookingEvidenceRequest;
import com.homeexpress.home_express_api.dto.response.BookingEvidenceResponse;
import com.homeexpress.home_express_api.dto.response.QuotationResponse;
import com.homeexpress.home_express_api.entity.EvidenceType;
import com.homeexpress.home_express_api.entity.User;
import com.homeexpress.home_express_api.repository.UserRepository;
import com.homeexpress.home_express_api.service.BookingService;
import com.homeexpress.home_express_api.service.EvidenceService;
import com.homeexpress.home_express_api.service.booking.BookingEstimateService;
import com.homeexpress.home_express_api.util.AuthenticationUtils;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    // Service chinh xu ly tao/cap nhat booking; nhan userId/role de kiem tra quyen
    private final BookingService bookingService;

    private final UserRepository userRepository;

    private final EvidenceService evidenceService;

    private final BookingEstimateService bookingEstimateService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<?> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {

        User user = AuthenticationUtils.getUser(authentication, userRepository);

        // Truyen userId/role de service gan chu so huu va validate quyen
        BookingResponse response = bookingService.createBooking(request, user.getUserId(), user.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Booking created successfully",
                "booking", response
        ));
    }

    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @GetMapping
    public ResponseEntity<?> getBookings(
            @RequestParam(required = false) Long customerId,
            Authentication authentication) {

        User user = AuthenticationUtils.getUser(authentication, userRepository);

        // Customer chi thay booking cua minh; Manager co the truyen customerId de loc
        List<BookingResponse> bookings =
                bookingService.getBookingsForUser(customerId, user.getUserId(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "bookings", bookings,
                "count", bookings.size()
        ));
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER','TRANSPORT')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(
            @PathVariable Long id,
            Authentication authentication) {

        User user = AuthenticationUtils.getUser(authentication, userRepository);

        // Service tu kiem tra quyen truy cap theo vai tro va owner
        BookingResponse booking = bookingService.getBookingById(id, user.getUserId(), user.getRole());

        return ResponseEntity.ok(booking);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingUpdateRequest request,
            Authentication authentication) {

        User user = AuthenticationUtils.getUser(authentication, userRepository);

        // Cho phep khach/manager cap nhat thong tin truoc khi booking chot
        BookingResponse response = bookingService.updateBooking(id, request, user.getUserId(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "message", "Booking updated successfully",
                "booking", response
        ));
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            Authentication authentication) {

        User user = AuthenticationUtils.getUser(authentication, userRepository);

        String reason = body != null ? body.get("reason") : null;

        // Huy booking voi ly do tuy chon; service se log va kiem tra trang thai hop le
        bookingService.cancelBooking(id, reason, user.getUserId(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "message", "Booking cancelled successfully",
                "bookingId", id
        ));
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER','TRANSPORT')")
    @GetMapping("/{id}/history")
    public ResponseEntity<?> getBookingHistory(
            @PathVariable Long id,
            Authentication authentication) {

        User user = AuthenticationUtils.getUser(authentication, userRepository);

        // Lich su trang thai day du de audit tung buoc cua booking
        List<BookingStatusHistoryResponse> history =
                bookingService.getBookingHistory(id, user.getUserId(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "bookingId", id,
                "history", history,
                "count", history.size()
        ));
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER')")
    @GetMapping("/{id}/quotations")
    public ResponseEntity<?> getBookingQuotations(
            @PathVariable Long id,
            Authentication authentication) {

        User user = AuthenticationUtils.getUser(authentication, userRepository);

        // Kiem tra quyen truoc khi tra danh sach bao gia
        bookingService.getBookingById(id, user.getUserId(), user.getRole());

        List<QuotationResponse> quotations = bookingService.getBookingQuotations(id, user.getUserId(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "bookingId", id,
                "quotations", quotations,
                "count", quotations.size()
        ));
    }

    /**
     * Khach chon transport sau khi xem bang bao gia/estimate
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{id}/assign-transport")
    public ResponseEntity<?> assignTransport(
            @PathVariable Long id,
            @Valid @RequestBody AssignTransportRequest request,
            Authentication authentication) {

        User user = AuthenticationUtils.getUser(authentication, userRepository);

        BookingResponse response = bookingService.assignTransport(
                id,
                request.getTransportId(),
                request.getEstimatedPrice(),
                user.getUserId(),
                user.getRole());

        return ResponseEntity.ok(Map.of(
                "message", "Transport assigned",
                "booking", response
        ));
    }

    /**
     * Khach xem bang gia du tinh dua tren rate card cua cac transport
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{id}/estimates")
    public ResponseEntity<?> getBookingEstimates(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "price") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) Double ratingGte,
            @RequestParam(required = false) Long priceCap,
            @RequestParam(required = false) String vehicleType,
            Authentication authentication) {

        User user = AuthenticationUtils.getUser(authentication, userRepository);

        // Chuan hoa query de tranh page/size am hoac qua lon
        BookingEstimateService.Query query = new BookingEstimateService.Query();
        query.page = page != null && page > 0 ? page : 1;
        query.size = size != null && size > 0 && size <= 50 ? size : 10;
        query.sort = sort;
        query.order = order;
        query.ratingGte = ratingGte;
        query.priceCap = priceCap;
        query.vehicleType = vehicleType;

        var estimates = bookingEstimateService.estimateForBooking(id, user.getUserId(), user.getRole(), query);

        return ResponseEntity.ok(Map.of(
                "bookingId", id,
                "estimates", estimates.getEstimates(),
                "summary", estimates.getSummary(),
                "pagination", estimates.getPagination(),
                "applied_filters", estimates.getAppliedFilters()
        ));
    }

    /**
     * Khach xac nhan dich vu da hoan tat; status chuyen CONFIRMED_BY_CUSTOMER
     * va settlement se duoc xu ly sang READY/credit vi
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{id}/confirm-completion")
    public ResponseEntity<?> confirmCompletion(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) ConfirmCompletionRequest request,
            Authentication authentication) {

        User user = AuthenticationUtils.getUser(authentication, userRepository);

        BookingResponse response = bookingService.confirmBookingCompletion(id, request, user.getUserId());

        return ResponseEntity.ok(Map.of(
                "message", "Booking completion confirmed successfully",
                "booking", response
        ));
    }

    // ========================================================================
    // EVIDENCE ENDPOINTS
    // ========================================================================

    /**
     * Lay toan bo evidence cua booking, co the loc theo loai evidence
     */
    @PreAuthorize("hasAnyRole('CUSTOMER','TRANSPORT','MANAGER')")
    @GetMapping("/{bookingId}/evidence")
    public ResponseEntity<?> getBookingEvidence(
            @PathVariable Long bookingId,
            @RequestParam(required = false) EvidenceType type,
            Authentication authentication) {

        User user = AuthenticationUtils.getUser(authentication, userRepository);

        List<BookingEvidenceResponse> evidence = evidenceService.getBookingEvidence(
                bookingId,
                user.getUserId(),
                user.getRole(),
                type
        );

        return ResponseEntity.ok(Map.of(
                "bookingId", bookingId,
                "evidence", evidence,
                "count", evidence.size()
        ));
    }

    /**
     * Upload evidence cho booking (khach hoac transport deu co the)
     */
    @PreAuthorize("hasAnyRole('CUSTOMER','TRANSPORT')")
    @PostMapping("/{bookingId}/evidence")
    public ResponseEntity<?> uploadBookingEvidence(
            @PathVariable Long bookingId,
            @Valid @RequestBody UploadBookingEvidenceRequest request,
            Authentication authentication) {

        User user = AuthenticationUtils.getUser(authentication, userRepository);

        BookingEvidenceResponse response = evidenceService.uploadBookingEvidence(
                bookingId,
                request,
                user.getUserId(),
                user.getRole()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Evidence uploaded successfully",
                "evidence", response
        ));
    }

    /**
     * Xoa evidence (chi nguoi upload hoac manager)
     */
    @PreAuthorize("hasAnyRole('CUSTOMER','TRANSPORT','MANAGER')")
    @DeleteMapping("/evidence/{evidenceId}")
    public ResponseEntity<?> deleteEvidence(
            @PathVariable Long evidenceId,
            Authentication authentication) {

        User user = AuthenticationUtils.getUser(authentication, userRepository);

        evidenceService.deleteEvidence(evidenceId, user.getUserId(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "message", "Evidence deleted successfully"
        ));
    }

}
