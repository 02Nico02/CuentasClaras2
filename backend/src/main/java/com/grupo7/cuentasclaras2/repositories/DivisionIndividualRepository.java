package com.grupo7.cuentasclaras2.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo7.cuentasclaras2.modelos.DivisionIndividual;

public interface DivisionIndividualRepository extends JpaRepository<DivisionIndividual, Long> {

    Optional<DivisionIndividual> findByIdAndFormaDividirId(Long id, long id2);

    Optional<DivisionIndividual> findByIdAndFormaDividirIdAndUsuarioId(Long id, Long formaDividirId, Long userId);

}
