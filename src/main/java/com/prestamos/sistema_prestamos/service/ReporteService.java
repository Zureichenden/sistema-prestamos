package com.prestamos.sistema_prestamos.service;

import com.prestamos.sistema_prestamos.dto.*;
import com.prestamos.sistema_prestamos.entity.*;
import com.prestamos.sistema_prestamos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.time.*;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ClienteRepository clienteRepository;
    private final PrestamoRepository prestamoRepository;
    private final PagoRepository pagoRepository;

    public Page<ReporteClienteDTO> reporteClientes(LocalDate inicio, LocalDate fin, int pagina, int tamanio) {
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(23, 59, 59);
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("fechaRegistro").descending());

        return clienteRepository
                .findByFechaRegistroBetween(inicioDateTime, finDateTime, pageable)
                .map(this::toClienteDTO);
    }

    public Page<PrestamoResponseDTO> reportePrestamos(LocalDate inicio, LocalDate fin, int pagina, int tamanio) {
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(23, 59, 59);
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("createdAt").descending());

        return prestamoRepository
                .findByCreatedAtBetween(inicioDateTime, finDateTime, pageable)
                .map(this::toPrestamoDTO);
    }

    public Page<ReportePagoDTO> reportePagos(LocalDate inicio, LocalDate fin, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("fechaPago").descending());

        return pagoRepository
                .findByFechaPagoBetween(inicio, fin, pageable)
                .map(this::toPagoDTO);
    }

    private ReporteClienteDTO toClienteDTO(Cliente c) {
        return ReporteClienteDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .apellido(c.getApellido())
                .email(c.getEmail())
                .rfc(c.getRfc())
                .telefono(c.getTelefono())
                .fechaRegistro(c.getFechaRegistro())
                .build();
    }

    private PrestamoResponseDTO toPrestamoDTO(Prestamo p) {
        return PrestamoResponseDTO.builder()
                .id(p.getId())
                .clienteId(p.getCliente().getId())
                .clienteNombre(p.getCliente().getNombre() + " " + p.getCliente().getApellido())
                .monto(p.getMonto())
                .tasaInteres(p.getTasaInteres())
                .numPagos(p.getNumPagos())
                .fechaInicio(p.getFechaInicio())
                .estatus(p.getEstatus().name())
                .createdAt(p.getCreatedAt())
                .build();
    }

    private ReportePagoDTO toPagoDTO(Pago p) {
        return ReportePagoDTO.builder()
                .id(p.getId())
                .prestamoId(p.getPrestamo().getId())
                .clienteNombre(p.getPrestamo().getCliente().getNombre() + " " +
                        p.getPrestamo().getCliente().getApellido())
                .montoPagado(p.getMontoPagado())
                .fechaPago(p.getFechaPago())
                .tipoPago(p.getTipoPago().name())
                .observaciones(p.getObservaciones())
                .createdAt(p.getCreatedAt())
                .build();
    }
}