package com.prestamos.sistema_prestamos.service;

import com.prestamos.sistema_prestamos.modules.prestamos.dto.PagoRequestDTO;
import com.prestamos.sistema_prestamos.modules.prestamos.dto.PagoResponseDTO;
import com.prestamos.sistema_prestamos.modules.clientes.entity.Cliente;
import com.prestamos.sistema_prestamos.modules.prestamos.entity.Amortizacion;
import com.prestamos.sistema_prestamos.modules.prestamos.entity.Pago;
import com.prestamos.sistema_prestamos.modules.prestamos.entity.Prestamo;
import com.prestamos.sistema_prestamos.modules.prestamos.repository.AmortizacionRepository;
import com.prestamos.sistema_prestamos.modules.prestamos.repository.PagoRepository;
import com.prestamos.sistema_prestamos.modules.prestamos.repository.PrestamoRepository;
import com.prestamos.sistema_prestamos.modules.prestamos.service.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock private PagoRepository pagoRepository;
    @Mock private PrestamoRepository prestamoRepository;
    @Mock private AmortizacionRepository amortizacionRepository;

    @InjectMocks
    private PagoService pagoService;

    private Prestamo prestamo;
    private Amortizacion amortizacion;
    private PagoRequestDTO pagoDTO;

    @BeforeEach
    void setUp() {
        Cliente cliente = Cliente.builder().id(1L).nombre("Juan").build();

        prestamo = Prestamo.builder()
                .id(1L)
                .cliente(cliente)
                .monto(new BigDecimal("50000"))
                .tasaInteres(new BigDecimal("12"))
                .numPagos(12)
                .estatus(Prestamo.EstatusPrestamo.ACTIVO)
                .build();

        amortizacion = Amortizacion.builder()
                .id(1L)
                .prestamo(prestamo)
                .numPago(1)
                .fechaVencimiento(LocalDate.of(2026, 6, 1))
                .capital(new BigDecimal("3924.94"))
                .interes(new BigDecimal("500.00"))
                .cuota(new BigDecimal("4424.94"))
                .saldoRestante(new BigDecimal("46075.06"))
                .estatus(Amortizacion.EstatusAmortizacion.PENDIENTE)
                .build();

        pagoDTO = new PagoRequestDTO();
        pagoDTO.setPrestamoId(1L);
        pagoDTO.setAmortizacionId(1L);
        pagoDTO.setMontoPagado(new BigDecimal("4424.94"));
        pagoDTO.setFechaPago(LocalDate.of(2026, 5, 28));
        pagoDTO.setObservaciones("Primer pago");
    }

    @Test
    void registrarPago_pagoNormal_exitoso() {
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        when(amortizacionRepository.findById(1L)).thenReturn(Optional.of(amortizacion));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> {
            Pago p = inv.getArgument(0);
            p.setAmortizacion(amortizacion);
            return p;
        });
        when(amortizacionRepository.findByPrestamoIdOrderByNumPago(1L)).thenReturn(List.of(amortizacion));

        PagoResponseDTO resultado = pagoService.registrarPago(pagoDTO);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("4424.94"), resultado.getMontoPagado());
        assertEquals("NORMAL", resultado.getTipoPago());
        verify(amortizacionRepository, times(1)).save(any());
    }

    @Test
    void registrarPago_prestamoLiquidado_lanzaExcepcion() {
        prestamo.setEstatus(Prestamo.EstatusPrestamo.LIQUIDADO);
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.registrarPago(pagoDTO));

        assertEquals("El préstamo ya está liquidado", ex.getMessage());
    }

    @Test
    void registrarPago_amortizacionYaPagada_lanzaExcepcion() {
        amortizacion.setEstatus(Amortizacion.EstatusAmortizacion.PAGADO);
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        when(amortizacionRepository.findById(1L)).thenReturn(Optional.of(amortizacion));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.registrarPago(pagoDTO));

        assertEquals("Esta amortización ya fue pagada", ex.getMessage());
    }

    @Test
    void registrarPago_montoParcial_tipoParcial() {
        pagoDTO.setMontoPagado(new BigDecimal("2000.00"));
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        when(amortizacionRepository.findById(1L)).thenReturn(Optional.of(amortizacion));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> {
            Pago p = inv.getArgument(0);
            p.setAmortizacion(amortizacion);
            return p;
        });
        when(amortizacionRepository.findByPrestamoIdOrderByNumPago(1L)).thenReturn(List.of(amortizacion));

        PagoResponseDTO resultado = pagoService.registrarPago(pagoDTO);

        assertEquals("PARCIAL", resultado.getTipoPago());
    }
}