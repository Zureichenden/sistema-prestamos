package com.prestamos.sistema_prestamos.modules.rh.repository;

import com.prestamos.sistema_prestamos.modules.rh.entity.Salario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalarioRepository extends JpaRepository<Salario, Long> {
    Page<Salario> findAll(Pageable pageable);
}