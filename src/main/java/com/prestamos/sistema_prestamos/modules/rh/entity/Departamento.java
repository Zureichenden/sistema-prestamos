package com.prestamos.sistema_prestamos.modules.rh.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "departamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    @Column(name = "jefe_id")
    private Long jefeId;
}