package com.homeexpress.home_express_api.controller.transport;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.homeexpress.home_express_api.dto.request.CategoryPricingRequest;
import com.homeexpress.home_express_api.dto.request.RateCardRequest;
import com.homeexpress.home_express_api.dto.response.ApiResponse;
import com.homeexpress.home_express_api.dto.response.CategoryPricingResponse;
import com.homeexpress.home_express_api.dto.response.RateCardResponse;
import com.homeexpress.home_express_api.dto.response.ReadyToQuoteStatusResponse;
import com.homeexpress.home_express_api.entity.Transport;
import com.homeexpress.home_express_api.entity.User;
import com.homeexpress.home_express_api.entity.UserRole;
import com.homeexpress.home_express_api.repository.TransportRepository;
import com.homeexpress.home_express_api.repository.UserRepository;
import com.homeexpress.home_express_api.service.CategoryPricingService;
import com.homeexpress.home_express_api.service.RateCardService;
import com.homeexpress.home_express_api.util.AuthenticationUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transport")
public class TransportRateCardController {

    private final UserRepository userRepository;
    private final TransportRepository transportRepository;
    private final RateCardService rateCardService;
    private final CategoryPricingService categoryPricingService;

    public TransportRateCardController(UserRepository userRepository,
            TransportRepository transportRepository,
            RateCardService rateCardService,
            CategoryPricingService categoryPricingService) {
        this.userRepository = userRepository;
        this.transportRepository = transportRepository;
        this.rateCardService = rateCardService;
        this.categoryPricingService = categoryPricingService;
    }

    @GetMapping("/rate-cards")
    @PreAuthorize("hasRole('TRANSPORT')")
    public ResponseEntity<?> getRateCards(Authentication authentication) {
        User user = AuthenticationUtils.getUser(authentication, userRepository);
        if (user.getRole() != UserRole.TRANSPORT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only transport accounts can access this resource"));
        }

        Transport transport = transportRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Transport profile not found for user"));

        List<RateCardResponse> cards = rateCardService.getRateCardsForTransport(transport.getTransportId());
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/rate-cards")
    @PreAuthorize("hasRole('TRANSPORT')")
    public ResponseEntity<?> createOrUpdateRateCard(@Valid @RequestBody RateCardRequest request,
            Authentication authentication) {
        User user = AuthenticationUtils.getUser(authentication, userRepository);
        if (user.getRole() != UserRole.TRANSPORT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only transport accounts can access this resource"));
        }

        Transport transport = transportRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Transport profile not found for user"));

        RateCardResponse response = rateCardService.createOrUpdateRateCard(transport.getTransportId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/rate-cards/{rateCardId}")
    @PreAuthorize("hasRole('TRANSPORT')")
    public ResponseEntity<?> deleteRateCard(@PathVariable Long rateCardId,
            Authentication authentication) {
        User user = AuthenticationUtils.getUser(authentication, userRepository);
        if (user.getRole() != UserRole.TRANSPORT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only transport accounts can access this resource"));
        }

        Transport transport = transportRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Transport profile not found for user"));

        rateCardService.deleteRateCard(transport.getTransportId(), rateCardId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ready-status")
    @PreAuthorize("hasRole('TRANSPORT')")
    public ResponseEntity<?> getReadyStatus(Authentication authentication) {
        User user = AuthenticationUtils.getUser(authentication, userRepository);
        if (user.getRole() != UserRole.TRANSPORT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only transport accounts can access this resource"));
        }

        Transport transport = transportRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Transport profile not found for user"));

        ReadyToQuoteStatusResponse status = rateCardService.getReadyToQuoteStatus(transport.getTransportId());
        return ResponseEntity.ok(status);
    }

    // ========================================================================
    // CATEGORY PRICING ENDPOINTS
    // ========================================================================
    /**
     * Get all category pricing for the authenticated transport user
     */
    @GetMapping("/category-pricing")
    @PreAuthorize("hasRole('TRANSPORT')")
    public ResponseEntity<?> getCategoryPricing(Authentication authentication) {
        User user = AuthenticationUtils.getUser(authentication, userRepository);
        if (user.getRole() != UserRole.TRANSPORT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only transport accounts can access this resource"));
        }

        Transport transport = transportRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Transport profile not found for user"));

        List<CategoryPricingResponse> pricingList = categoryPricingService.getCategoryPricingByTransport(transport.getTransportId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("pricingRules", pricingList)));
    }

    /**
     * Create or update category pricing for the authenticated transport user
     */
    @PostMapping("/category-pricing")
    @PreAuthorize("hasRole('TRANSPORT')")
    public ResponseEntity<?> createCategoryPricing(@Valid @RequestBody CategoryPricingRequest request,
            Authentication authentication) {
        User user = AuthenticationUtils.getUser(authentication, userRepository);
        if (user.getRole() != UserRole.TRANSPORT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only transport accounts can access this resource"));
        }

        Transport transport = transportRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Transport profile not found for user"));

        // Ensure the request is for the authenticated transport user
        request.setTransportId(transport.getTransportId());

        try {
            CategoryPricingResponse response = categoryPricingService.createCategoryPricing(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(Map.of("pricingId", response.getCategoryPricingId()),
                            "Category pricing created successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update category pricing for the authenticated transport user
     */
    @PostMapping("/category-pricing/{id}")
    @PreAuthorize("hasRole('TRANSPORT')")
    public ResponseEntity<?> updateCategoryPricing(@PathVariable Long id,
            @Valid @RequestBody CategoryPricingRequest request,
            Authentication authentication) {
        User user = AuthenticationUtils.getUser(authentication, userRepository);
        if (user.getRole() != UserRole.TRANSPORT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only transport accounts can access this resource"));
        }

        Transport transport = transportRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Transport profile not found for user"));

        // Ensure the request is for the authenticated transport user
        request.setTransportId(transport.getTransportId());

        try {
            CategoryPricingResponse response = categoryPricingService.updateCategoryPricing(id, request);
            return ResponseEntity.ok(ApiResponse.success(response, "Category pricing updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Delete/deactivate category pricing for the authenticated transport user
     */
    @DeleteMapping("/category-pricing/{id}")
    @PreAuthorize("hasRole('TRANSPORT')")
    public ResponseEntity<?> deleteCategoryPricing(@PathVariable Long id,
            Authentication authentication) {
        User user = AuthenticationUtils.getUser(authentication, userRepository);
        if (user.getRole() != UserRole.TRANSPORT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only transport accounts can access this resource"));
        }

        Transport transport = transportRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Transport profile not found for user"));

        try {
            // Verify the pricing belongs to this transport before deleting
            CategoryPricingResponse pricing = categoryPricingService.getCategoryPricingById(id);
            if (!pricing.getTransportId().equals(transport.getTransportId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You can only delete your own pricing"));
            }

            categoryPricingService.deactivateCategoryPricing(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Category pricing deactivated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
