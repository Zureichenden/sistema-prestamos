package com.prestamos.sistema_prestamos.modules.empleados.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "empleado_telefonos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoTelefono {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(nullable = false)
    private String numero;

    @Enumerated(EnumType.STRING)
    private TipoTelefono tipo;

    private boolean principal;

    public enum TipoTelefono {
        CELULAR, CASA, TRABAJO
    }
}