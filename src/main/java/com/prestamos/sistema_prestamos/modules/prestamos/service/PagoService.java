package com.prestamos.sistema_prestamos.modules.prestamos.service;

import com.prestamos.sistema_prestamos.modules.prestamos.dto.PagoRequestDTO;
import com.prestamos.sistema_prestamos.modules.prestamos.dto.PagoResponseDTO;
import com.prestamos.sistema_prestamos.modules.prestamos.entity.Amortizacion;
import com.prestamos.sistema_prestamos.modules.prestamos.entity.Pago;
import com.prestamos.sistema_prestamos.modules.prestamos.entity.Prestamo;
import com.prestamos.sistema_prestamos.modules.prestamos.repository.AmortizacionRepository;
import com.prestamos.sistema_prestamos.modules.prestamos.repository.PagoRepository;
import com.prestamos.sistema_prestamos.modules.prestamos.repository.PrestamoRepository;
import com.prestamos.sistema_prestamos.shared.BitacoraService;
import com.prestamos.sistema_prestamos.shared.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PrestamoRepository prestamoRepository;
    private final AmortizacionRepository amortizacionRepository;
    private final BitacoraService bitacoraService;
    private final EmailService emailService;

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

        // Enviar email de confirmación de pago
        emailService.enviarConfirmacionPago(
                prestamo.getCliente().getEmail(),
                prestamo.getCliente().getNombre(),
                dto.getMontoPagado(),
                amortizacion.getNumPago(),
                amortizacion.getSaldoRestante(),
                dto.getFechaPago()
        );

        bitacoraService.registrar("CREAR", "PAGO", pago.getId(),
                "Pago registrado: $" + dto.getMontoPagado() + " para préstamo #" + dto.getPrestamoId());

        return toDTO(pago, amortizacion);
    }

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