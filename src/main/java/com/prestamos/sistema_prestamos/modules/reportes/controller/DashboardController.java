package com.prestamos.sistema_prestamos.modules.reportes.controller;

import com.prestamos.sistema_prestamos.modules.reportes.dto.DashboardDTO;
import com.prestamos.sistema_prestamos.modules.reportes.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor

public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDTO> obtener() {
        return ResponseEntity.ok(dashboardService.obtenerDashboard());
    }
}