package com.prestamos.sistema_prestamos.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoRequestDTO {

    @NotNull(message = "El préstamo es obligatorio")
    private Long prestamoId;

    @NotNull(message = "La amortización es obligatoria")
    private Long amortizacionId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal montoPagado;

    @NotNull(message = "La fecha de pago es obligatoria")
    private LocalDate fechaPago;

    private String observaciones;
}