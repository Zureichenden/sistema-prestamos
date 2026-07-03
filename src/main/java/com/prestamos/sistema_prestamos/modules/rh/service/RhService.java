package com.prestamos.sistema_prestamos.modules.rh.service;

import com.prestamos.sistema_prestamos.modules.rh.dto.*;
import com.prestamos.sistema_prestamos.modules.rh.entity.*;
import com.prestamos.sistema_prestamos.modules.rh.repository.*;
import com.prestamos.sistema_prestamos.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RhService {

    private final PuestoRepository puestoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final SalarioRepository salarioRepository;

    // ── PUESTOS ──────────────────────────────────────────────

    public PuestoDTO crearPuesto(PuestoDTO dto) {
        if (puestoRepository.existsByNombre(dto.getNombre()))
            throw new BusinessException("Ya existe un puesto con ese nombre");
        Puesto puesto = Puesto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .nivel(dto.getNivel())
                .build();
        return toPuestoDTO(puestoRepository.save(puesto));
    }

    public Page<PuestoDTO> listarPuestos(int pagina, int tamanio) {
        return puestoRepository.findAll(PageRequest.of(pagina, tamanio, Sort.by("nivel").ascending()))
                .map(this::toPuestoDTO);
    }

    public List<PuestoDTO> listarTodosPuestos() {
        return puestoRepository.findAll(Sort.by("nivel").ascending())
                .stream().map(this::toPuestoDTO).collect(Collectors.toList());
    }

    public PuestoDTO actualizarPuesto(Long id, PuestoDTO dto) {
        Puesto puesto = puestoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Puesto no encontrado"));
        puesto.setNombre(dto.getNombre());
        puesto.setDescripcion(dto.getDescripcion());
        puesto.setNivel(dto.getNivel());
        return toPuestoDTO(puestoRepository.save(puesto));
    }

    public void eliminarPuesto(Long id) {
        if (!puestoRepository.existsById(id))
            throw new BusinessException("Puesto no encontrado");
        puestoRepository.deleteById(id);
    }

    // ── DEPARTAMENTOS ────────────────────────────────────────

    public DepartamentoDTO crearDepartamento(DepartamentoDTO dto) {
        if (departamentoRepository.existsByNombre(dto.getNombre()))
            throw new BusinessException("Ya existe un departamento con ese nombre");
        Departamento departamento = Departamento.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .jefeId(dto.getJefeId())
                .build();
        return toDepartamentoDTO(departamentoRepository.save(departamento));
    }

    public Page<DepartamentoDTO> listarDepartamentos(int pagina, int tamanio) {
        return departamentoRepository.findAll(PageRequest.of(pagina, tamanio, Sort.by("nombre").ascending()))
                .map(this::toDepartamentoDTO);
    }

    public List<DepartamentoDTO> listarTodosDepartamentos() {
        return departamentoRepository.findAll(Sort.by("nombre").ascending())
                .stream().map(this::toDepartamentoDTO).collect(Collectors.toList());
    }

    public DepartamentoDTO actualizarDepartamento(Long id, DepartamentoDTO dto) {
        Departamento departamento = departamentoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Departamento no encontrado"));
        departamento.setNombre(dto.getNombre());
        departamento.setDescripcion(dto.getDescripcion());
        departamento.setJefeId(dto.getJefeId());
        return toDepartamentoDTO(departamentoRepository.save(departamento));
    }

    public void eliminarDepartamento(Long id) {
        if (!departamentoRepository.existsById(id))
            throw new BusinessException("Departamento no encontrado");
        departamentoRepository.deleteById(id);
    }

    // ── SALARIOS ─────────────────────────────────────────────

    public SalarioDTO crearSalario(SalarioDTO dto) {
        Salario salario = Salario.builder()
                .monto(dto.getMonto())
                .descripcion(dto.getDescripcion())
                .fechaVigencia(dto.getFechaVigencia())
                .build();
        return toSalarioDTO(salarioRepository.save(salario));
    }

    public Page<SalarioDTO> listarSalarios(int pagina, int tamanio) {
        return salarioRepository.findAll(PageRequest.of(pagina, tamanio, Sort.by("fechaVigencia").descending()))
                .map(this::toSalarioDTO);
    }

    public List<SalarioDTO> listarTodosSalarios() {
        return salarioRepository.findAll(Sort.by("monto").ascending())
                .stream().map(this::toSalarioDTO).collect(Collectors.toList());
    }

    public SalarioDTO actualizarSalario(Long id, SalarioDTO dto) {
        Salario salario = salarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Salario no encontrado"));
        salario.setMonto(dto.getMonto());
        salario.setDescripcion(dto.getDescripcion());
        salario.setFechaVigencia(dto.getFechaVigencia());
        return toSalarioDTO(salarioRepository.save(salario));
    }

    public void eliminarSalario(Long id) {
        if (!salarioRepository.existsById(id))
            throw new BusinessException("Salario no encontrado");
        salarioRepository.deleteById(id);
    }

    // ── MAPPERS ──────────────────────────────────────────────

    private PuestoDTO toPuestoDTO(Puesto p) {
        return PuestoDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .nivel(p.getNivel())
                .build();
    }

    private DepartamentoDTO toDepartamentoDTO(Departamento d) {
        return DepartamentoDTO.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .descripcion(d.getDescripcion())
                .jefeId(d.getJefeId())
                .build();
    }

    private SalarioDTO toSalarioDTO(Salario s) {
        return SalarioDTO.builder()
                .id(s.getId())
                .monto(s.getMonto())
                .descripcion(s.getDescripcion())
                .fechaVigencia(s.getFechaVigencia())
                .build();
    }
}