package com.prestamos.sistema_prestamos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "amortizaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amortizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestamo_id", nullable = false)
    private Prestamo prestamo;

    @Column(name = "num_pago", nullable = false)
    private Integer numPago; // 1, 2, 3... N

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal capital;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal interes;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal cuota; // capital + interes

    @Column(name = "saldo_restante", precision = 12, scale = 2)
    private BigDecimal saldoRestante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstatusAmortizacion estatus;

    public enum EstatusAmortizacion {
        PENDIENTE, PAGADO, VENCIDO
    }
}