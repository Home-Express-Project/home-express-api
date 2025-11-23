package com.homeexpress.home_express_api.service;

import com.homeexpress.home_express_api.dto.response.CustomerDashboardStatsResponse;
import com.homeexpress.home_express_api.entity.BookingStatus;
import com.homeexpress.home_express_api.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomerDashboardService {

    private final BookingRepository bookingRepository;

    public CustomerDashboardStatsResponse getDashboardStats(Long customerId) {
        long total = bookingRepository.countByCustomerId(customerId);
        long pending = bookingRepository.countByCustomerIdAndStatus(customerId, BookingStatus.PENDING);
        long completed = bookingRepository.countByCustomerIdAndStatus(customerId, BookingStatus.COMPLETED);
        long cancelled = bookingRepository.countByCustomerIdAndStatus(customerId, BookingStatus.CANCELLED);

        BigDecimal totalSpent = bookingRepository.sumFinalPriceByCustomerAndStatus(customerId, BookingStatus.COMPLETED);
        if (totalSpent == null) {
            totalSpent = BigDecimal.ZERO;
        }

        CustomerDashboardStatsResponse response = new CustomerDashboardStatsResponse();
        response.setTotalBookings(total);
        response.setPendingBookings(pending);
        response.setCompletedBookings(completed);
        response.setCancelledBookings(cancelled);
        response.setTotalSpent(totalSpent);

        // Placeholder: actual average rating calculation should be wired once review data is available.
        response.setAverageRating(0.0);

        return response;
    }
}

