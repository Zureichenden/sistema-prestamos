package com.prestamos.sistema_prestamos.repository;

import com.prestamos.sistema_prestamos.entity.Prestamo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    List<Prestamo> findByClienteId(Long clienteId);
    List<Prestamo> findByEstatus(Prestamo.EstatusPrestamo estatus);
    Page<Prestamo> findByClienteId(Long clienteId, Pageable pageable);
    Page<Prestamo> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
    List<Prestamo> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fin);
}