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
import com.grupo7.cuentasclaras2.DTO.MiembrosGrupoDTO;
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

	/**
	 * Obtiene todos los grupos.
	 *
	 * @return Lista de todos los grupos.
	 */
	public List<Grupo> getAllGroups() {
		return grupoRepository.findAll();
	}

	/**
	 * Obtiene un grupo por su identificador que no sea un grupo pareja.
	 *
	 * @param id Identificador del grupo.
	 * @return Optional que contiene el Grupo correspondiente al identificador, si
	 *         existe y no es un grupo pareja.
	 */
	public Optional<Grupo> getNonParejaGroupById(Long id) {
		return grupoRepository.findByIdAndEsParejaIsFalse(id);
	}

	/**
	 * Obtiene un grupo pareja por su identificador.
	 *
	 * @param id Identificador del grupo pareja.
	 * @return Optional que contiene el Grupo correspondiente al identificador, si
	 *         es un grupo pareja.
	 */
	public Optional<Grupo> getParejaGroupById(Long id) {
		return grupoRepository.findByIdAndEsParejaIsTrue(id);
	}

	/**
	 * Obtiene un grupo por su identificador.
	 *
	 * @param id Identificador del grupo.
	 * @return Optional que contiene el Grupo correspondiente al identificador, si
	 *         existe.
	 */
	public Optional<Grupo> getGroupById(Long id) {
		return grupoRepository.findById(id);
	}

	/**
	 * Guarda un grupo.
	 *
	 * @param grupo Grupo a guardar.
	 * @return Grupo guardado.
	 */
	public Grupo saveGroup(Grupo grupo) {
		return grupoRepository.save(grupo);
	}

	/**
	 * Elimina un grupo por su ID.
	 *
	 * @param id ID del grupo a eliminar.
	 */
	public void deleteGroup(Long id) {
		grupoRepository.deleteById(id);
	}

	/**
	 * Obtiene un grupo por su nombre.
	 *
	 * @param nombre Nombre del grupo.
	 * @return Optional que contiene el Grupo correspondiente al nombre, si existe.
	 */
	public Optional<Grupo> getGroupByNombre(String nombre) {
		return grupoRepository.findByNombre(nombre);
	}

	/**
	 * Obtiene los grupos asociados a un usuario por su identificador.
	 *
	 * @param userId Identificador del usuario.
	 * @return Lista de grupos asociados al usuario.
	 */
	public List<Grupo> getGroupsByUserId(Long userId) {
		return grupoRepository.findByMiembros_IdAndEsParejaIsFalse(userId);
	}

	/**
	 * Obtiene los grupos de tipo pareja donde participa un usuario por su
	 * identificador.
	 *
	 * @param userId Identificador del usuario.
	 * @return Lista de grupos de tipo pareja en los que participa el usuario.
	 */
	public List<Grupo> getGroupsWhereEsPareja(Long userId) {
		return grupoRepository.findByMiembros_IdAndEsParejaIsTrue(userId);
	}

	/**
	 * Busca un grupo de tipo pareja en el que participen dos usuarios.
	 *
	 * @param userId1 Identificador del primer usuario.
	 * @param userId2 Identificador del segundo usuario.
	 * @return Grupo de tipo pareja en el que participan los dos usuarios.
	 */
	public Optional<Grupo> getPairGroupByUserIds(Long userId1, Long userId2) {
		// Verifica si ambos usuarios pertenecen al mismo grupo de tipo pareja
		List<Grupo> pairGroups = grupoRepository.findByMiembros_IdInAndEsParejaIsTrue(Arrays.asList(userId1, userId2));

		return pairGroups.stream().findFirst();
	}

	/**
	 * Agrega un miembro a un grupo.
	 *
	 * @param groupId Identificador del grupo.
	 * @param userId  Identificador del usuario a agregar.
	 * @return true si se agregó correctamente, false si no.
	 * @throws GroupException Si se intenta agregar un miembro a un grupo pareja.
	 */
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

	/**
	 * Agrega un miembro a un grupo.
	 *
	 * @param grupo   Grupo al que se agrega el miembro.
	 * @param usuario Usuario a agregar al grupo.
	 * @return true si se agregó correctamente, false si no.
	 * @throws GroupException Si se intenta agregar un miembro a un grupo pareja.
	 */
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

	/**
	 * Elimina un miembro de un grupo.
	 *
	 * @param groupId  Identificador del grupo.
	 * @param memberId Identificador del miembro a eliminar.
	 * @return true si se eliminó correctamente, false si no.
	 * @throws GroupException Si se intenta eliminar un miembro a un grupo pareja.
	 */
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

	/**
	 * Crea un nuevo grupo a partir de un DTO y lo guarda.
	 *
	 * @param grupoDTO DTO con la información del grupo a crear.
	 * @return Optional que contiene el grupo guardado, si la creación fue exitosa.
	 * @throws GroupException Si hay algún problema durante la creación del grupo.
	 */
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

	/**
	 * Crea un nuevo grupo de pareja a partir de un DTO y lo guarda.
	 *
	 * @param grupoDTO DTO con la información del grupo de pareja a crear.
	 * @return Optional que contiene el grupo de pareja guardado, si la creación fue
	 *         exitosa.
	 * @throws GroupException Si hay algún problema durante la creación del grupo de
	 *                        pareja.
	 */
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

	@Transactional
	public Optional<Grupo> newCoupleGroup(List<Usuario> miembros) {
		if (miembros == null || miembros.size() != 2) {
			throw new GroupException("Un grupo de pareja debe tener exactamente 2 miembros.");
		}

		Grupo grupo = new Grupo();
		grupo.setEsPareja(true);

		validarMiembrosParaGrupoPareja(miembros);

		grupo.agregarMiembros(miembros);
		Grupo grupoGuardado = grupoRepository.save(grupo);
		usuarioRepository.saveAll(miembros);

		return Optional.of(grupoGuardado);
	}

	/**
	 * Agrega un pago a un grupo.
	 *
	 * @param grupo Grupo al que se agrega el pago.
	 * @param pago  Pago a agregar.
	 * @return true si se agregó correctamente, false si no.
	 */
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

	/**
	 * Agrega un miembro a un grupo.
	 *
	 * @param grupo   Grupo al que se agrega el miembro.
	 * @param usuario Usuario a agregar al grupo.
	 * @return true si se agregó correctamente, false si no.
	 */
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

	/**
	 * Elimina un miembro de un grupo.
	 *
	 * @param grupo   Grupo del que se elimina el miembro.
	 * @param usuario Usuario a eliminar del grupo.
	 * @return true si se eliminó correctamente, false si no.
	 * @throws GroupException Si el usuario tiene cuentas pendientes.
	 */
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

	/**
	 * Actualiza la información de un grupo.
	 *
	 * @param id       Identificador del grupo a actualizar.
	 * @param grupoDTO DTO con la información actualizada del grupo.
	 * @return Optional que contiene el grupo actualizado, si la actualización fue
	 *         exitosa.
	 * @throws GroupException Si hay algún problema durante la actualización del
	 *                        grupo.
	 */
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

	/**
	 * Verifica si un usuario pertenece a un grupo.
	 *
	 * @param usuarioId Identificador del usuario.
	 * @param grupoId   Identificador del grupo.
	 * @return true si el usuario pertenece al grupo, false si no.
	 */
	public boolean usuarioPerteneceAlGrupo(long usuarioId, long grupoId) {
		return grupoRepository.existsByIdAndMiembros_Id(grupoId, usuarioId);
	}

	/**
	 * Verifica si un usuario tiene deudas pendientes en un grupo.
	 *
	 * @param grupo   Grupo en el que se verifica la existencia de deudas
	 *                pendientes.
	 * @param usuario Usuario del que se verifica la existencia de deudas
	 *                pendientes.
	 * @return true si el usuario tiene deudas pendientes, false si no.
	 */
	private boolean tieneDeudasPendientesEnGrupo(Grupo grupo, Usuario usuario) {
		return grupo.getDeudas().stream()
				.anyMatch(deuda -> deuda.getDeudor().equals(usuario) || deuda.getAcreedor().equals(usuario));
	}

	/**
	 * Valida la lista de miembros para la creación de un grupo de pareja.
	 *
	 * @param miembros La lista de usuarios que se desea validar para el grupo de
	 *                 pareja.
	 * @throws GroupException Si la lista de miembros no tiene exactamente 2
	 *                        elementos,
	 *                        si se intenta unir a un mismo miembro en la pareja o
	 *                        si ya existe un grupo de pareja entre los dos
	 *                        usuarios.
	 */
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

	/**
	 * Guarda los usuarios en el grupo especificado, actualizando su pertenencia al
	 * grupo en la base de datos.
	 *
	 * @param miembros La lista de usuarios que se agregarán al grupo.
	 * @param grupo    El grupo al que se agregarán los usuarios.
	 */
	private void guardarUsuariosEnGrupo(List<Usuario> miembros, Grupo grupo) {
		List<Usuario> miembrosCopia = new ArrayList<>(miembros);
		for (Usuario miembro : miembrosCopia) {
			miembro.unirseAGrupo(grupo);
			usuarioRepository.save(miembro);
		}
	}

	/**
	 * Valida un objeto GrupoDTO para asegurar que cumpla con los requisitos de un
	 * grupo de pareja.
	 *
	 * @param grupoDTO El objeto GrupoDTO que se desea validar.
	 * @throws GroupException Si el objeto GrupoDTO no tiene exactamente 2 miembros.
	 */
	private void validarCoupleGroupDTO(GrupoDTO grupoDTO) {

		if (grupoDTO.getMiembros() == null || grupoDTO.getMiembros().size() != 2) {
			throw new GroupException("Un grupo de pareja debe tener exactamente 2 miembros.");
		}

	}

	/**
	 * Valida un objeto GrupoDTO para asegurar que cumpla con los requisitos básicos
	 * de un grupo.
	 *
	 * @param grupoDTO El objeto GrupoDTO que se desea validar.
	 * @throws GroupException Si el nombre del grupo es nulo o vacío, o si la
	 *                        categoría del grupo es nula.
	 */
	private void validarGrupoDTO(GrupoDTO grupoDTO) {
		if (grupoDTO.getNombre() == null || grupoDTO.getNombre().isEmpty()) {
			throw new GroupException("El nombre del grupo es obligatorio.");
		}

		Long categoriaId = grupoDTO.getCategoria() != null ? grupoDTO.getCategoria().getId() : null;
		if (categoriaId == null) {
			throw new GroupException("La categoría del grupo es obligatoria.");
		}
	}

	/**
	 * Obtiene y valida una categoría por su identificador.
	 *
	 * @param categoriaId El identificador de la categoría que se desea obtener y
	 *                    validar.
	 * @return La categoría validada.
	 * @throws GroupException Si la categoría con el identificador especificado no
	 *                        existe o no es de grupo.
	 */
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

	/**
	 * Convierte una lista de DTO de usuarios a una lista de usuarios validados.
	 *
	 * @param miembrosDTO La lista de DTO de usuarios que se desea convertir y
	 *                    validar.
	 * @return La lista de usuarios validados.
	 */
	private List<Usuario> convertirDTOaUsuariosValidados(List<MiembrosGrupoDTO> miembrosDTO) {
		return miembrosDTO.stream()
				.map(this::obtenerUsuarioValidado)
				.collect(Collectors.toList());
	}

	/**
	 * Obtiene y valida un usuario por su identificador.
	 *
	 * @param miembrosGrupoDTO El DTO que contiene el identificador del usuario que
	 *                         se desea obtener y validar.
	 * @return El usuario validado.
	 * @throws GroupException Si el usuario con el identificador especificado no
	 *                        existe.
	 */
	private Usuario obtenerUsuarioValidado(MiembrosGrupoDTO miembrosGrupoDTO) {
		Long usuarioId = miembrosGrupoDTO.getIdUsuario();
		Optional<Usuario> usuarioOptional = usuarioService.getById(usuarioId);

		if (usuarioOptional.isEmpty()) {
			throw new GroupException("El usuario con ID " + usuarioId + " no existe.");
		}

		return usuarioOptional.get();
	}

	/**
	 * Verifica si ya existe un grupo de pareja entre dos usuarios específicos.
	 *
	 * @param usuario1 El primer usuario.
	 * @param usuario2 El segundo usuario.
	 * @return true si ya existe un grupo de pareja entre los dos usuarios, false en
	 *         caso contrario.
	 */
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

	/**
	 * Verifica si un usuario participa en un gasto dado, ya sea como autor del
	 * gasto o como participante
	 * en la división individual del mismo.
	 *
	 * @param usuario El usuario que se desea verificar si participa en el gasto.
	 * @param gasto   El gasto en el que se quiere verificar la participación del
	 *                usuario.
	 * @return true si el usuario es autor del gasto o participa en la división
	 *         individual, false en caso contrario.
	 */
	private boolean usuarioParticipaEnGasto(Usuario usuario, Gasto gasto) {
		boolean esAutor = gasto.getGastoAutor().stream().anyMatch(ga -> ga.getIntegrante().equals(usuario));
		boolean esParticipante = gasto.getFormaDividir().getDivisionIndividual().stream()
				.anyMatch(ga -> ga.getUsuario().equals(usuario));
		return esAutor || esParticipante;
	}
}
