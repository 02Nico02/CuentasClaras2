package com.grupo7.cuentasclaras2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo7.cuentasclaras2.modelos.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByGrupoId(long grupoId);

    List<Pago> findByAutorId(long autorId);

    List<Pago> findByDestinatarioId(long destinatarioId);
}
