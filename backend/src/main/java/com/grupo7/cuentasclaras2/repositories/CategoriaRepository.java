package com.grupo7.cuentasclaras2.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo7.cuentasclaras2.modelos.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Categoria findByNombre(String nombre);

    List<Categoria> findByGrupoTrue();

    List<Categoria> findByGrupoFalse();

    List<Categoria> findByNombreContainingIgnoreCase(String nombre);

    List<Categoria> findByFechaCreacionBefore(Date fecha);

    List<Categoria> findByFechaActualizacionAfter(Date fecha);
}
