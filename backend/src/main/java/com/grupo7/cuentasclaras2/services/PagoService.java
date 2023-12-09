package com.grupo7.cuentasclaras2.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo7.cuentasclaras2.DTO.PagoDTO;
import com.grupo7.cuentasclaras2.exception.BDErrorException;
import com.grupo7.cuentasclaras2.exception.PagoException;
import com.grupo7.cuentasclaras2.modelos.DeudaUsuario;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Pago;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.PagoRepository;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private DeudaUsuarioService deudaUsuarioService;

    public List<Pago> obtenerTodosLosPagos() {
        return pagoRepository.findAll();
    }

    public Optional<Pago> obtenerPagoPorId(long id) {
        return pagoRepository.findById(id);
    }

    public Pago guardarPago(Pago pago) {
        try {
            return pagoRepository.save(pago);
        } catch (DataAccessException e) {
            throw new BDErrorException("Error al guardar el pago en la base de datos", e);
        }
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

    @Transactional
    public Pago guardarPagoDesdeDTO(PagoDTO pagoDTO) {
        validarPagoDTO(pagoDTO);

        Usuario autor = usuarioService.getById(pagoDTO.getAutorId())
                .orElseThrow(() -> new PagoException("No se encontró el autor del pago"));

        Usuario destinatario = usuarioService.getById(pagoDTO.getDestinatarioId())
                .orElseThrow(() -> new PagoException("No se encontró el destinatario del pago"));

        Grupo grupo = grupoService.getGroupById(pagoDTO.getGrupoId())
                .orElseThrow(() -> new PagoException("No se encontró el grupo especificado"));

        DeudaUsuario deudaUsuario = deudaUsuarioService.obtenerDeudaEntreUsuariosEnGrupo(grupo.getId(),
                autor.getId(), destinatario.getId())
                .orElseThrow(() -> new PagoException("No se encontró una deuda entre "
                        + autor.getUsername() + " y " + destinatario.getUsername() + " en el grupo con ID "
                        + grupo.getId()));

        validarMontoDePago(pagoDTO.getMonto(), deudaUsuario.getMonto());

        Pago nuevoPago = new Pago();
        nuevoPago.setAutor(autor);
        nuevoPago.setDestinatario(destinatario);
        nuevoPago.setGrupo(grupo);
        nuevoPago.setMonto(pagoDTO.getMonto());

        Pago pagoGuardado = guardarPago(nuevoPago);

        deudaUsuarioService.realizarPago(deudaUsuario.getId(), pagoGuardado.getMonto());
        grupoService.addPaymentToGroup(grupo, pagoGuardado);

        return pagoGuardado;
    }

    private void validarPagoDTO(PagoDTO pagoDTO) {
        if (pagoDTO.getMonto() <= 0) {
            throw new PagoException("El monto del pago debe ser mayor que cero.");
        }

        if (pagoDTO.getAutorId() == null || pagoDTO.getDestinatarioId() == null) {
            throw new PagoException("No se ha proporcionado el autor o destinatario del pago");
        }

        if (pagoDTO.getAutorId().equals(pagoDTO.getDestinatarioId())) {
            throw new PagoException("No se puede realizar un pago a uno mismo");
        }
    }

    private void validarMontoDePago(double montoPago, double montoDeuda) {
        if (montoPago > montoDeuda) {
            throw new PagoException("El monto del pago es superior a la deuda");
        }
    }
}
