package com.grupo7.cuentasclaras2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo7.cuentasclaras2.repositories.PagoRepository;

@Service
public class PagoService {
    @Autowired
    private PagoRepository pagoRepositorio;
}
