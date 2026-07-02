package com.prestamos.sistema_prestamos.modules.prestamos.repository;

import com.prestamos.sistema_prestamos.modules.prestamos.entity.Amortizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AmortizacionRepository extends JpaRepository<Amortizacion, Long> {
    List<Amortizacion> findByPrestamoIdOrderByNumPago(Long prestamoId);
    List<Amortizacion> findByEstatus(Amortizacion.EstatusAmortizacion estatus);
}