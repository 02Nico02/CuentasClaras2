package com.grupo7.cuentasclaras2.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo7.cuentasclaras2.modelos.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByGrupos_Id(long grupoId);

    List<Usuario> findByAmigos_Id(long amigoId);

    List<Usuario> findByNombresIgnoreCaseContainingOrApellidoIgnoreCaseContaining(String nombre, String apellido);

    List<Usuario> findByInvitacionesGrupo_Destinatario_Id(long destinatarioId);

    List<Usuario> findByInvitacionesAmigosRecibidas_Id(long invitacionAmistadId);
}
