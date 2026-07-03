package com.prestamos.sistema_prestamos.modules.rh.repository;

import com.prestamos.sistema_prestamos.modules.rh.entity.Departamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    boolean existsByNombre(String nombre);
    Page<Departamento> findAll(Pageable pageable);
}