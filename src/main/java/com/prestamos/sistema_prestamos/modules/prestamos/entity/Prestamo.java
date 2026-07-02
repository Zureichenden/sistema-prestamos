package com.prestamos.sistema_prestamos.modules.prestamos.entity;

import com.prestamos.sistema_prestamos.modules.clientes.entity.Cliente;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prestamos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "tasa_interes", nullable = false, precision = 5, scale = 2)
    private BigDecimal tasaInteres; // Ej: 12.00 = 12% anual

    @Column(name = "num_pagos", nullable = false)
    private Integer numPagos; // Número de mensualidades

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstatusPrestamo estatus;

    @Column(name = "contrato_pdf")
    private String contratoPdf;

    @Column(name = "contrato_subido")
    private Boolean contratoSubido;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.estatus = EstatusPrestamo.ACTIVO;
    }

    public enum EstatusPrestamo {
        ACTIVO, LIQUIDADO, VENCIDO
    }
}