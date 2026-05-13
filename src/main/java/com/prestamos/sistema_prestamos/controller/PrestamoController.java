package com.prestamos.sistema_prestamos.controller;

import com.prestamos.sistema_prestamos.dto.PagoResponseDTO;
import com.prestamos.sistema_prestamos.dto.PrestamoRequestDTO;
import com.prestamos.sistema_prestamos.entity.Amortizacion;
import com.prestamos.sistema_prestamos.entity.Prestamo;
import com.prestamos.sistema_prestamos.service.ExcelService;
import com.prestamos.sistema_prestamos.service.PdfService;
import com.prestamos.sistema_prestamos.service.PrestamoService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.prestamos.sistema_prestamos.dto.PagoRequestDTO;
import com.prestamos.sistema_prestamos.dto.PagoResponseDTO;
import com.prestamos.sistema_prestamos.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
public class PrestamoController {
    private final PagoService pagoService;

    private final PrestamoService prestamoService;
    private final PdfService pdfService;
    private final ExcelService excelService;

    @PostMapping
    public ResponseEntity<Prestamo> crear(@Valid @RequestBody PrestamoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.crear(dto));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<Prestamo>> listarPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(prestamoService.listarPorCliente(clienteId, pagina, tamanio));
    }

    /*
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Prestamo>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(prestamoService.listarPorCliente(clienteId));
    }

     */

    @GetMapping("/prestamo/{prestamoId}")
    public ResponseEntity<Page<PagoResponseDTO>> listarPorPrestamo(
            @PathVariable Long prestamoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(pagoService.listarPorPrestamo(prestamoId, pagina, tamanio));
    }

    @GetMapping("/{prestamoId}/amortizaciones")
    public ResponseEntity<List<Amortizacion>> obtenerTabla(@PathVariable Long prestamoId) {
        return ResponseEntity.ok(prestamoService.obtenerTablaAmortizacion(prestamoId));
    }

    @GetMapping("/{prestamoId}/pdf")
    public ResponseEntity<byte[]> exportarPdf(@PathVariable Long prestamoId) {
        byte[] pdf = pdfService.generarTablaPdf(prestamoId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=amortizacion-prestamo-" + prestamoId + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/{prestamoId}/excel")
    public ResponseEntity<byte[]> exportarExcel(@PathVariable Long prestamoId) {
        byte[] excel = excelService.generarTablaExcel(prestamoId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=amortizacion-prestamo-" + prestamoId + ".xlsx")
                .contentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }



}