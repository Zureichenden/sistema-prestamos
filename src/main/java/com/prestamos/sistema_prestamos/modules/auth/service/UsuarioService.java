package com.prestamos.sistema_prestamos.modules.auth.service;

import com.prestamos.sistema_prestamos.modules.auth.dto.*;
import com.prestamos.sistema_prestamos.modules.auth.entity.*;
import com.prestamos.sistema_prestamos.modules.auth.repository.*;
import com.prestamos.sistema_prestamos.modules.empleados.entity.Empleado;
import com.prestamos.sistema_prestamos.modules.empleados.repository.EmpleadoRepository;
import com.prestamos.sistema_prestamos.shared.exception.BusinessException;
import com.prestamos.sistema_prestamos.shared.BitacoraService;
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
    private final EmpleadoRepository empleadoRepository;

    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername()))
            throw new BusinessException("El username ya está en uso");
        if (usuarioRepository.existsByEmail(dto.getEmail()))
            throw new BusinessException("El email ya está en uso");

        Set<Rol> roles = dto.getRoles().stream()
                .map(nombre -> rolRepository.findByNombre(nombre)
                        .orElseThrow(() -> new BusinessException("Rol no encontrado: " + nombre)))
                .collect(Collectors.toSet());

        Empleado empleado = null;
        if (dto.getEmpleadoId() != null) {
            empleado = empleadoRepository.findById(dto.getEmpleadoId())
                    .orElseThrow(() -> new BusinessException("Empleado no encontrado"));
        }

        Usuario.TipoUsuario tipo = dto.getTipoUsuario() != null ?
                Usuario.TipoUsuario.valueOf(dto.getTipoUsuario()) :
                Usuario.TipoUsuario.EMPLEADO;

        Usuario usuario = Usuario.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .roles(roles)
                .tipoUsuario(tipo)
                .empleado(empleado)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        bitacoraService.registrar("CREAR", "USUARIO", guardado.getId(),
                "Usuario creado: " + guardado.getUsername());
        return toDTO(guardado);
    }

    @Transactional
    public UsuarioResponseDTO crearParaEmpleado(Long empleadoId, UsuarioRequestDTO dto) {
        dto.setEmpleadoId(empleadoId);
        dto.setTipoUsuario("EMPLEADO");

        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));

        if (usuarioRepository.existsByEmpleadoId(empleadoId))
            throw new BusinessException("Este empleado ya tiene un usuario asignado");

        return crear(dto);
    }

    public Page<UsuarioResponseDTO> listar(int pagina, int tamanio) {
        return usuarioRepository.findAll(
                        PageRequest.of(pagina, tamanio, Sort.by("id").descending()))
                .map(this::toDTO);
    }

    public UsuarioResponseDTO obtenerPorId(Long id) {
        return toDTO(usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado")));
    }

    @Transactional
    public UsuarioResponseDTO actualizarRoles(Long id, Set<String> nombresRoles) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        Set<Rol> roles = nombresRoles.stream()
                .map(nombre -> rolRepository.findByNombre(nombre)
                        .orElseThrow(() -> new BusinessException("Rol no encontrado: " + nombre)))
                .collect(Collectors.toSet());

        usuario.setRoles(roles);
        bitacoraService.registrar("ACTUALIZAR", "USUARIO", id,
                "Roles actualizados: " + nombresRoles);
        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO toggleActivo(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        usuario.setActivo(!usuario.isActivo());
        bitacoraService.registrar(usuario.isActivo() ? "ACTIVAR" : "DESACTIVAR",
                "USUARIO", id, "Usuario " + (usuario.isActivo() ? "activado" : "desactivado"));
        return toDTO(usuarioRepository.save(usuario));
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

    private UsuarioResponseDTO toDTO(Usuario u) {
        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .activo(u.isActivo())
                .tipoUsuario(u.getTipoUsuario() != null ? u.getTipoUsuario().name() : null)
                .roles(u.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet()))
                .empleadoId(u.getEmpleado() != null ? u.getEmpleado().getId() : null)
                .empleadoNombre(u.getEmpleado() != null ?
                        u.getEmpleado().getNombre() + " " + u.getEmpleado().getApellido() : null)
                .clienteId(u.getCliente() != null ? u.getCliente().getId() : null)
                .createdAt(u.getCreatedAt())
                .build();
    }

    public UsuarioResponseDTO obtenerPorEmpleadoId(Long empleadoId) {
        return usuarioRepository.findByEmpleadoId(empleadoId)
                .map(this::toDTO)
                .orElse(null);
    }


}