package com.prestamos.sistema_prestamos.modules.reportes.controller;

import com.prestamos.sistema_prestamos.shared.Bitacora;
import com.prestamos.sistema_prestamos.shared.BitacoraService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bitacora")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class BitacoraController {

    private final BitacoraService bitacoraService;

    @GetMapping
    public ResponseEntity<Page<Bitacora>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(bitacoraService.listar(pagina, tamanio));
    }

    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<Page<Bitacora>> listarPorUsuario(
            @PathVariable String usuario,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(bitacoraService.listarPorUsuario(usuario, pagina, tamanio));
    }

    @GetMapping("/entidad/{entidad}")
    public ResponseEntity<Page<Bitacora>> listarPorEntidad(
            @PathVariable String entidad,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(bitacoraService.listarPorEntidad(entidad, pagina, tamanio));
    }
}