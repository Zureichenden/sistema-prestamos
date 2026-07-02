package com.prestamos.sistema_prestamos.modules.prestamos.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponseDTO {
    private Long id;
    private Long prestamoId;
    private Long amortizacionId;
    private Integer numPago;
    private BigDecimal montoPagado;
    private LocalDate fechaPago;
    private LocalDateTime createdAt;
    private String tipoPago;
    private String observaciones;
    private String estatusAmortizacion;
}