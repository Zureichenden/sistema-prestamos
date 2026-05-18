package com.prestamos.sistema_prestamos.service;

import com.prestamos.sistema_prestamos.entity.Rol;
import com.prestamos.sistema_prestamos.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    public Rol crear(String nombre, String descripcion) {
        if (rolRepository.existsByNombre(nombre))
            throw new RuntimeException("El rol ya existe");
        return rolRepository.save(Rol.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .build());
    }
}