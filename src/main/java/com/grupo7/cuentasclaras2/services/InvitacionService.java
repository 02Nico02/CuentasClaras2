package com.grupo7.cuentasclaras2.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public boolean enviarInvitacion(Long remitenteId, Long destinatarioId, Long grupoId) {
		Optional<Usuario> remitenteOptional = usuarioRepository.findById(remitenteId);
		Optional<Usuario> destinatarioOptional = usuarioRepository.findById(destinatarioId);
		Optional<Grupo> grupoOptional = grupoRepository.findById(grupoId);

		if (remitenteOptional.isPresent() && destinatarioOptional.isPresent() && grupoOptional.isPresent()) {
			Usuario remitente = remitenteOptional.get();
			Usuario destinatario = destinatarioOptional.get();
			Grupo grupo = grupoOptional.get();

			if (grupo.getMiembros().contains(remitente)) {
				Invitacion invitacion = new Invitacion();
				invitacion.setRemitente(remitente);
				invitacion.setDestinatario(destinatario);
				invitacion.setGrupo(grupo);

				invitacionRepository.save(invitacion);

				destinatario.addGroupInvitationReceived(invitacion);

				usuarioRepository.save(destinatario);

				return true;
			}
		}

		return false;
	}

	public void aceptarInvitacion(Usuario usuario, Long invitacionId) {
		Optional<Invitacion> invitacionOptional = invitacionRepository.findById(invitacionId);

		if (invitacionOptional.isPresent()) {
			Invitacion invitacion = invitacionOptional.get();
			if (invitacion.getDestinatario().equals(usuario)) {
				Grupo grupo = invitacion.getGrupo();
				grupoService.addMemberToGroup(grupo, usuario);
				invitacionRepository.delete(invitacion);
			}
		}
	}

	public void rechazarInvitacion(Usuario usuario, Long invitacionId) {
		Optional<Invitacion> invitacionOptional = invitacionRepository.findById(invitacionId);

		if (invitacionOptional.isPresent()) {
			Invitacion invitacion = invitacionOptional.get();
			if (invitacion.getDestinatario().equals(usuario)) {
				invitacionRepository.delete(invitacion);
			}
		}
	}
}
