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
import com.grupo7.cuentasclaras2.modelos.Gasto;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Pago;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.GastoRepository;
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
	private GastoRepository gastoRepository;

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

	public List<Grupo> getGroupsByUserId(Long userId) {
		return grupoRepository.findByMiembros_IdAndEsParejaIsFalse(userId);
	}

	public List<Grupo> getGroupsWhereEsPareja(Long userId) {
		return grupoRepository.findByMiembros_IdAndEsParejaIsTrue(userId);
	}

	public boolean addMemberToGroup(Long groupId, Long userId) {
		Optional<Grupo> grupoOptional = grupoRepository.findById(groupId);
		Optional<Usuario> usuarioOptional = usuarioService.getById(userId);

		if (grupoOptional.isPresent() && usuarioOptional.isPresent()) {
			Grupo grupo = grupoOptional.get();
			Usuario usuario = usuarioOptional.get();

			if (grupo.getEsPareja()) {
				throw new GroupException("No se puede agregar miembros a una pareja");
			}
			if (agregarMiembroAGrupo(grupo, usuario)) {
				return true;
			}
		}

		return false;
	}

	public boolean addMemberToGroup(Grupo grupo, Usuario usuario) {
		if (grupo != null && usuario != null) {
			if (grupo.getEsPareja()) {
				throw new GroupException("No se puede agregar miembros a una pareja");
			}
			if (agregarMiembroAGrupo(grupo, usuario)) {
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

			if (grupo.getEsPareja()) {
				throw new GroupException("No se puede eliminar miembros de una pareja");
			}
			if (quitarMiembroDeGrupo(grupo, usuario)) {
				List<Gasto> gastosGrupo = grupo.getGastos();

				for (Gasto gasto : gastosGrupo) {
					if (usuarioParticipaEnGasto(usuario, gasto)) {
						gasto.setEditable(false);
						gastoRepository.save(gasto);
					}
				}
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
			throw new GroupException("Se debe proporcionar al creador del grupo.");
		}

		if (miembros.size() != 1) {
			throw new GroupException("Se debe proporcionar solo 1 miembro, el creador.");
		}

		grupo.agregarMiembro(miembros.get(0));
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
			boolean tieneDeudasPendientes = tieneDeudasPendientesEnGrupo(grupo, usuario);

			if (!tieneDeudasPendientes) {
				boolean removed = miembros.remove(usuario);
				if (removed) {
					usuario.salirDeGrupo(grupo);
					grupoRepository.save(grupo);
					usuarioRepository.save(usuario);
				}

				return removed;
			} else {
				throw new GroupException("El usuario tiene cuentas pendientes, no se puede eliminar");
			}
		}

		return false;
	}

	public Optional<Grupo> updateGroup(Long id, GrupoDTO grupoDTO) {
		Optional<Grupo> grupoOptional = getGroupById(id);
		if (grupoOptional.isPresent()) {
			Grupo grupo = grupoOptional.get();

			if (grupo.getEsPareja()) {
				throw new GroupException("No se puede actualizar los datos de una pareja");
			}

			if (grupoDTO.getNombre() != null && !grupoDTO.getNombre().isEmpty()) {
				grupo.setNombre(grupoDTO.getNombre());
			}

			if (grupoDTO.getCategoria() != null) {
				Categoria categoria = categoriaService.getCategoriaById(grupoDTO.getCategoria().getId())
						.orElseThrow(() -> new GroupException("Categoría no encontrada"));
				if (!categoria.isGrupo()) {
					throw new GroupException("La categoría elegida no es para un grupo");
				}
				grupo.setCategoria(categoria);
			}

			return Optional.of(saveGroup(grupo));
		} else {
			return Optional.empty();
		}
	}

	private boolean tieneDeudasPendientesEnGrupo(Grupo grupo, Usuario usuario) {
		return grupo.getDeudas().stream()
				.anyMatch(deuda -> deuda.getDeudor().equals(usuario) || deuda.getAcreedor().equals(usuario));
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

	private boolean usuarioParticipaEnGasto(Usuario usuario, Gasto gasto) {
		boolean esAutor = gasto.getGastoAutor().stream().anyMatch(ga -> ga.getIntegrante().equals(usuario));
		boolean esParticipante = gasto.getFormaDividir().getDivisionIndividual().stream()
				.anyMatch(ga -> ga.getUsuario().equals(usuario));
		return esAutor || esParticipante;
	}
}
