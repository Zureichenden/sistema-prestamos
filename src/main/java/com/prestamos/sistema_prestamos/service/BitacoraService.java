package com.prestamos.sistema_prestamos.service;

import com.prestamos.sistema_prestamos.entity.Bitacora;
import com.prestamos.sistema_prestamos.repository.BitacoraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BitacoraService {

    private final BitacoraRepository bitacoraRepository;

    @Async
    public void registrar(String accion, String entidad, Long entidadId, String detalle) {
        try {
            String usuario = obtenerUsuarioActual();
            Bitacora registro = Bitacora.builder()
                    .usuario(usuario)
                    .accion(accion)
                    .entidad(entidad)
                    .entidadId(entidadId)
                    .detalle(detalle)
                    .build();
            bitacoraRepository.save(registro);
            log.info("Bitácora: [{}] {} #{} — {}", usuario, accion, entidadId, detalle);
        } catch (Exception e) {
            log.error("Error al registrar bitácora: {}", e.getMessage());
        }
    }

    public Page<Bitacora> listar(int pagina, int tamanio) {
        return bitacoraRepository.findAllByOrderByFechaHoraDesc(
                PageRequest.of(pagina, tamanio, Sort.by("fechaHora").descending()));
    }

    public Page<Bitacora> listarPorUsuario(String usuario, int pagina, int tamanio) {
        return bitacoraRepository.findByUsuario(usuario,
                PageRequest.of(pagina, tamanio, Sort.by("fechaHora").descending()));
    }

    public Page<Bitacora> listarPorEntidad(String entidad, int pagina, int tamanio) {
        return bitacoraRepository.findByEntidad(entidad,
                PageRequest.of(pagina, tamanio, Sort.by("fechaHora").descending()));
    }

    private String obtenerUsuarioActual() {
        try {
            return SecurityContextHolder.getContext()
                    .getAuthentication().getName();
        } catch (Exception e) {
            return "sistema";
        }
    }
}