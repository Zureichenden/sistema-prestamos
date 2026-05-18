package com.prestamos.sistema_prestamos.controller;

import com.prestamos.sistema_prestamos.dto.AuthRequestDTO;
import com.prestamos.sistema_prestamos.dto.AuthResponseDTO;
import com.prestamos.sistema_prestamos.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import com.prestamos.sistema_prestamos.entity.Rol;
import com.prestamos.sistema_prestamos.entity.Usuario;
import com.prestamos.sistema_prestamos.repository.UsuarioRepository;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Obtener roles del usuario
            Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Set<String> roles = usuario.getRoles().stream()
                    .map(Rol::getNombre)
                    .collect(Collectors.toSet());

            String token = jwtUtil.generarToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponseDTO(token, request.getUsername(), roles));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
        }
    }


}