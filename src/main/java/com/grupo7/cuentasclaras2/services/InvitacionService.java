package com.grupo7.cuentasclaras2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo7.cuentasclaras2.exception.GroupException;
import com.grupo7.cuentasclaras2.exception.InvitationGroupException;
import com.grupo7.cuentasclaras2.exception.UserException;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Invitacion;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.GrupoRepository;
import com.grupo7.cuentasclaras2.repositories.InvitacionRepository;
import com.grupo7.cuentasclaras2.repositories.UsuarioRepository;

@Service
public class InvitacionService {
	@Autowired
	private InvitacionRepository invitacionRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private GrupoRepository grupoRepository;

	@Autowired
	private GrupoService grupoService;

	public void enviarInvitacion(Long remitenteId, Long destinatarioId, Long grupoId) {
		Usuario remitente = usuarioRepository.findById(remitenteId)
				.orElseThrow(() -> new UserException("Usuario remitente no encontrado"));

		Usuario destinatario = usuarioRepository.findById(destinatarioId)
				.orElseThrow(() -> new UserException("Usuario destinatario no encontrado"));

		Grupo grupo = grupoRepository.findById(grupoId)
				.orElseThrow(() -> new GroupException("Grupo no encontrado"));

		if (!grupo.getMiembros().contains(remitente)) {
			throw new GroupException("El remitente no es miembro del grupo");
		}

		Invitacion invitacion = new Invitacion();
		invitacion.setRemitente(remitente);
		invitacion.setDestinatario(destinatario);
		invitacion.setGrupo(grupo);

		invitacionRepository.save(invitacion);

		destinatario.addGroupInvitationReceived(invitacion);

		usuarioRepository.save(destinatario);
	}

	public void aceptarInvitacion(Usuario usuario, Long invitacionId) {
		Invitacion invitacion = invitacionRepository.findById(invitacionId)
				.orElseThrow(() -> new InvitationGroupException("Invitación no encontrada"));

		if (!invitacion.getDestinatario().equals(usuario)) {
			throw new InvitationGroupException("El usuario no es el destinatario de la invitación");
		}

		Grupo grupo = invitacion.getGrupo();
		grupoService.addMemberToGroup(grupo, usuario);
		invitacionRepository.delete(invitacion);
	}

	public void rechazarInvitacion(Usuario usuario, Long invitacionId) {
		Invitacion invitacion = invitacionRepository.findById(invitacionId)
				.orElseThrow(() -> new InvitationGroupException("Invitación no encontrada"));

		if (!invitacion.getDestinatario().equals(usuario)) {
			throw new InvitationGroupException("El usuario no es el destinatario de la invitación");
		}

		invitacionRepository.delete(invitacion);
	}
}
