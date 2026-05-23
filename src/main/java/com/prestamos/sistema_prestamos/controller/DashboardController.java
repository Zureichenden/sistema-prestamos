package com.prestamos.sistema_prestamos.controller;

import com.prestamos.sistema_prestamos.dto.DashboardDTO;
import com.prestamos.sistema_prestamos.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDTO> obtener() {
        return ResponseEntity.ok(dashboardService.obtenerDashboard());
    }
}