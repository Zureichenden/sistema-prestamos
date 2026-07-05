package com.prestamos.sistema_prestamos.modules.empleados.repository;

import com.prestamos.sistema_prestamos.modules.empleados.entity.EmpleadoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmpleadoDocumentoRepository extends JpaRepository<EmpleadoDocumento, Long> {
    List<EmpleadoDocumento> findByEmpleadoId(Long empleadoId);
}