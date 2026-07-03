package com.prestamos.sistema_prestamos.modules.rh.repository;

import com.prestamos.sistema_prestamos.modules.rh.entity.Puesto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuestoRepository extends JpaRepository<Puesto, Long> {
    boolean existsByNombre(String nombre);
    Page<Puesto> findAll(Pageable pageable);
}