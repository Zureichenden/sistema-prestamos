package com.prestamos.sistema_prestamos.modules.empleados.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bitacora_empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BitacoraEmpleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimiento tipoMovimiento;

    private String descripcion;

    @Column(name = "valor_anterior")
    private String valorAnterior;

    @Column(name = "valor_nuevo")
    private String valorNuevo;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @PrePersist
    public void prePersist() {
        this.fechaHora = LocalDateTime.now();
    }

    public enum TipoMovimiento {
        ALTA,
        BAJA,
        REACTIVACION,
        CAMBIO_PUESTO,
        CAMBIO_DEPARTAMENTO,
        CAMBIO_SALARIO,
        CAMBIO_DATOS_PERSONALES
    }
}