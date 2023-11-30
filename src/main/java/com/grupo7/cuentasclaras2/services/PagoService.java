package com.grupo7.cuentasclaras2.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo7.cuentasclaras2.modelos.Pago;
import com.grupo7.cuentasclaras2.repositories.PagoRepository;

@Service
public class PagoService {
    @Autowired
    private PagoRepository pagoRepository;

    public List<Pago> obtenerTodosLosPagos() {
        return pagoRepository.findAll();
    }

    public Optional<Pago> obtenerPagoPorId(long id) {
        return pagoRepository.findById(id);
    }

    public Pago guardarPago(Pago pago) {
        return pagoRepository.save(pago);
    }

    public void eliminarPago(long id) {
        pagoRepository.deleteById(id);
    }

    public List<Pago> obtenerPagosPorGrupo(long grupoId) {
        return pagoRepository.findByGrupoId(grupoId);
    }

    public List<Pago> obtenerPagosRealizadosPorUsuario(long usuarioId) {
        return pagoRepository.findByAutorId(usuarioId);
    }

    public List<Pago> obtenerPagosRecibidosPorUsuario(long usuarioId) {
        return pagoRepository.findByDestinatarioId(usuarioId);
    }
}
