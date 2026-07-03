package com.prestamos.sistema_prestamos.modules.rh.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartamentoDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;
    private Long jefeId;
    private String jefeNombre;
}