package com.prestamos.sistema_prestamos.modules.empleados.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoDocumentoDTO {
    private Long id;
    private Long empleadoId;
    private String tipo;
    private String nombreArchivo;
    private LocalDateTime fechaSubida;
}