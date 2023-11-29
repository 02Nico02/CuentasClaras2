package com.grupo7.cuentasclaras2.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Optional<Usuario> sendFriendRequest(String userEmail, String friendEmail) {
        Optional<Usuario> senderOptional = usuarioRepository.findByEmail(userEmail);
        Optional<Usuario> receiverOptional = usuarioRepository.findByEmail(friendEmail);

        if (senderOptional.isPresent() && receiverOptional.isPresent()) {
            Usuario sender = senderOptional.get();
            Usuario receiver = receiverOptional.get();

            if (sender.equals(receiver)) {
                return Optional.empty();
            }

            if (sender.getAmigos().contains(receiver)) {
                return Optional.empty();
            }

            if (invitacionAmistadRepository.findByRemitenteAndReceptor(sender, receiver).isPresent() ||
                    invitacionAmistadRepository.findByRemitenteAndReceptor(receiver, sender).isPresent()) {
                return Optional.empty();
            }
            InvitacionAmistad invitacion = new InvitacionAmistad(sender, receiver);
            invitacionAmistadRepository.save(invitacion);

            sender.agregarInvitacionAmistadEnviada(invitacion);
            receiver.agregarInvitacionAmistadRecibida(invitacion);

            usuarioRepository.save(sender);
            usuarioRepository.save(receiver);

            return Optional.of(invitacion.getRemitente());
        }

        return Optional.empty();
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
