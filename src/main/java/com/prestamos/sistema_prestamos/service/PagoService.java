package com.prestamos.sistema_prestamos.service;

import com.prestamos.sistema_prestamos.dto.PagoRequestDTO;
import com.prestamos.sistema_prestamos.dto.PagoResponseDTO;
import com.prestamos.sistema_prestamos.entity.*;
import com.prestamos.sistema_prestamos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PrestamoRepository prestamoRepository;
    private final AmortizacionRepository amortizacionRepository;
    private final BitacoraService bitacoraService;

    @Transactional
    public PagoResponseDTO registrarPago(PagoRequestDTO dto) {
        // Validar préstamo
        Prestamo prestamo = prestamoRepository.findById(dto.getPrestamoId())
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        if (prestamo.getEstatus() == Prestamo.EstatusPrestamo.LIQUIDADO)
            throw new RuntimeException("El préstamo ya está liquidado");

        // Validar amortización
        Amortizacion amortizacion = amortizacionRepository.findById(dto.getAmortizacionId())
                .orElseThrow(() -> new RuntimeException("Amortización no encontrada"));

        if (amortizacion.getEstatus() == Amortizacion.EstatusAmortizacion.PAGADO)
            throw new RuntimeException("Esta amortización ya fue pagada");

        // Determinar tipo de pago
        Pago.TipoPago tipoPago;
        int comparacion = dto.getMontoPagado().compareTo(amortizacion.getCuota());
        if (comparacion < 0) {
            tipoPago = Pago.TipoPago.PARCIAL;
        } else if (dto.getFechaPago().isBefore(amortizacion.getFechaVencimiento())) {
            tipoPago = Pago.TipoPago.ADELANTADO;
        } else {
            tipoPago = Pago.TipoPago.NORMAL;
        }

        // Registrar pago
        Pago pago = Pago.builder()
                .prestamo(prestamo)
                .amortizacion(amortizacion)
                .montoPagado(dto.getMontoPagado())
                .fechaPago(dto.getFechaPago())
                .tipoPago(tipoPago)
                .observaciones(dto.getObservaciones())
                .build();


        pagoRepository.save(pago);
        bitacoraService.registrar("CREAR", "PAGO", pago.getId(),
                "Pago registrado: $" + dto.getMontoPagado() + " para préstamo #" + dto.getPrestamoId());

        // Marcar amortización como pagada (si no es parcial)
        if (tipoPago != Pago.TipoPago.PARCIAL) {
            amortizacion.setEstatus(Amortizacion.EstatusAmortizacion.PAGADO);
            amortizacionRepository.save(amortizacion);
        }

        // Verificar si el préstamo quedó liquidado
        long pendientes = amortizacionRepository
                .findByPrestamoIdOrderByNumPago(prestamo.getId())
                .stream()
                .filter(a -> a.getEstatus() == Amortizacion.EstatusAmortizacion.PENDIENTE)
                .count();

        if (pendientes == 0) {
            prestamo.setEstatus(Prestamo.EstatusPrestamo.LIQUIDADO);
            prestamoRepository.save(prestamo);
        }

        return toDTO(pago, amortizacion);
    }

    /*
    public List<PagoResponseDTO> listarPorPrestamo(Long prestamoId) {
        return pagoRepository.findByPrestamoId(prestamoId)
                .stream()
                .map(p -> toDTO(p, p.getAmortizacion()))
                .collect(Collectors.toList());
    }

     */

    public Page<PagoResponseDTO> listarPorPrestamo(Long prestamoId, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("id").descending());
        return pagoRepository.findByPrestamoId(prestamoId, pageable)
                .map(p -> toDTO(p, p.getAmortizacion()));
    }



    private PagoResponseDTO toDTO(Pago pago, Amortizacion amortizacion) {
        return PagoResponseDTO.builder()
                .id(pago.getId())
                .prestamoId(pago.getPrestamo().getId())
                .amortizacionId(amortizacion.getId())
                .numPago(amortizacion.getNumPago())
                .montoPagado(pago.getMontoPagado())
                .fechaPago(pago.getFechaPago())
                .createdAt(pago.getCreatedAt())
                .tipoPago(pago.getTipoPago().name())
                .observaciones(pago.getObservaciones())
                .estatusAmortizacion(amortizacion.getEstatus().name())
                .build();
    }
}