package com.prestamos.sistema_prestamos.modules.auth.service;

import com.prestamos.sistema_prestamos.modules.auth.dto.CambiarPasswordDTO;
import com.prestamos.sistema_prestamos.modules.auth.dto.UsuarioRequestDTO;
import com.prestamos.sistema_prestamos.modules.auth.dto.UsuarioResponseDTO;
import com.prestamos.sistema_prestamos.modules.auth.entity.Rol;
import com.prestamos.sistema_prestamos.modules.auth.entity.Usuario;
import com.prestamos.sistema_prestamos.modules.auth.repository.RolRepository;
import com.prestamos.sistema_prestamos.modules.auth.repository.UsuarioRepository;
import com.prestamos.sistema_prestamos.shared.BitacoraService;
import com.prestamos.sistema_prestamos.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final BitacoraService bitacoraService;

    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername()))
            throw new RuntimeException("El username ya está en uso");
        if (usuarioRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("El email ya está en uso");

        Set<Rol> roles = dto.getRoles().stream()
                .map(nombre -> rolRepository.findByNombre(nombre)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombre)))
                .collect(Collectors.toSet());

        Usuario usuario = Usuario.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .roles(roles)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        bitacoraService.registrar("CREAR", "USUARIO", guardado.getId(),
                "Usuario creado: " + guardado.getUsername());
        return toDTO(guardado);
    }

    public Page<UsuarioResponseDTO> listar(int pagina, int tamanio) {
        return usuarioRepository.findAll(
                        PageRequest.of(pagina, tamanio, Sort.by("id").descending()))
                .map(this::toDTO);
    }

    public UsuarioResponseDTO obtenerPorId(Long id) {
        return toDTO(usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
    }

    @Transactional
    public UsuarioResponseDTO actualizarRoles(Long id, Set<String> nombresRoles) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Set<Rol> roles = nombresRoles.stream()
                .map(nombre -> rolRepository.findByNombre(nombre)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombre)))
                .collect(Collectors.toSet());

        usuario.setRoles(roles);
        bitacoraService.registrar("ACTUALIZAR", "USUARIO", id,
                "Roles actualizados: " + nombresRoles);
        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO toggleActivo(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(!usuario.isActivo());
        bitacoraService.registrar(usuario.isActivo() ? "ACTIVAR" : "DESACTIVAR",
                "USUARIO", id, "Usuario " + (usuario.isActivo() ? "activado" : "desactivado"));
        return toDTO(usuarioRepository.save(usuario));
    }

    private UsuarioResponseDTO toDTO(Usuario u) {
        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .activo(u.isActivo())
                .roles(u.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet()))
                .createdAt(u.getCreatedAt())
                .build();
    }

    @Transactional
    public void cambiarPassword(String username, CambiarPasswordDTO dto) {
        if (!dto.getPasswordNueva().equals(dto.getConfirmarPassword()))
            throw new BusinessException("Las contraseñas no coinciden");

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if (!passwordEncoder.matches(dto.getPasswordActual(), usuario.getPassword()))
            throw new BusinessException("La contraseña actual es incorrecta");

        usuario.setPassword(passwordEncoder.encode(dto.getPasswordNueva()));
        usuarioRepository.save(usuario);
        bitacoraService.registrar("ACTUALIZAR", "USUARIO", usuario.getId(),
                "Contraseña actualizada para: " + username);
    }


}