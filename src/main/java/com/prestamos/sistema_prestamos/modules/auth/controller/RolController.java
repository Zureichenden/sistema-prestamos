package com.prestamos.sistema_prestamos.modules.auth.controller;

import com.prestamos.sistema_prestamos.modules.auth.entity.Rol;
import com.prestamos.sistema_prestamos.modules.auth.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor

public class RolController {

    private final RolService rolService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Rol>> listar() {
        return ResponseEntity.ok(rolService.listar());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Rol> crear(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(rolService.crear(
                body.get("nombre"),
                body.get("descripcion")));
    }
}