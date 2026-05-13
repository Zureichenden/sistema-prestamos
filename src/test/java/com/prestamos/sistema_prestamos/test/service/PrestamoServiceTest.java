package com.prestamos.sistema_prestamos.service;

import com.prestamos.sistema_prestamos.dto.PrestamoRequestDTO;
import com.prestamos.sistema_prestamos.entity.*;
import com.prestamos.sistema_prestamos.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceTest {

    @Mock private PrestamoRepository prestamoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private AmortizacionRepository amortizacionRepository;

    @InjectMocks
    private PrestamoService prestamoService;

    private Cliente cliente;
    private PrestamoRequestDTO requestDTO;
    private Prestamo prestamo;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@email.com")
                .rfc("PEJJ900101ABC")
                .telefono("6671234567")
                .build();

        requestDTO = PrestamoRequestDTO.builder()
                .clienteId(1L)
                .monto(new BigDecimal("50000"))
                .tasaInteres(new BigDecimal("12"))
                .numPagos(12)
                .fechaInicio(LocalDate.of(2026, 5, 1))
                .build();

        prestamo = Prestamo.builder()
                .id(1L)
                .cliente(cliente)
                .monto(new BigDecimal("50000"))
                .tasaInteres(new BigDecimal("12"))
                .numPagos(12)
                .fechaInicio(LocalDate.of(2026, 5, 1))
                .estatus(Prestamo.EstatusPrestamo.ACTIVO)
                .build();
    }

    @Test
    void crear_prestamoValido_retornaPrestamo() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamo);
        when(amortizacionRepository.saveAll(any())).thenReturn(List.of());

        Prestamo resultado = prestamoService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("50000"), resultado.getMonto());
        assertEquals(Prestamo.EstatusPrestamo.ACTIVO, resultado.getEstatus());
        verify(amortizacionRepository, times(1)).saveAll(any());
    }

    @Test
    void crear_clienteNoExistente_lanzaExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
        requestDTO.setClienteId(99L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> prestamoService.crear(requestDTO));

        assertEquals("Cliente no encontrado", ex.getMessage());
        verify(prestamoRepository, never()).save(any());
    }

    @Test
    void crear_generaAmortizacionesCorrectas() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamo);
        when(amortizacionRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        prestamoService.crear(requestDTO);

        verify(amortizacionRepository, times(1)).saveAll(argThat(lista -> {
            List<Amortizacion> amortizaciones = (List<Amortizacion>) lista;
            return amortizaciones.size() == 12;
        }));
    }

    @Test
    void listarPorCliente_retornaListaPrestamos() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Prestamo> page = new PageImpl<>(List.of(prestamo));

        when(prestamoRepository.findByClienteId(1L, pageable)).thenReturn(page);

        Page<Prestamo> resultado = prestamoService.listarPorCliente(1L, 0, 10);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());
        assertEquals(new BigDecimal("50000"), resultado.getContent().get(0).getMonto());
    }


}