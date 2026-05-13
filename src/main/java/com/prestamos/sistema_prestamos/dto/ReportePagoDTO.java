package com.prestamos.sistema_prestamos.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportePagoDTO {
    private Long id;
    private Long prestamoId;
    private String clienteNombre;
    private BigDecimal montoPagado;
    private LocalDate fechaPago;
    private String tipoPago;
    private String observaciones;
    private LocalDateTime createdAt;
}