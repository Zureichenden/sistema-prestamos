package com.prestamos.sistema_prestamos.controller;

import com.prestamos.sistema_prestamos.dto.*;
import com.prestamos.sistema_prestamos.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/clientes")
    public ResponseEntity<Page<ReporteClienteDTO>> reporteClientes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(reporteService.reporteClientes(inicio, fin, pagina, tamanio));
    }

    @GetMapping("/prestamos")
    public ResponseEntity<Page<PrestamoResponseDTO>> reportePrestamos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(reporteService.reportePrestamos(inicio, fin, pagina, tamanio));
    }

    @GetMapping("/pagos")
    public ResponseEntity<Page<ReportePagoDTO>> reportePagos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(reporteService.reportePagos(inicio, fin, pagina, tamanio));
    }
}