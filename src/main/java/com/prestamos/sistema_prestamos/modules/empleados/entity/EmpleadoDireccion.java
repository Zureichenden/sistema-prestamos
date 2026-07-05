package com.prestamos.sistema_prestamos.modules.empleados.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "empleado_direcciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoDireccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    private String calle;
    private String colonia;
    private String ciudad;
    private String estado;

    @Column(name = "codigo_postal", length = 5)
    private String codigoPostal;

    @Enumerated(EnumType.STRING)
    private TipoDireccion tipo;

    private boolean principal;

    public enum TipoDireccion {
        CASA, TRABAJO
    }
}