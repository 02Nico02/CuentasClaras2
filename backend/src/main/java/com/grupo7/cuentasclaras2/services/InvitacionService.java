package com.grupo7.cuentasclaras2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo7.cuentasclaras2.exception.GroupException;
import com.grupo7.cuentasclaras2.exception.InvitacionException;
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

	/**
	 * Envía una invitación desde el remitente a un destinatario para unirse a un
	 * grupo.
	 *
	 * @param remitente      El remitente de la invitación.
	 * @param destinatarioId El ID del destinatario de la invitación.
	 * @param grupoId        El ID del grupo al que se invita al destinatario.
	 * @throws UserException       Si remitente y destinatario son el mismo usuario.
	 * @throws UserException       Si el usuario destinatario no se encuentra.
	 * @throws GroupException      Si el grupo no se encuentra o el remitente no es
	 *                             miembro del grupo.
	 * @throws GroupException      Si el grupo es una pareja (no se pueden enviar
	 *                             invitaciones en grupos de 2).
	 * @throws InvitacionException Si ya existe una invitación entre el remitente y
	 *                             el destinatario.
	 */
	public void enviarInvitacion(Usuario remitente, Long destinatarioId, Long grupoId) {

		if (remitente.getId() == destinatarioId) {
			throw new UserException("remitente y destinatario son el mismo");
		}

		Usuario destinatario = usuarioRepository.findById(destinatarioId)
				.orElseThrow(() -> new UserException("Usuario destinatario no encontrado"));

		Grupo grupo = grupoRepository.findById(grupoId)
				.orElseThrow(() -> new GroupException("Grupo no encontrado"));

		if (!grupo.getMiembros().contains(remitente)) {
			throw new GroupException("El remitente no es miembro del grupo");
		}

		if (grupo.getEsPareja()) {
			throw new GroupException("No se puede enviar invitaciones de un grupo de 2");
		}

		if (invitacionRepository.existsByRemitenteAndDestinatario(remitente, destinatario)
				|| invitacionRepository.existsByRemitenteAndDestinatario(destinatario, remitente)) {
			throw new InvitacionException("Ya existe una invitación entre el remitente y el destinatario");
		}

		Invitacion invitacion = new Invitacion();
		invitacion.setRemitente(remitente);
		invitacion.setDestinatario(destinatario);
		invitacion.setGrupo(grupo);

		invitacionRepository.save(invitacion);

		destinatario.addGroupInvitationReceived(invitacion);

		usuarioRepository.save(destinatario);
	}

	/**
	 * Acepta una invitación para unirse a un grupo.
	 *
	 * @param usuario      El usuario que acepta la invitación.
	 * @param invitacionId El ID de la invitación que se acepta.
	 * @throws InvitationGroupException Si la invitación no se encuentra.
	 * @throws InvitationGroupException Si el usuario no es el destinatario de la
	 *                                  invitación.
	 */
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

	/**
	 * Rechaza una invitación para unirse a un grupo.
	 *
	 * @param usuario      El usuario que rechaza la invitación.
	 * @param invitacionId El ID de la invitación que se rechaza.
	 * @throws InvitationGroupException Si la invitación no se encuentra.
	 * @throws InvitationGroupException Si el usuario no es el destinatario de la
	 *                                  invitación.
	 */
	public void rechazarInvitacion(Usuario usuario, Long invitacionId) {
		Invitacion invitacion = invitacionRepository.findById(invitacionId)
				.orElseThrow(() -> new InvitationGroupException("Invitación no encontrada"));

		if (!invitacion.getDestinatario().equals(usuario)) {
			throw new InvitationGroupException("El usuario no es el destinatario de la invitación");
		}

		invitacionRepository.delete(invitacion);
	}
}
