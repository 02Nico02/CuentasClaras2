package com.grupo7.cuentasclaras2.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo7.cuentasclaras2.DTO.GrupoDTO;
import com.grupo7.cuentasclaras2.DTO.IdEmailUsuarioDTO;
import com.grupo7.cuentasclaras2.modelos.Categoria;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Pago;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.GrupoRepository;

@Service
public class GrupoService {
	@Autowired
	private GrupoRepository grupoRepository;

	@Autowired
	private UsuarioService usuarioService;

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

			List<Usuario> miembros = grupo.getMiembros();

			if (miembros == null || !miembros.contains(usuario)) {
				if (grupo.getEsPareja() && miembros != null && miembros.size() >= 2) {
					return false;
				}
				grupo.agregarMiembro(usuario);
				grupoRepository.save(grupo);
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

			List<Usuario> miembros = grupo.getMiembros();

			if (miembros != null && !miembros.isEmpty()) {
				boolean removed = miembros.remove(usuario);

				if (removed) {
					grupoRepository.save(grupo);
				}

				return removed;
			}
		}

		return false;
	}

	public Optional<Grupo> newGroupByDTO(GrupoDTO grupoDTO) {
		if (grupoDTO.getNombre() == null || grupoDTO.getNombre().isEmpty()) {
			throw new IllegalArgumentException("El nombre del grupo es obligatorio.");
		}

		Grupo grupo = new Grupo();
		grupo.setNombre(grupoDTO.getNombre());
		grupo.setEsPareja(false);

		Long categoriaId = grupoDTO.getCategoria() != null ? grupoDTO.getCategoria().getId() : null;
		if (categoriaId == null) {
			throw new IllegalArgumentException("La categoría del grupo es obligatoria.");
		}

		Optional<Categoria> categoriaOptional = categoriaService.getCategoriaById(categoriaId);

		if (categoriaOptional.isEmpty()) {
			throw new IllegalArgumentException("La categoría especificada no existe.");
		}

		Categoria categoria = categoriaOptional.get();
		if (!categoria.isGrupo()) {
			throw new IllegalArgumentException("La categoría especificada no es de grupo.");
		}

		List<IdEmailUsuarioDTO> miembrosDTO = grupoDTO.getMiembros();
		if (miembrosDTO == null || miembrosDTO.isEmpty()) {
			throw new IllegalArgumentException("Se requiere al menos un miembro para crear un grupo.");
		}

		List<Usuario> miembros = convertirDTOaUsuarios(miembrosDTO);

		grupo.setMiembros(miembros);

		categoria.addGroup(grupo);
		grupo.setCategoria(categoria);

		categoriaService.saveCategoria(categoria);

		Grupo grupoGuardado = grupoRepository.save(grupo);
		return Optional.of(grupoGuardado);
	}

	private List<Usuario> convertirDTOaUsuarios(List<IdEmailUsuarioDTO> miembrosDTO) {
		return miembrosDTO.stream()
				.map(idEmailUsuarioDTO -> {
					Long usuarioId = idEmailUsuarioDTO.getId();
					Optional<Usuario> usuarioOptional = usuarioService.getById(usuarioId);

					if (usuarioOptional.isEmpty()) {
						throw new IllegalArgumentException("El usuario con ID " + usuarioId + " no existe.");
					}

					return usuarioOptional.get();
				})
				.collect(Collectors.toList());
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
}
