package com.prestamos.sistema_prestamos.modules.rh.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalarioDTO {
    private Long id;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "1.00", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    private String descripcion;

    @NotNull(message = "La fecha de vigencia es obligatoria")
    private LocalDate fechaVigencia;
}