package com.prestamos.sistema_prestamos.modules.empleados.repository;

import com.prestamos.sistema_prestamos.modules.empleados.entity.EmpleadoBeneficiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmpleadoBeneficiarioRepository extends JpaRepository<EmpleadoBeneficiario, Long> {
    List<EmpleadoBeneficiario> findByEmpleadoId(Long empleadoId);
    void deleteByEmpleadoId(Long empleadoId);
}