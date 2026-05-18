package com.prestamos.sistema_prestamos.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestamoRequestDTO {

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;
    private String contratoPdf;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "1000.00", message = "El monto mínimo es $1,000")
    private BigDecimal monto;

    @NotNull(message = "La tasa de interés es obligatoria")
    @DecimalMin(value = "0.01", message = "La tasa debe ser mayor a 0")
    private BigDecimal tasaInteres;

    @NotNull(message = "El número de pagos es obligatorio")
    @Min(value = 1, message = "Mínimo 1 pago")
    @Max(value = 60, message = "Máximo 60 pagos")
    private Integer numPagos;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;
}