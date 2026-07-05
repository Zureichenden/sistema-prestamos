package com.prestamos.sistema_prestamos.modules.empleados.service;

import com.prestamos.sistema_prestamos.modules.empleados.dto.*;
import com.prestamos.sistema_prestamos.modules.empleados.entity.*;
import com.prestamos.sistema_prestamos.modules.empleados.repository.*;
import com.prestamos.sistema_prestamos.modules.rh.entity.*;
import com.prestamos.sistema_prestamos.modules.rh.repository.*;
import com.prestamos.sistema_prestamos.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import com.prestamos.sistema_prestamos.modules.empleados.entity.BitacoraEmpleado.TipoMovimiento;
@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoDireccionRepository direccionRepository;
    private final EmpleadoTelefonoRepository telefonoRepository;
    private final EmpleadoBeneficiarioRepository beneficiarioRepository;
    private final EmpleadoDocumentoRepository documentoRepository;
    private final BitacoraEmpleadoRepository bitacoraRepository;
    private final PuestoRepository puestoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final SalarioRepository salarioRepository;

    // ── EMPLEADOS ────────────────────────────────────────────

    @Transactional
    public EmpleadoResponseDTO crear(EmpleadoRequestDTO dto) {
        if (empleadoRepository.existsByEmail(dto.getEmail()))
            throw new BusinessException("Ya existe un empleado con ese email");
        if (empleadoRepository.existsByRfc(dto.getRfc()))
            throw new BusinessException("Ya existe un empleado con ese RFC");
        if (dto.getCurp() != null && empleadoRepository.existsByCurp(dto.getCurp()))
            throw new BusinessException("Ya existe un empleado con ese CURP");
        if (dto.getNss() != null && empleadoRepository.existsByNss(dto.getNss()))
            throw new BusinessException("Ya existe un empleado con ese NSS");

        Empleado empleado = Empleado.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .fechaNacimiento(dto.getFechaNacimiento())
                .rfc(dto.getRfc())
                .curp(dto.getCurp())
                .nss(dto.getNss())
                .fechaIngreso(dto.getFechaIngreso())
                .puesto(dto.getPuestoId() != null ?
                        puestoRepository.findById(dto.getPuestoId()).orElse(null) : null)
                .departamento(dto.getDepartamentoId() != null ?
                        departamentoRepository.findById(dto.getDepartamentoId()).orElse(null) : null)
                .salario(dto.getSalarioId() != null ?
                        salarioRepository.findById(dto.getSalarioId()).orElse(null) : null)
                .build();

        Empleado guardado = empleadoRepository.save(empleado);
        registrarBitacora(guardado, BitacoraEmpleado.TipoMovimiento.ALTA,
                "Alta de empleado", null, guardado.getNombre() + " " + guardado.getApellido());
        return toDTO(guardado);
    }

    public Page<EmpleadoResponseDTO> listar(int pagina, int tamanio) {
        return empleadoRepository.findAll(
                        PageRequest.of(pagina, tamanio, Sort.by("apellido").ascending()))
                .map(this::toDTO);
    }

    public Page<EmpleadoResponseDTO> listarActivos(int pagina, int tamanio) {
        return empleadoRepository.findByActivo(true,
                        PageRequest.of(pagina, tamanio, Sort.by("apellido").ascending()))
                .map(this::toDTO);
    }

    public EmpleadoResponseDTO obtenerPorId(Long id) {
        return toDTO(empleadoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado")));
    }

    @Transactional
    public EmpleadoResponseDTO actualizar(Long id, EmpleadoRequestDTO dto) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));

        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setEmail(dto.getEmail());
        empleado.setFechaNacimiento(dto.getFechaNacimiento());
        empleado.setRfc(dto.getRfc());
        empleado.setCurp(dto.getCurp());
        empleado.setNss(dto.getNss());
        empleado.setFechaIngreso(dto.getFechaIngreso());

        registrarBitacora(empleado, BitacoraEmpleado.TipoMovimiento.CAMBIO_DATOS_PERSONALES,
                "Actualización de datos personales", null, null);

        return toDTO(empleadoRepository.save(empleado));
    }

    @Transactional
    public EmpleadoResponseDTO cambiarPuesto(Long id, Long puestoId) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));
        Puesto nuevoPuesto = puestoRepository.findById(puestoId)
                .orElseThrow(() -> new BusinessException("Puesto no encontrado"));

        String anterior = empleado.getPuesto() != null ? empleado.getPuesto().getNombre() : "Sin puesto";
        empleado.setPuesto(nuevoPuesto);

        registrarBitacora(empleado, BitacoraEmpleado.TipoMovimiento.CAMBIO_PUESTO,
                "Cambio de puesto", anterior, nuevoPuesto.getNombre());

        return toDTO(empleadoRepository.save(empleado));
    }

    @Transactional
    public EmpleadoResponseDTO cambiarDepartamento(Long id, Long departamentoId) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));
        Departamento nuevoDpto = departamentoRepository.findById(departamentoId)
                .orElseThrow(() -> new BusinessException("Departamento no encontrado"));

        String anterior = empleado.getDepartamento() != null ?
                empleado.getDepartamento().getNombre() : "Sin departamento";
        empleado.setDepartamento(nuevoDpto);

        registrarBitacora(empleado, BitacoraEmpleado.TipoMovimiento.CAMBIO_DEPARTAMENTO,
                "Cambio de departamento", anterior, nuevoDpto.getNombre());

        return toDTO(empleadoRepository.save(empleado));
    }

    @Transactional
    public EmpleadoResponseDTO cambiarSalario(Long id, Long salarioId) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));
        Salario nuevoSalario = salarioRepository.findById(salarioId)
                .orElseThrow(() -> new BusinessException("Salario no encontrado"));

        String anterior = empleado.getSalario() != null ?
                "$" + empleado.getSalario().getMonto().toPlainString() : "Sin salario";
        empleado.setSalario(nuevoSalario);

        registrarBitacora(empleado, BitacoraEmpleado.TipoMovimiento.CAMBIO_SALARIO,
                "Cambio de salario", anterior, "$" + nuevoSalario.getMonto().toPlainString());

        return toDTO(empleadoRepository.save(empleado));
    }

    @Transactional
    public EmpleadoResponseDTO darBaja(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));
        if (!empleado.isActivo())
            throw new BusinessException("El empleado ya está dado de baja");
        empleado.setActivo(false);
        registrarBitacora(empleado, BitacoraEmpleado.TipoMovimiento.BAJA,
                "Baja de empleado", "ACTIVO", "INACTIVO");
        return toDTO(empleadoRepository.save(empleado));
    }

    @Transactional
    public EmpleadoResponseDTO reactivar(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));
        if (empleado.isActivo())
            throw new BusinessException("El empleado ya está activo");
        empleado.setActivo(true);
        registrarBitacora(empleado, BitacoraEmpleado.TipoMovimiento.REACTIVACION,
                "Reactivación de empleado", "INACTIVO", "ACTIVO");
        return toDTO(empleadoRepository.save(empleado));
    }

    // ── DIRECCIONES ──────────────────────────────────────────

    @Transactional
    public EmpleadoDireccionDTO agregarDireccion(Long empleadoId, EmpleadoDireccionDTO dto) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));

        EmpleadoDireccion direccion = EmpleadoDireccion.builder()
                .empleado(empleado)
                .calle(dto.getCalle())
                .colonia(dto.getColonia())
                .ciudad(dto.getCiudad())
                .estado(dto.getEstado())
                .codigoPostal(dto.getCodigoPostal())
                .tipo(dto.getTipo() != null ?
                        EmpleadoDireccion.TipoDireccion.valueOf(dto.getTipo()) : null)
                .principal(dto.isPrincipal())
                .build();

        return toDireccionDTO(direccionRepository.save(direccion));
    }

    public List<EmpleadoDireccionDTO> listarDirecciones(Long empleadoId) {
        return direccionRepository.findByEmpleadoId(empleadoId)
                .stream().map(this::toDireccionDTO).collect(Collectors.toList());
    }

    @Transactional
    public void eliminarDireccion(Long id) {
        if (!direccionRepository.existsById(id))
            throw new BusinessException("Dirección no encontrada");
        direccionRepository.deleteById(id);
    }

    // ── TELÉFONOS ────────────────────────────────────────────

    @Transactional
    public EmpleadoTelefonoDTO agregarTelefono(Long empleadoId, EmpleadoTelefonoDTO dto) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));

        EmpleadoTelefono telefono = EmpleadoTelefono.builder()
                .empleado(empleado)
                .numero(dto.getNumero())
                .tipo(dto.getTipo() != null ?
                        EmpleadoTelefono.TipoTelefono.valueOf(dto.getTipo()) : null)
                .principal(dto.isPrincipal())
                .build();

        return toTelefonoDTO(telefonoRepository.save(telefono));
    }

    public List<EmpleadoTelefonoDTO> listarTelefonos(Long empleadoId) {
        return telefonoRepository.findByEmpleadoId(empleadoId)
                .stream().map(this::toTelefonoDTO).collect(Collectors.toList());
    }

    @Transactional
    public void eliminarTelefono(Long id) {
        if (!telefonoRepository.existsById(id))
            throw new BusinessException("Teléfono no encontrado");
        telefonoRepository.deleteById(id);
    }

    // ── BENEFICIARIOS ────────────────────────────────────────

    @Transactional
    public EmpleadoBeneficiarioDTO agregarBeneficiario(Long empleadoId, EmpleadoBeneficiarioDTO dto) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));

        EmpleadoBeneficiario beneficiario = EmpleadoBeneficiario.builder()
                .empleado(empleado)
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .parentesco(dto.getParentesco())
                .porcentaje(dto.getPorcentaje())
                .telefono(dto.getTelefono())
                .fechaNacimiento(dto.getFechaNacimiento())
                .build();

        return toBeneficiarioDTO(beneficiarioRepository.save(beneficiario));
    }

    public List<EmpleadoBeneficiarioDTO> listarBeneficiarios(Long empleadoId) {
        return beneficiarioRepository.findByEmpleadoId(empleadoId)
                .stream().map(this::toBeneficiarioDTO).collect(Collectors.toList());
    }

    @Transactional
    public void eliminarBeneficiario(Long id) {
        if (!beneficiarioRepository.existsById(id))
            throw new BusinessException("Beneficiario no encontrado");
        beneficiarioRepository.deleteById(id);
    }

    // ── BITÁCORA ─────────────────────────────────────────────

    public List<BitacoraEmpleadoDTO> listarBitacora(Long empleadoId) {
        return bitacoraRepository.findByEmpleadoIdOrderByFechaHoraDesc(empleadoId)
                .stream().map(this::toBitacoraDTO).collect(Collectors.toList());
    }

    public Page<BitacoraEmpleadoDTO> listarTodaBitacora(int pagina, int tamanio) {
        return bitacoraRepository.findAllByOrderByFechaHoraDesc(
                        PageRequest.of(pagina, tamanio))
                .map(this::toBitacoraDTO);
    }

    // ── HELPERS ──────────────────────────────────────────────

    private void registrarBitacora(Empleado empleado, TipoMovimiento tipo,
                                   String descripcion, String anterior, String nuevo) {
        String usuario = "sistema";
        try {
            usuario = SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception ignored) {}

        BitacoraEmpleado registro = BitacoraEmpleado.builder()
                .empleado(empleado)
                .tipoMovimiento(tipo)
                .descripcion(descripcion)
                .valorAnterior(anterior)
                .valorNuevo(nuevo)
                .build();
        bitacoraRepository.save(registro);
    }

    // ── MAPPERS ──────────────────────────────────────────────

    private EmpleadoResponseDTO toDTO(Empleado e) {
        return EmpleadoResponseDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .apellido(e.getApellido())
                .email(e.getEmail())
                .fechaNacimiento(e.getFechaNacimiento())
                .rfc(e.getRfc())
                .curp(e.getCurp())
                .nss(e.getNss())
                .fechaIngreso(e.getFechaIngreso())
                .activo(e.isActivo())
                .puestoId(e.getPuesto() != null ? e.getPuesto().getId() : null)
                .puestoNombre(e.getPuesto() != null ? e.getPuesto().getNombre() : null)
                .departamentoId(e.getDepartamento() != null ? e.getDepartamento().getId() : null)
                .departamentoNombre(e.getDepartamento() != null ? e.getDepartamento().getNombre() : null)
                .salarioId(e.getSalario() != null ? e.getSalario().getId() : null)
                .salarioMonto(e.getSalario() != null ? e.getSalario().getMonto() : null)
                .createdAt(e.getCreatedAt())
                .build();
    }

    private EmpleadoDireccionDTO toDireccionDTO(EmpleadoDireccion d) {
        return EmpleadoDireccionDTO.builder()
                .id(d.getId())
                .empleadoId(d.getEmpleado().getId())
                .calle(d.getCalle())
                .colonia(d.getColonia())
                .ciudad(d.getCiudad())
                .estado(d.getEstado())
                .codigoPostal(d.getCodigoPostal())
                .tipo(d.getTipo() != null ? d.getTipo().name() : null)
                .principal(d.isPrincipal())
                .build();
    }

    private EmpleadoTelefonoDTO toTelefonoDTO(EmpleadoTelefono t) {
        return EmpleadoTelefonoDTO.builder()
                .id(t.getId())
                .empleadoId(t.getEmpleado().getId())
                .numero(t.getNumero())
                .tipo(t.getTipo() != null ? t.getTipo().name() : null)
                .principal(t.isPrincipal())
                .build();
    }

    private EmpleadoBeneficiarioDTO toBeneficiarioDTO(EmpleadoBeneficiario b) {
        return EmpleadoBeneficiarioDTO.builder()
                .id(b.getId())
                .empleadoId(b.getEmpleado().getId())
                .nombre(b.getNombre())
                .apellido(b.getApellido())
                .parentesco(b.getParentesco())
                .porcentaje(b.getPorcentaje())
                .telefono(b.getTelefono())
                .fechaNacimiento(b.getFechaNacimiento())
                .build();
    }

    private BitacoraEmpleadoDTO toBitacoraDTO(BitacoraEmpleado b) {
        return BitacoraEmpleadoDTO.builder()
                .id(b.getId())
                .empleadoId(b.getEmpleado().getId())
                .empleadoNombre(b.getEmpleado().getNombre() + " " + b.getEmpleado().getApellido())
                .tipoMovimiento(b.getTipoMovimiento().name())
                .descripcion(b.getDescripcion())
                .valorAnterior(b.getValorAnterior())
                .valorNuevo(b.getValorNuevo())
                .usuarioId(b.getUsuarioId())
                .fechaHora(b.getFechaHora())
                .build();
    }
}