package com.homeexpress.home_express_api.controller.admin;

import com.homeexpress.home_express_api.dto.response.AdminDashboardStatsResponse;
import com.homeexpress.home_express_api.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<AdminDashboardStatsResponse> getStats() {
        AdminDashboardStatsResponse stats = dashboardService.getAdminDashboardStats();
        return ResponseEntity.ok(stats);
    }
}


