package com.prestamos.sistema_prestamos.modules.rh.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuestoDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @Min(value = 1, message = "El nivel mínimo es 1")
    private Integer nivel;
}