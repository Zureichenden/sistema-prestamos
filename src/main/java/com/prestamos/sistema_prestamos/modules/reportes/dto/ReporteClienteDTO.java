package com.prestamos.sistema_prestamos.modules.reportes.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteClienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rfc;
    private String telefono;
    private LocalDateTime fechaRegistro;
}