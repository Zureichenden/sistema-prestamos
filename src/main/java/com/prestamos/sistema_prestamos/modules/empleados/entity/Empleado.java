package com.prestamos.sistema_prestamos.modules.empleados.entity;

import com.prestamos.sistema_prestamos.modules.rh.entity.Departamento;
import com.prestamos.sistema_prestamos.modules.rh.entity.Puesto;
import com.prestamos.sistema_prestamos.modules.rh.entity.Salario;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(nullable = false, unique = true, length = 13)
    private String rfc;

    @Column(unique = true, length = 18)
    private String curp;

    @Column(unique = true)
    private String nss;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    private boolean activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puesto_id")
    private Puesto puesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salario_id")
    private Salario salario;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.activo = true;
    }
}