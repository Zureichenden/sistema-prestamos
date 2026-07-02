package com.prestamos.sistema_prestamos.modules.prestamos.controller;

import com.prestamos.sistema_prestamos.modules.prestamos.dto.PagoRequestDTO;
import com.prestamos.sistema_prestamos.modules.prestamos.dto.PagoResponseDTO;
import com.prestamos.sistema_prestamos.modules.prestamos.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponseDTO> registrarPago(@Valid @RequestBody PagoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.registrarPago(dto));
    }

    @GetMapping("/prestamo/{prestamoId}")
    public ResponseEntity<Page<PagoResponseDTO>> listarPorPrestamo(
            @PathVariable Long prestamoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(pagoService.listarPorPrestamo(prestamoId, pagina, tamanio));
    }


}