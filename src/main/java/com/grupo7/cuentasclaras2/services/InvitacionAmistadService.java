package com.grupo7.cuentasclaras2.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo7.cuentasclaras2.exception.FriendshipException;
import com.grupo7.cuentasclaras2.modelos.InvitacionAmistad;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.InvitacionAmistadRepository;
import com.grupo7.cuentasclaras2.repositories.UsuarioRepository;

@Service
@Transactional
public class InvitacionAmistadService {
    @Autowired
    private InvitacionAmistadRepository invitacionAmistadRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void sendFriendRequest(Usuario senderUser, Usuario recipientUser) {
        if (senderUser == null || recipientUser == null) {
            throw new FriendshipException("SenderUser y recipientUser no pueden ser nulos");
        }

        if (senderUser.equals(recipientUser)) {
            throw new FriendshipException("El emisor y receptor son el mismo");
        }

        if (senderUser.getAmigos().contains(recipientUser)) {
            throw new FriendshipException("Ya son amigos");
        }

        if (invitacionAmistadRepository.findByRemitenteAndReceptor(senderUser, recipientUser).isPresent() ||
                invitacionAmistadRepository.findByRemitenteAndReceptor(recipientUser, senderUser).isPresent()) {
            throw new FriendshipException("Ya se envió una solicitud de amistad");
        }

        InvitacionAmistad invitacion = new InvitacionAmistad(senderUser, recipientUser);
        invitacionAmistadRepository.save(invitacion);

        senderUser.agregarInvitacionAmistadEnviada(invitacion);
        recipientUser.agregarInvitacionAmistadRecibida(invitacion);

        usuarioRepository.save(senderUser);
        usuarioRepository.save(recipientUser);
    }

    public void aceptarSolicitudAmistad(Usuario usuario, Long invitationId) {
        Optional<InvitacionAmistad> invitacionOptional = invitacionAmistadRepository.findById(invitationId);

        invitacionOptional.ifPresent(invitacion -> {
            if (invitacion.getReceptor().equals(usuario)) {
                usuario.agregarAmigo(invitacion.getRemitente());
                invitacionAmistadRepository.delete(invitacion);
            }
        });
    }

    public void rechazarSolicitudAmistad(Usuario usuario, Long invitationId) {
        Optional<InvitacionAmistad> invitacionOptional = invitacionAmistadRepository.findById(invitationId);

        invitacionOptional.ifPresent(invitacion -> {
            if (invitacion.getReceptor().equals(usuario)) {
                invitacionAmistadRepository.delete(invitacion);
            }
        });
    }
}
