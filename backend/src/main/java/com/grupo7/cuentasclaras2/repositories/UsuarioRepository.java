package com.grupo7.cuentasclaras2.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    boolean existsByUsername(String userName);

    boolean existsByEmail(String email);

    boolean existsByIdAndGrupos_Id(long usuarioId, long grupoId);

    @Query("SELECT u FROM Usuario u WHERE NOT EXISTS (SELECT 1 FROM Grupo g JOIN g.miembros m WHERE g.id = :idGrupo AND u = m) AND u.username LIKE %:usernameQuery%")
    List<Usuario> findUsersNotInGroupByQuery(@Param("idGrupo") Long idGrupo,
            @Param("usernameQuery") String usernameQuery);

    @Query("SELECT u FROM Usuario u " +
            "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :usernameQuery, '%')) " +
            "AND u.id <> :id " +
            "AND u NOT IN (SELECT a FROM Usuario usuario JOIN usuario.amigos a WHERE usuario.id = :id) " +
            "AND u NOT IN (SELECT ir.receptor FROM InvitacionAmistad ir WHERE ir.remitente.id = :id) " +
            "AND u NOT IN (SELECT ie.remitente FROM InvitacionAmistad ie WHERE ie.receptor.id = :id)")
    List<Usuario> findUsersByUsernameNotFriends(@Param("usernameQuery") String usernameQuery, @Param("id") long id);

}
