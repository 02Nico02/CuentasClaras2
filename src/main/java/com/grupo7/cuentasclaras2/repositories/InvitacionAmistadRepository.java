package com.grupo7.cuentasclaras2.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo7.cuentasclaras2.modelos.InvitacionAmistad;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public interface InvitacionAmistadRepository extends JpaRepository<InvitacionAmistad, Long> {
	Optional<InvitacionAmistad> findByRemitenteAndReceptor(Usuario remitente, Usuario receptor);

	List<InvitacionAmistad> findByReceptor(Usuario receptor);

	List<InvitacionAmistad> findByRemitente(Usuario remitente);

	long countByReceptor(Usuario receptor);

	void deleteByRemitenteAndReceptor(Usuario remitente, Usuario receptor);
}
