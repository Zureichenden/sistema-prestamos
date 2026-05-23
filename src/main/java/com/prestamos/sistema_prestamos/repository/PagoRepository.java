package com.prestamos.sistema_prestamos.repository;

import com.prestamos.sistema_prestamos.entity.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByPrestamoId(Long prestamoId);
    List<Pago> findByAmortizacionId(Long amortizacionId);
    boolean existsByAmortizacionId(Long amortizacionId);
    Page<Pago> findByPrestamoId(Long prestamoId, Pageable pageable);
    Page<Pago> findByFechaPagoBetween(LocalDate inicio, LocalDate fin, Pageable pageable);
    List<Pago> findByFechaPagoBetween(LocalDate inicio, LocalDate fin);
}