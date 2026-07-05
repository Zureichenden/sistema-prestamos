package com.prestamos.sistema_prestamos.modules.empleados.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private LocalDate fechaNacimiento;
    private String rfc;
    private String curp;
    private String nss;
    private LocalDate fechaIngreso;
    private boolean activo;
    private Long puestoId;
    private String puestoNombre;
    private Long departamentoId;
    private String departamentoNombre;
    private Long salarioId;
    private BigDecimal salarioMonto;
    private LocalDateTime createdAt;
}