package com.prestamos.sistema_prestamos.modules.rh.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "puestos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Puesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    private Integer nivel;
}