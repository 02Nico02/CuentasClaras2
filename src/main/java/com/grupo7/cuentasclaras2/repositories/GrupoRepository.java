package com.grupo7.cuentasclaras2.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo7.cuentasclaras2.modelos.Grupo;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    Optional<Grupo> findByNombre(String nombre);
}
