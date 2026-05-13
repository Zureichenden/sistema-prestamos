package com.prestamos.sistema_prestamos.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestamoResponseDTO {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private BigDecimal monto;
    private BigDecimal tasaInteres;
    private Integer numPagos;
    private LocalDate fechaInicio;
    private String estatus;
    private LocalDateTime createdAt;
}