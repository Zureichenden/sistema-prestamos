package com.prestamos.sistema_prestamos.modules.empleados.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoBeneficiarioDTO {
    private Long id;
    private Long empleadoId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El parentesco es obligatorio")
    private String parentesco;

    @DecimalMin(value = "0.01", message = "El porcentaje debe ser mayor a 0")
    @DecimalMax(value = "100.00", message = "El porcentaje no puede superar 100")
    private BigDecimal porcentaje;

    private String telefono;
    private LocalDate fechaNacimiento;
}