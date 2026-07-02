package com.prestamos.sistema_prestamos.shared;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BitacoraRepository extends JpaRepository<Bitacora, Long> {
    Page<Bitacora> findByUsuario(String usuario, Pageable pageable);
    Page<Bitacora> findByEntidad(String entidad, Pageable pageable);
    Page<Bitacora> findAllByOrderByFechaHoraDesc(Pageable pageable);
}