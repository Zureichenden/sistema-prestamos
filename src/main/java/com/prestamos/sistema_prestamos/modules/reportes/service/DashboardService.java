package com.prestamos.sistema_prestamos.modules.reportes.service;

import com.prestamos.sistema_prestamos.modules.reportes.dto.DashboardDTO;
import com.prestamos.sistema_prestamos.modules.prestamos.entity.Prestamo;
import com.prestamos.sistema_prestamos.modules.clientes.repository.ClienteRepository;
import com.prestamos.sistema_prestamos.modules.prestamos.repository.AmortizacionRepository;
import com.prestamos.sistema_prestamos.modules.prestamos.repository.PagoRepository;
import com.prestamos.sistema_prestamos.modules.prestamos.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ClienteRepository clienteRepository;
    private final PrestamoRepository prestamoRepository;
    private final PagoRepository pagoRepository;
    private final AmortizacionRepository amortizacionRepository;

    public DashboardDTO obtenerDashboard() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDateTime inicioMesDT = inicioMes.atStartOfDay();
        LocalDateTime finMesDT = hoy.atTime(23, 59, 59);

        // Tarjetas
        long totalClientes = clienteRepository.count();
        long prestamosActivos = prestamoRepository.findByEstatus(Prestamo.EstatusPrestamo.ACTIVO).size();
        long prestamosLiquidados = prestamoRepository.findByEstatus(Prestamo.EstatusPrestamo.LIQUIDADO).size();
        long totalPrestamos = prestamoRepository.count();

        BigDecimal montoTotalPrestado = prestamoRepository.findAll().stream()
                .map(p -> p.getMonto() != null ? p.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRecaudado = pagoRepository.findAll().stream()
                .map(p -> p.getMontoPagado() != null ? p.getMontoPagado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pagosMes = pagoRepository.findByFechaPagoBetween(inicioMes, hoy).stream().count();

        BigDecimal montoRecaudadoMes = pagoRepository.findByFechaPagoBetween(inicioMes, hoy).stream()
                .map(p -> p.getMontoPagado() != null ? p.getMontoPagado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Gráficas — últimos 6 meses
        List<String> meses = new ArrayList<>();
        List<Long> prestamosPorMes = new ArrayList<>();
        List<BigDecimal> montoPorMes = new ArrayList<>();
        List<Long> pagosPorMes = new ArrayList<>();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy", new Locale("es", "MX"));

        for (int i = 5; i >= 0; i--) {
            LocalDate primerDia = hoy.minusMonths(i).withDayOfMonth(1);
            LocalDate ultimoDia = primerDia.withDayOfMonth(primerDia.lengthOfMonth());
            LocalDateTime inicioDT = primerDia.atStartOfDay();
            LocalDateTime finDT = ultimoDia.atTime(23, 59, 59);

            meses.add(primerDia.format(fmt));

            long numPrestamos = prestamoRepository.findByCreatedAtBetween(inicioDT, finDT).stream().count();
            prestamosPorMes.add(numPrestamos);

            BigDecimal monto = prestamoRepository.findByCreatedAtBetween(inicioDT, finDT).stream()
                    .map(p -> p.getMonto() != null ? p.getMonto() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            montoPorMes.add(monto);

            long numPagos = pagoRepository.findByFechaPagoBetween(primerDia, ultimoDia).stream().count();
            pagosPorMes.add(numPagos);
        }

        return DashboardDTO.builder()
                .totalClientes(totalClientes)
                .prestamosActivos(prestamosActivos)
                .prestamosLiquidados(prestamosLiquidados)
                .totalPrestamos(totalPrestamos)
                .montoTotalPrestado(montoTotalPrestado)
                .totalRecaudado(totalRecaudado)
                .pagosMes(pagosMes)
                .montoRecaudadoMes(montoRecaudadoMes)
                .meses(meses)
                .prestamosPorMes(prestamosPorMes)
                .montoPorMes(montoPorMes)
                .pagosPorMes(pagosPorMes)
                .build();
    }
}