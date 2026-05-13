package com.prestamos.sistema_prestamos.repository;

import com.prestamos.sistema_prestamos.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByRfc(String rfc);
    boolean existsByEmail(String email);
    boolean existsByRfc(String rfc);
    Page<Cliente> findByFechaRegistroBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

}