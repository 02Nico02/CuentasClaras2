package com.grupo7.cuentasclaras2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo7.cuentasclaras2.modelos.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
