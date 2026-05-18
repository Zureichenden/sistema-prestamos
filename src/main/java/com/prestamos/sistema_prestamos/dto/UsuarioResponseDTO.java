package com.prestamos.sistema_prestamos.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private String nombre;
    private String email;
    private boolean activo;
    private Set<String> roles;
    private LocalDateTime createdAt;
}