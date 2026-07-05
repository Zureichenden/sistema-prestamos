package com.prestamos.sistema_prestamos.modules.empleados.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BitacoraEmpleadoDTO {
    private Long id;
    private Long empleadoId;
    private String empleadoNombre;
    private String tipoMovimiento;
    private String descripcion;
    private String valorAnterior;
    private String valorNuevo;
    private Long usuarioId;
    private LocalDateTime fechaHora;
}