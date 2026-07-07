package com.prestamos.sistema_prestamos.modules.auth.repository;

import com.prestamos.sistema_prestamos.modules.auth.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<Usuario> findAll(Pageable pageable);
    boolean existsByEmpleadoId(Long empleadoId);
    Optional<Usuario> findByEmpleadoId(Long empleadoId);
}