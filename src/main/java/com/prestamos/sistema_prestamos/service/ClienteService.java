package com.prestamos.sistema_prestamos.service;

import com.prestamos.sistema_prestamos.dto.ClienteDTO;
import com.prestamos.sistema_prestamos.entity.Cliente;
import com.prestamos.sistema_prestamos.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EmailService emailService;
    private final BitacoraService bitacoraService;

    public ClienteDTO crear(ClienteDTO dto) {
        if (clienteRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("Ya existe un cliente con ese email");
        if (clienteRepository.existsByRfc(dto.getRfc()))
            throw new RuntimeException("Ya existe un cliente con ese RFC");

        Cliente cliente = Cliente.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .rfc(dto.getRfc())
                .telefono(dto.getTelefono())
                .build();

        Cliente guardado = clienteRepository.save(cliente);
        emailService.enviarBienvenida(guardado.getEmail(), guardado.getNombre());
        bitacoraService.registrar("CREAR", "CLIENTE", guardado.getId(),
                "Cliente creado: " + guardado.getNombre() + " " + guardado.getApellido());
        return toDTO(guardado);
    }

    /*
    public List<ClienteDTO> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

     */

    public Page<ClienteDTO> listar(int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("id").descending());
        return clienteRepository.findAll(pageable).map(this::toDTO);
    }



    public ClienteDTO obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
        return toDTO(cliente);
    }

    public ClienteDTO actualizar(Long id, ClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));

        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setTelefono(dto.getTelefono());


        ClienteDTO resultado = toDTO(clienteRepository.save(cliente));
        bitacoraService.registrar("ACTUALIZAR", "CLIENTE", id,
                "Cliente actualizado: " + dto.getNombre() + " " + dto.getApellido());
        return resultado;
    }

    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id))
            throw new RuntimeException("Cliente no encontrado con id: " + id);
        clienteRepository.deleteById(id);
        bitacoraService.registrar("ELIMINAR", "CLIENTE", id, "Cliente eliminado");
    }

    private ClienteDTO toDTO(Cliente c) {
        return ClienteDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .apellido(c.getApellido())
                .email(c.getEmail())
                .rfc(c.getRfc())
                .telefono(c.getTelefono())
                .build();
    }
}