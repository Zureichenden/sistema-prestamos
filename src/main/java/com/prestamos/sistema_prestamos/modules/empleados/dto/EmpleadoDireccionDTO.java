package com.prestamos.sistema_prestamos.modules.empleados.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoDireccionDTO {
    private Long id;
    private Long empleadoId;

    @NotBlank(message = "La calle es obligatoria")
    private String calle;

    private String colonia;
    private String ciudad;
    private String estado;

    @Size(min = 5, max = 5, message = "El código postal debe tener 5 dígitos")
    private String codigoPostal;

    private String tipo;
    private boolean principal;
}