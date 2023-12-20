package com.grupo7.cuentasclaras2.services;

import java.util.List;
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

    @Autowired
    private GrupoService grupoService;

    /**
     * Envía una solicitud de amistad desde el remitente al destinatario.
     *
     * @param senderUser    El usuario que envía la solicitud.
     * @param recipientUser El usuario que recibe la solicitud.
     * @throws FriendshipException Si senderUser o recipientUser son nulos.
     * @throws FriendshipException Si senderUser y recipientUser son el mismo
     *                             usuario.
     * @throws FriendshipException Si senderUser y recipientUser ya son amigos.
     * @throws FriendshipException Si ya se envió una solicitud de amistad entre
     *                             senderUser y recipientUser.
     */
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

    /**
     * Acepta una solicitud de amistad y crea el grupo de pareja
     *
     * @param usuario      El usuario que acepta la solicitud.
     * @param invitationId El ID de la invitación que se acepta.
     * @return true si la operación se realiza con éxito, false si ocurre algún
     *         error.
     */
    public boolean aceptarSolicitudAmistad(Usuario usuario, Long invitationId) {
        try {
            Optional<InvitacionAmistad> invitacionOptional = invitacionAmistadRepository.findById(invitationId);

            if (invitacionOptional.isPresent()) {
                InvitacionAmistad invitacion = invitacionOptional.get();
                if (invitacion.getReceptor().equals(usuario)) {
                    Usuario remitente = invitacion.getRemitente();
                    usuario.agregarAmigo(remitente);
                    invitacionAmistadRepository.delete(invitacion);
                    grupoService.newCoupleGroup(List.of(usuario, remitente));
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Rechaza una solicitud de amistad.
     *
     * @param usuario      El usuario que rechaza la solicitud.
     * @param invitationId El ID de la invitación que se rechaza.
     * @return true si la operación se realiza con éxito, false si ocurre algún
     *         error.
     */
    public boolean rechazarSolicitudAmistad(Usuario usuario, Long invitationId) {
        try {
            Optional<InvitacionAmistad> invitacionOptional = invitacionAmistadRepository.findById(invitationId);

            if (invitacionOptional.isPresent()) {
                InvitacionAmistad invitacion = invitacionOptional.get();
                if (invitacion.getReceptor().equals(usuario)) {
                    invitacionAmistadRepository.delete(invitacion);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
