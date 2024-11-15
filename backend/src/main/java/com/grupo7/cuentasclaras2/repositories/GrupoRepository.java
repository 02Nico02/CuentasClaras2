package com.grupo7.cuentasclaras2.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    Optional<Grupo> findByNombre(String nombre);

    Optional<Grupo> findByIdAndEsParejaIsFalse(Long groupId);

    Optional<Grupo> findByIdAndEsParejaIsTrue(Long groupId);

    boolean existsByEsParejaAndMiembrosIn(boolean b, List<Usuario> asList);

    List<Grupo> findAllByEsParejaAndMiembrosIn(boolean esPareja, List<Usuario> miembros);

    List<Grupo> findByMiembros_IdAndEsParejaIsFalse(Long userId);

    List<Grupo> findByMiembros_IdAndEsParejaIsTrue(Long userId);

    boolean existsByIdAndMiembros_Id(long grupoId, long usuarioId);

    List<Grupo> findByMiembros_IdInAndEsParejaIsTrue(List<Long> asList);

}
