package com.prestamos.sistema_prestamos.modules.empleados.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "empleado_beneficiarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoBeneficiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String parentesco;

    @Column(precision = 5, scale = 2)
    private BigDecimal porcentaje;

    private String telefono;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
}