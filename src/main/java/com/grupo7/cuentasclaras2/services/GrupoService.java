package com.grupo7.cuentasclaras2.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.GrupoRepository;
import com.grupo7.cuentasclaras2.repositories.UsuarioRepository;

@Service
public class GrupoService {
	@Autowired
	private GrupoRepository grupoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

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
		Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);

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
		Optional<Usuario> usuarioOptional = usuarioRepository.findById(memberId);

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
