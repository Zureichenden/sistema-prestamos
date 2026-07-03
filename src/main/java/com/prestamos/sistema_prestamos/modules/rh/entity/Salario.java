package com.prestamos.sistema_prestamos.modules.rh.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "salarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    private String descripcion;

    @Column(name = "fecha_vigencia", nullable = false)
    private LocalDate fechaVigencia;
}