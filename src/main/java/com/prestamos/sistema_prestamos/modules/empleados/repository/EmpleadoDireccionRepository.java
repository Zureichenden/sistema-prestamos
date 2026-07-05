package com.prestamos.sistema_prestamos.modules.empleados.repository;

import com.prestamos.sistema_prestamos.modules.empleados.entity.EmpleadoDireccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmpleadoDireccionRepository extends JpaRepository<EmpleadoDireccion, Long> {
    List<EmpleadoDireccion> findByEmpleadoId(Long empleadoId);
    void deleteByEmpleadoId(Long empleadoId);
}