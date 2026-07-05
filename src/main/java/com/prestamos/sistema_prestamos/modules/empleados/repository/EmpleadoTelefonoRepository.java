package com.prestamos.sistema_prestamos.modules.empleados.repository;

import com.prestamos.sistema_prestamos.modules.empleados.entity.EmpleadoTelefono;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmpleadoTelefonoRepository extends JpaRepository<EmpleadoTelefono, Long> {
    List<EmpleadoTelefono> findByEmpleadoId(Long empleadoId);
    void deleteByEmpleadoId(Long empleadoId);
}