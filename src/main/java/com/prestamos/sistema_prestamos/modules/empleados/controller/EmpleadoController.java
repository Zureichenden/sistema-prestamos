package com.prestamos.sistema_prestamos.modules.empleados.controller;

import com.prestamos.sistema_prestamos.modules.empleados.dto.*;
import com.prestamos.sistema_prestamos.modules.empleados.service.EmpleadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    // ── EMPLEADOS ────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<EmpleadoResponseDTO> crear(@Valid @RequestBody EmpleadoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<Page<EmpleadoResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(empleadoService.listar(pagina, tamanio));
    }

    @GetMapping("/activos")
    public ResponseEntity<Page<EmpleadoResponseDTO>> listarActivos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(empleadoService.listarActivos(pagina, tamanio));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EmpleadoRequestDTO dto) {
        return ResponseEntity.ok(empleadoService.actualizar(id, dto));
    }

    @PutMapping("/{id}/puesto/{puestoId}")
    public ResponseEntity<EmpleadoResponseDTO> cambiarPuesto(
            @PathVariable Long id,
            @PathVariable Long puestoId) {
        return ResponseEntity.ok(empleadoService.cambiarPuesto(id, puestoId));
    }

    @PutMapping("/{id}/departamento/{departamentoId}")
    public ResponseEntity<EmpleadoResponseDTO> cambiarDepartamento(
            @PathVariable Long id,
            @PathVariable Long departamentoId) {
        return ResponseEntity.ok(empleadoService.cambiarDepartamento(id, departamentoId));
    }

    @PutMapping("/{id}/salario/{salarioId}")
    public ResponseEntity<EmpleadoResponseDTO> cambiarSalario(
            @PathVariable Long id,
            @PathVariable Long salarioId) {
        return ResponseEntity.ok(empleadoService.cambiarSalario(id, salarioId));
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<EmpleadoResponseDTO> darBaja(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.darBaja(id));
    }

    @PutMapping("/{id}/reactivar")
    public ResponseEntity<EmpleadoResponseDTO> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.reactivar(id));
    }

    // ── DIRECCIONES ──────────────────────────────────────────

    @PostMapping("/{id}/direcciones")
    public ResponseEntity<EmpleadoDireccionDTO> agregarDireccion(
            @PathVariable Long id,
            @Valid @RequestBody EmpleadoDireccionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(empleadoService.agregarDireccion(id, dto));
    }

    @GetMapping("/{id}/direcciones")
    public ResponseEntity<List<EmpleadoDireccionDTO>> listarDirecciones(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.listarDirecciones(id));
    }

    @DeleteMapping("/direcciones/{id}")
    public ResponseEntity<Void> eliminarDireccion(@PathVariable Long id) {
        empleadoService.eliminarDireccion(id);
        return ResponseEntity.noContent().build();
    }

    // ── TELÉFONOS ────────────────────────────────────────────

    @PostMapping("/{id}/telefonos")
    public ResponseEntity<EmpleadoTelefonoDTO> agregarTelefono(
            @PathVariable Long id,
            @Valid @RequestBody EmpleadoTelefonoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(empleadoService.agregarTelefono(id, dto));
    }

    @GetMapping("/{id}/telefonos")
    public ResponseEntity<List<EmpleadoTelefonoDTO>> listarTelefonos(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.listarTelefonos(id));
    }

    @DeleteMapping("/telefonos/{id}")
    public ResponseEntity<Void> eliminarTelefono(@PathVariable Long id) {
        empleadoService.eliminarTelefono(id);
        return ResponseEntity.noContent().build();
    }

    // ── BENEFICIARIOS ────────────────────────────────────────

    @PostMapping("/{id}/beneficiarios")
    public ResponseEntity<EmpleadoBeneficiarioDTO> agregarBeneficiario(
            @PathVariable Long id,
            @Valid @RequestBody EmpleadoBeneficiarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(empleadoService.agregarBeneficiario(id, dto));
    }

    @GetMapping("/{id}/beneficiarios")
    public ResponseEntity<List<EmpleadoBeneficiarioDTO>> listarBeneficiarios(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.listarBeneficiarios(id));
    }

    @DeleteMapping("/beneficiarios/{id}")
    public ResponseEntity<Void> eliminarBeneficiario(@PathVariable Long id) {
        empleadoService.eliminarBeneficiario(id);
        return ResponseEntity.noContent().build();
    }

    // ── BITÁCORA ─────────────────────────────────────────────

    @GetMapping("/{id}/bitacora")
    public ResponseEntity<List<BitacoraEmpleadoDTO>> listarBitacora(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.listarBitacora(id));
    }

    @GetMapping("/bitacora/todos")
    public ResponseEntity<Page<BitacoraEmpleadoDTO>> listarTodaBitacora(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(empleadoService.listarTodaBitacora(pagina, tamanio));
    }
}