package com.prestamos.sistema_prestamos.modules.empleados.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "Email inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    private LocalDate fechaNacimiento;

    @NotBlank(message = "El RFC es obligatorio")
    private String rfc;

    private String curp;
    private String nss;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate fechaIngreso;

    private Long puestoId;
    private Long departamentoId;
    private Long salarioId;
}