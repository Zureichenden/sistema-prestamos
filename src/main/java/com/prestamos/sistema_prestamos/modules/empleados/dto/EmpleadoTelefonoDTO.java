package com.prestamos.sistema_prestamos.modules.empleados.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoTelefonoDTO {
    private Long id;
    private Long empleadoId;

    @NotBlank(message = "El número es obligatorio")
    private String numero;

    private String tipo;
    private boolean principal;
}