package com.prestamos.sistema_prestamos.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {

    // Tarjetas resumen
    private Long totalClientes;
    private Long prestamosActivos;
    private Long prestamosLiquidados;
    private Long totalPrestamos;
    private BigDecimal montoTotalPrestado;
    private BigDecimal totalRecaudado;
    private Long pagosMes;
    private BigDecimal montoRecaudadoMes;

    // Gráfica préstamos por mes (últimos 6 meses)
    private List<String> meses;
    private List<Long> prestamosPorMes;
    private List<BigDecimal> montoPorMes;

    // Gráfica pagos por mes (últimos 6 meses)
    private List<Long> pagosPorMes;
}