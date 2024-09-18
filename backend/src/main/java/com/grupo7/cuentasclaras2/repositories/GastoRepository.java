package com.grupo7.cuentasclaras2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo7.cuentasclaras2.modelos.Gasto;

public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findByGrupoId(Long groupId);

    List<Gasto> findByGrupoIdAndCategoriaId(Long groupId, Long categoryId);

}
