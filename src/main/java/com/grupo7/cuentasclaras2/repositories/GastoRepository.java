package com.grupo7.cuentasclaras2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo7.cuentasclaras2.modelos.Gasto;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

}
