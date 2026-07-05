package com.prestamos.sistema_prestamos.modules.empleados.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "empleado_documentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDocumento tipo;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "fecha_subida")
    private LocalDateTime fechaSubida;

    @PrePersist
    public void prePersist() {
        this.fechaSubida = LocalDateTime.now();
    }

    public enum TipoDocumento {
        INE, CURP, NSS, COMPROBANTE_DOMICILIO,
        ACTA_NACIMIENTO, CONTRATO, OTRO
    }
}