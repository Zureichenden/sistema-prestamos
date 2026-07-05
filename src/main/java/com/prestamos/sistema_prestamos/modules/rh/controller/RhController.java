package com.prestamos.sistema_prestamos.modules.rh.controller;

import com.prestamos.sistema_prestamos.modules.rh.dto.*;
import com.prestamos.sistema_prestamos.modules.rh.service.RhService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rh")
@RequiredArgsConstructor

public class RhController {

    private final RhService rhService;

    // ── PUESTOS ──────────────────────────────────────────────

    @PostMapping("/puestos")
    public ResponseEntity<PuestoDTO> crearPuesto(@Valid @RequestBody PuestoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rhService.crearPuesto(dto));
    }

    @GetMapping("/puestos")
    public ResponseEntity<Page<PuestoDTO>> listarPuestos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(rhService.listarPuestos(pagina, tamanio));
    }

    @GetMapping("/puestos/todos")
    public ResponseEntity<List<PuestoDTO>> listarTodosPuestos() {
        return ResponseEntity.ok(rhService.listarTodosPuestos());
    }

    @PutMapping("/puestos/{id}")
    public ResponseEntity<PuestoDTO> actualizarPuesto(@PathVariable Long id,
                                                      @Valid @RequestBody PuestoDTO dto) {
        return ResponseEntity.ok(rhService.actualizarPuesto(id, dto));
    }

    @DeleteMapping("/puestos/{id}")
    public ResponseEntity<Void> eliminarPuesto(@PathVariable Long id) {
        rhService.eliminarPuesto(id);
        return ResponseEntity.noContent().build();
    }

    // ── DEPARTAMENTOS ────────────────────────────────────────

    @PostMapping("/departamentos")
    public ResponseEntity<DepartamentoDTO> crearDepartamento(@Valid @RequestBody DepartamentoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rhService.crearDepartamento(dto));
    }

    @GetMapping("/departamentos")
    public ResponseEntity<Page<DepartamentoDTO>> listarDepartamentos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(rhService.listarDepartamentos(pagina, tamanio));
    }

    @GetMapping("/departamentos/todos")
    public ResponseEntity<List<DepartamentoDTO>> listarTodosDepartamentos() {
        return ResponseEntity.ok(rhService.listarTodosDepartamentos());
    }

    @PutMapping("/departamentos/{id}")
    public ResponseEntity<DepartamentoDTO> actualizarDepartamento(@PathVariable Long id,
                                                                  @Valid @RequestBody DepartamentoDTO dto) {
        return ResponseEntity.ok(rhService.actualizarDepartamento(id, dto));
    }

    @DeleteMapping("/departamentos/{id}")
    public ResponseEntity<Void> eliminarDepartamento(@PathVariable Long id) {
        rhService.eliminarDepartamento(id);
        return ResponseEntity.noContent().build();
    }

    // ── SALARIOS ─────────────────────────────────────────────

    @PostMapping("/salarios")
    public ResponseEntity<SalarioDTO> crearSalario(@Valid @RequestBody SalarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rhService.crearSalario(dto));
    }

    @GetMapping("/salarios")
    public ResponseEntity<Page<SalarioDTO>> listarSalarios(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(rhService.listarSalarios(pagina, tamanio));
    }

    @GetMapping("/salarios/todos")
    public ResponseEntity<List<SalarioDTO>> listarTodosSalarios() {
        return ResponseEntity.ok(rhService.listarTodosSalarios());
    }

    @PutMapping("/salarios/{id}")
    public ResponseEntity<SalarioDTO> actualizarSalario(@PathVariable Long id,
                                                        @Valid @RequestBody SalarioDTO dto) {
        return ResponseEntity.ok(rhService.actualizarSalario(id, dto));
    }

    @DeleteMapping("/salarios/{id}")
    public ResponseEntity<Void> eliminarSalario(@PathVariable Long id) {
        rhService.eliminarSalario(id);
        return ResponseEntity.noContent().build();
    }
}