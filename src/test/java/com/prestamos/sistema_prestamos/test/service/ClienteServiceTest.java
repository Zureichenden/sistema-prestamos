package com.prestamos.sistema_prestamos.service;

import com.prestamos.sistema_prestamos.modules.clientes.dto.ClienteDTO;
import com.prestamos.sistema_prestamos.modules.clientes.entity.Cliente;
import com.prestamos.sistema_prestamos.modules.clientes.repository.ClienteRepository;
import com.prestamos.sistema_prestamos.modules.clientes.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@email.com")
                .rfc("PEJJ900101ABC")
                .telefono("6671234567")
                .build();

        clienteDTO = ClienteDTO.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@email.com")
                .rfc("PEJJ900101ABC")
                .telefono("6671234567")
                .build();
    }

    @Test
    void crear_clienteNuevo_retornaClienteDTO() {
        when(clienteRepository.existsByEmail(clienteDTO.getEmail())).thenReturn(false);
        when(clienteRepository.existsByRfc(clienteDTO.getRfc())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteDTO resultado = clienteService.crear(clienteDTO);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        assertEquals("juan@email.com", resultado.getEmail());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void crear_emailDuplicado_lanzaExcepcion() {
        when(clienteRepository.existsByEmail(clienteDTO.getEmail())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.crear(clienteDTO));

        assertEquals("Ya existe un cliente con ese email", ex.getMessage());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void crear_rfcDuplicado_lanzaExcepcion() {
        when(clienteRepository.existsByEmail(clienteDTO.getEmail())).thenReturn(false);
        when(clienteRepository.existsByRfc(clienteDTO.getRfc())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.crear(clienteDTO));

        assertEquals("Ya existe un cliente con ese RFC", ex.getMessage());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void listar_retornaListaDeClientes() {
        Page<Cliente> page = new PageImpl<>(List.of(cliente));
        when(clienteRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ClienteDTO> resultado = clienteService.listar(0, 10);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Juan", resultado.getContent().get(0).getNombre());
    }

    @Test
    void obtenerPorId_clienteExistente_retornaClienteDTO() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        ClienteDTO resultado = clienteService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Pérez", resultado.getApellido());
    }

    @Test
    void obtenerPorId_clienteNoExistente_lanzaExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.obtenerPorId(99L));

        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
    }

    @Test
    void eliminar_clienteExistente_eliminaCorrectamente() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(1L);

        assertDoesNotThrow(() -> clienteService.eliminar(1L));
        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_clienteNoExistente_lanzaExcepcion() {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.eliminar(99L));

        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
        verify(clienteRepository, never()).deleteById(any());
    }
}