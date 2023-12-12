package com.grupo7.cuentasclaras2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo7.cuentasclaras2.modelos.Invitacion;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public interface InvitacionRepository extends JpaRepository<Invitacion, Long> {

    boolean existsByRemitenteAndDestinatario(Usuario remitente, Usuario destinatario);

}
