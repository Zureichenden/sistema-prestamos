package com.prestamos.sistema_prestamos.modules.empleados.repository;

import com.prestamos.sistema_prestamos.modules.empleados.entity.BitacoraEmpleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BitacoraEmpleadoRepository extends JpaRepository<BitacoraEmpleado, Long> {
    List<BitacoraEmpleado> findByEmpleadoIdOrderByFechaHoraDesc(Long empleadoId);
    Page<BitacoraEmpleado> findAllByOrderByFechaHoraDesc(Pageable pageable);
}