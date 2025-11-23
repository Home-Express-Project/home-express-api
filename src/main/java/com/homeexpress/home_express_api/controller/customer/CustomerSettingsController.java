package com.homeexpress.home_express_api.controller.customer;

import com.homeexpress.home_express_api.dto.request.UpdateCustomerSettingsRequest;
import com.homeexpress.home_express_api.dto.response.CustomerSettingsResponse;
import com.homeexpress.home_express_api.entity.User;
import com.homeexpress.home_express_api.entity.UserRole;
import com.homeexpress.home_express_api.repository.UserRepository;
import com.homeexpress.home_express_api.service.CustomerSettingsService;
import com.homeexpress.home_express_api.util.AuthenticationUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/customer/settings")
public class CustomerSettingsController {

    private final UserRepository userRepository;

    private final CustomerSettingsService customerSettingsService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getSettings(Authentication authentication) {
        User user = resolveUser(authentication);
        if (user.getRole() != UserRole.CUSTOMER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only customers can access these settings"));
        }

        CustomerSettingsResponse response = customerSettingsService.getSettings(user.getUserId());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    @PutMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> updateSettings(@Valid @RequestBody UpdateCustomerSettingsRequest request,
                                            Authentication authentication) {
        User user = resolveUser(authentication);
        if (user.getRole() != UserRole.CUSTOMER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only customers can update these settings"));
        }

        CustomerSettingsResponse updated = customerSettingsService.updateSettings(user.getUserId(), request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Customer settings updated successfully",
                "data", updated
        ));
    }

    private User resolveUser(Authentication authentication) {
        return AuthenticationUtils.getUser(authentication, userRepository);
    }
}

