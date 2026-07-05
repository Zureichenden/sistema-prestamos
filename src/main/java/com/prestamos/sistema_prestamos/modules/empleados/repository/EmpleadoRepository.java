package com.prestamos.sistema_prestamos.modules.empleados.repository;

import com.prestamos.sistema_prestamos.modules.empleados.entity.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    boolean existsByEmail(String email);
    boolean existsByRfc(String rfc);
    boolean existsByCurp(String curp);
    boolean existsByNss(String nss);
    Optional<Empleado> findByEmail(String email);
    Page<Empleado> findByActivo(boolean activo, Pageable pageable);
    Page<Empleado> findByDepartamentoId(Long departamentoId, Pageable pageable);
    Page<Empleado> findByPuestoId(Long puestoId, Pageable pageable);
}