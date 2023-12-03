package com.grupo7.cuentasclaras2.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo7.cuentasclaras2.DTO.GrupoDTO;
import com.grupo7.cuentasclaras2.DTO.IdEmailUsuarioDTO;
import com.grupo7.cuentasclaras2.exception.GroupException;
import com.grupo7.cuentasclaras2.modelos.Categoria;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Pago;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.GrupoRepository;
import com.grupo7.cuentasclaras2.repositories.UsuarioRepository;

@Service
public class GrupoService {
	@Autowired
	private GrupoRepository grupoRepository;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private CategoriaService categoriaService;

	public List<Grupo> getAllGroups() {
		return grupoRepository.findAll();
	}

	public Optional<Grupo> getGroupById(Long id) {
		return grupoRepository.findById(id);
	}

	public Grupo saveGroup(Grupo grupo) {
		return grupoRepository.save(grupo);
	}

	public void deleteGroup(Long id) {
		grupoRepository.deleteById(id);
	}

	public Optional<Grupo> getGroupByNombre(String nombre) {
		return grupoRepository.findByNombre(nombre);
	}

	public boolean addMemberToGroup(Long groupId, Long userId) {
		Optional<Grupo> grupoOptional = grupoRepository.findById(groupId);
		Optional<Usuario> usuarioOptional = usuarioService.getById(userId);

		if (grupoOptional.isPresent() && usuarioOptional.isPresent()) {
			Grupo grupo = grupoOptional.get();
			Usuario usuario = usuarioOptional.get();

			if (agregarMiembroAGrupo(grupo, usuario)) {
				return true;
			}
		}

		return false;
	}

	public boolean addMemberToGroup(Grupo grupo, Usuario usuario) {
		if (grupo != null && usuario != null) {
			List<Usuario> miembros = grupo.getMiembros();

			if (miembros == null || !miembros.contains(usuario)) {
				if (grupo.getEsPareja() && miembros != null && miembros.size() >= 2) {
					return false;
				}

				grupo.agregarMiembro(usuario);
				grupoRepository.save(grupo);

				usuario.unirseAGrupo(grupo);
				usuarioRepository.save(usuario);
				return true;
			}
		}

		return false;
	}

	public boolean removeMemberFromGroup(Long groupId, Long memberId) {
		Optional<Grupo> grupoOptional = grupoRepository.findById(groupId);
		Optional<Usuario> usuarioOptional = usuarioService.getById(memberId);

		if (grupoOptional.isPresent() && usuarioOptional.isPresent()) {
			Grupo grupo = grupoOptional.get();
			Usuario usuario = usuarioOptional.get();

			if (quitarMiembroDeGrupo(grupo, usuario)) {
				return true;
			}
		}

		return false;
	}

	@Transactional
	public Optional<Grupo> newGroupByDTO(GrupoDTO grupoDTO) {
		validarGrupoDTO(grupoDTO);

		Grupo grupo = new Grupo();
		grupo.setNombre(grupoDTO.getNombre());
		grupo.setEsPareja(false);

		Categoria categoria = obtenerCategoriaValidada(grupoDTO.getCategoria().getId());

		List<Usuario> miembros = convertirDTOaUsuariosValidados(grupoDTO.getMiembros());

		if (miembros.isEmpty()) {
			throw new GroupException("Se debe proporcionar al menos un usuario al crear un grupo.");
		}

		grupo.agregarMiembros(miembros);
		categoria.addGroup(grupo);
		grupo.setCategoria(categoria);

		categoriaService.saveCategoria(categoria);
		Grupo grupoGuardado = grupoRepository.save(grupo);

		guardarUsuariosEnGrupo(miembros, grupoGuardado);

		return Optional.of(grupoGuardado);
	}

	@Transactional
	public Optional<Grupo> newCoupleGroupByDTO(GrupoDTO grupoDTO) {
		validarCoupleGroupDTO(grupoDTO);

		Grupo grupo = new Grupo();
		grupo.setEsPareja(true);

		List<Usuario> miembros = convertirDTOaUsuariosValidados(grupoDTO.getMiembros());

		validarMiembrosParaGrupoPareja(miembros);

		grupo.agregarMiembros(miembros);
		Grupo grupoGuardado = grupoRepository.save(grupo);
		usuarioRepository.saveAll(miembros);

		// guardarUsuariosEnGrupo(miembros, grupoGuardado);

		return Optional.of(grupoGuardado);
	}

	public boolean addPaymentToGroup(Grupo grupo, Pago pago) {
		if (grupo != null && pago != null) {
			List<Pago> pagos = grupo.getPagos();

			if (pagos == null || !pagos.contains(pago)) {
				grupo.agregarPago(pago);
				grupoRepository.save(grupo);
				return true;
			}
		}

		return false;
	}

	// public Gasto addGastoToGroup(Gasto gasto, Long groupId) {
	// Optional<Grupo> grupoOptional = grupoRepository.findById(groupId);

	// if (grupoOptional.isPresent()) {
	// Grupo grupo = grupoOptional.get();
	// gasto.setGrupo(grupo);

	// gastoRepository.save(gasto);
	// grupo.agregarGasto(gasto);
	// grupoRepository.save(grupo);

	// return gasto;
	// }

	// return null;
	// }

	private boolean agregarMiembroAGrupo(Grupo grupo, Usuario usuario) {
		List<Usuario> miembros = grupo.getMiembros();

		if (miembros == null || !miembros.contains(usuario)) {
			if (grupo.getEsPareja() && miembros != null && miembros.size() >= 2) {
				return false;
			}

			if (miembros != null && miembros.contains(usuario)) {
				return false;
			}

			grupo.agregarMiembro(usuario);
			grupoRepository.save(grupo);
			usuario.unirseAGrupo(grupo);
			usuarioRepository.save(usuario);
			return true;
		}

		return false;
	}

	private boolean quitarMiembroDeGrupo(Grupo grupo, Usuario usuario) {
		List<Usuario> miembros = grupo.getMiembros();

		if (miembros != null && !miembros.isEmpty()) {
			boolean removed = miembros.remove(usuario);

			if (removed) {
				grupoRepository.save(grupo);
				usuario.salirDeGrupo(grupo);
				usuarioRepository.save(usuario);
			}

			return removed;
		}

		return false;
	}

	private void validarMiembrosParaGrupoPareja(List<Usuario> miembros) {
		if (miembros.size() != 2) {
			throw new GroupException("Un grupo de pareja debe tener exactamente 2 miembros.");
		}

		Usuario usuario1 = miembros.get(0);
		Usuario usuario2 = miembros.get(1);

		if (usuario1.equals(usuario2)) {
			throw new GroupException("No se pueden unir a un mismo miembro en una pareja");
		}

		if (existeGrupoParejaEntreUsuarios(usuario1, usuario2)) {
			throw new GroupException("Ya existe un grupo de pareja entre estos dos usuarios.");
		}
	}

	private void guardarUsuariosEnGrupo(List<Usuario> miembros, Grupo grupo) {
		List<Usuario> miembrosCopia = new ArrayList<>(miembros);
		for (Usuario miembro : miembrosCopia) {
			miembro.unirseAGrupo(grupo);
			usuarioRepository.save(miembro);
		}
	}

	private void validarCoupleGroupDTO(GrupoDTO grupoDTO) {

		if (grupoDTO.getMiembros() == null || grupoDTO.getMiembros().size() != 2) {
			throw new GroupException("Un grupo de pareja debe tener exactamente 2 miembros.");
		}

	}

	private void validarGrupoDTO(GrupoDTO grupoDTO) {
		if (grupoDTO.getNombre() == null || grupoDTO.getNombre().isEmpty()) {
			throw new GroupException("El nombre del grupo es obligatorio.");
		}

		Long categoriaId = grupoDTO.getCategoria() != null ? grupoDTO.getCategoria().getId() : null;
		if (categoriaId == null) {
			throw new GroupException("La categoría del grupo es obligatoria.");
		}
	}

	private Categoria obtenerCategoriaValidada(Long categoriaId) {
		Optional<Categoria> categoriaOptional = categoriaService.getCategoriaById(categoriaId);

		if (categoriaOptional.isEmpty()) {
			throw new GroupException("La categoría especificada no existe.");
		}

		Categoria categoria = categoriaOptional.get();
		if (!categoria.isGrupo()) {
			throw new GroupException("La categoría especificada no es de grupo.");
		}

		return categoria;
	}

	private List<Usuario> convertirDTOaUsuariosValidados(List<IdEmailUsuarioDTO> miembrosDTO) {
		return miembrosDTO.stream()
				.map(this::obtenerUsuarioValidado)
				.collect(Collectors.toList());
	}

	private Usuario obtenerUsuarioValidado(IdEmailUsuarioDTO idEmailUsuarioDTO) {
		Long usuarioId = idEmailUsuarioDTO.getId();
		Optional<Usuario> usuarioOptional = usuarioService.getById(usuarioId);

		if (usuarioOptional.isEmpty()) {
			throw new GroupException("El usuario con ID " + usuarioId + " no existe.");
		}

		return usuarioOptional.get();
	}

	private boolean existeGrupoParejaEntreUsuarios(Usuario usuario1, Usuario usuario2) {
		List<Grupo> gruposPareja = grupoRepository.findAllByEsParejaAndMiembrosIn(true,
				Arrays.asList(usuario1, usuario2));

		for (Grupo grupo : gruposPareja) {
			List<Usuario> miembros = grupo.getMiembros();
			if (miembros != null && miembros.size() == 2 && miembros.contains(usuario1)
					&& miembros.contains(usuario2)) {
				return true;
			}
		}

		return false;
	}
}
