package com.prestamos.sistema_prestamos.service;

import com.prestamos.sistema_prestamos.dto.PrestamoRequestDTO;
import com.prestamos.sistema_prestamos.entity.*;
import com.prestamos.sistema_prestamos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final ClienteRepository clienteRepository;
    private final AmortizacionRepository amortizacionRepository;
    private final BitacoraService bitacoraService;

    @Transactional
    public Prestamo crear(PrestamoRequestDTO dto) {
        if (dto.getContratoPdf() == null || dto.getContratoPdf().isBlank())
            throw new RuntimeException("Debes subir el contrato firmado antes de guardar el préstamo");

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Prestamo prestamo = Prestamo.builder()
                .cliente(cliente)
                .monto(dto.getMonto())
                .tasaInteres(dto.getTasaInteres())
                .numPagos(dto.getNumPagos())
                .fechaInicio(dto.getFechaInicio())
                .contratoPdf(dto.getContratoPdf())
                .contratoSubido(dto.getContratoPdf() != null && !dto.getContratoPdf().isBlank())
                .build();

        Prestamo guardado = prestamoRepository.save(prestamo);
        generarAmortizaciones(guardado);
        bitacoraService.registrar("CREAR", "PRESTAMO", guardado.getId(),
                "Préstamo creado con contrato: " + dto.getContratoPdf());
        return guardado;
    }

    /*
    public List<Prestamo> listarPorCliente(Long clienteId) {
        return prestamoRepository.findByClienteId(clienteId);
    }

     */

    public Page<Prestamo> listarPorCliente(Long clienteId, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("id").descending());
        return prestamoRepository.findByClienteId(clienteId, pageable);
    }

    public List<Amortizacion> obtenerTablaAmortizacion(Long prestamoId) {
        return amortizacionRepository.findByPrestamoIdOrderByNumPago(prestamoId);
    }

    private void generarAmortizaciones(Prestamo prestamo) {
        BigDecimal monto = prestamo.getMonto();
        BigDecimal tasaMensual = prestamo.getTasaInteres()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        int n = prestamo.getNumPagos();

        // Fórmula de cuota fija: M * (i * (1+i)^n) / ((1+i)^n - 1)
        BigDecimal unoPlusI = BigDecimal.ONE.add(tasaMensual);
        BigDecimal unoPlusIPowN = unoPlusI.pow(n, new MathContext(10));
        BigDecimal cuota = monto
                .multiply(tasaMensual.multiply(unoPlusIPowN))
                .divide(unoPlusIPowN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        BigDecimal saldo = monto;
        List<Amortizacion> amortizaciones = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            BigDecimal interesMes = saldo.multiply(tasaMensual).setScale(2, RoundingMode.HALF_UP);
            BigDecimal capitalMes = cuota.subtract(interesMes).setScale(2, RoundingMode.HALF_UP);

            // Último pago: ajustar por redondeo
            if (i == n) {
                capitalMes = saldo;
                cuota = capitalMes.add(interesMes);
            }

            saldo = saldo.subtract(capitalMes).setScale(2, RoundingMode.HALF_UP);

            Amortizacion amortizacion = Amortizacion.builder()
                    .prestamo(prestamo)
                    .numPago(i)
                    .fechaVencimiento(prestamo.getFechaInicio().plusMonths(i))
                    .capital(capitalMes)
                    .interes(interesMes)
                    .cuota(cuota)
                    .saldoRestante(saldo)
                    .estatus(Amortizacion.EstatusAmortizacion.PENDIENTE)
                    .build();

            amortizaciones.add(amortizacion);
        }

        amortizacionRepository.saveAll(amortizaciones);
    }
}