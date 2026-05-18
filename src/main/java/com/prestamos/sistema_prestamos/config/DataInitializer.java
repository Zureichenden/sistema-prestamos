package com.prestamos.sistema_prestamos.config;

import com.prestamos.sistema_prestamos.entity.Rol;
import com.prestamos.sistema_prestamos.entity.Usuario;
import com.prestamos.sistema_prestamos.repository.RolRepository;
import com.prestamos.sistema_prestamos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        crearRolSiNoExiste("ADMIN", "Acceso total al sistema");
        crearRolSiNoExiste("GESTOR", "Crear clientes, préstamos y pagos");
        crearRolSiNoExiste("AUDITOR", "Ver reportes y bitácora");
        crearRolSiNoExiste("VIEWER", "Solo consultas");

        if (!usuarioRepository.existsByUsername("admin")) {
            Rol rolAdmin = rolRepository.findByNombre("ADMIN").orElseThrow();
            Usuario admin = Usuario.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .nombre("Administrador")
                    .email("admin@sistema.com")
                    .roles(Set.of(rolAdmin))
                    .build();
            usuarioRepository.save(admin);
            log.info("✅ Usuario admin creado");
        }
    }

    private void crearRolSiNoExiste(String nombre, String descripcion) {
        if (!rolRepository.existsByNombre(nombre)) {
            rolRepository.save(Rol.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .build());
            log.info("✅ Rol creado: {}", nombre);
        }
    }
}