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
import com.grupo7.cuentasclaras2.exception.RecursoNoEncontradoException;
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

    /**
     * Obtiene todos los pagos existentes.
     *
     * @return Lista de todos los pagos.
     */
    public List<Pago> obtenerTodosLosPagos() {
        return pagoRepository.findAll();
    }

    /**
     * Obtiene un pago por su ID.
     *
     * @param id ID del pago a buscar.
     * @return Un Optional que contiene el pago si se encuentra, o vacío de lo
     *         contrario.
     */
    public Optional<Pago> obtenerPagoPorId(long id) {
        return pagoRepository.findById(id);
    }

    /**
     * Guarda un nuevo pago en la base de datos.
     * 
     * @param pago El pago a guardar.
     * @return El pago guardado.
     * @throws BDErrorException Si hay un error al guardar el pago en la base de
     *                          datos.
     */
    public Pago guardarPago(Pago pago) {
        try {
            return pagoRepository.save(pago);
        } catch (DataAccessException e) {
            throw new BDErrorException("Error al guardar el pago en la base de datos", e);
        }
    }

    /**
     * Elimina un pago por su ID.
     *
     * @param id ID del pago a eliminar.
     */
    public void eliminarPago(long id) {
        pagoRepository.deleteById(id);
    }

    /**
     * Obtiene todos los pagos asociados a un grupo.
     *
     * @param grupoId ID del grupo.
     * @return Lista de pagos asociados al grupo.
     */
    public List<Pago> obtenerPagosPorGrupo(long grupoId) {
        return pagoRepository.findByGrupoId(grupoId);
    }

    /**
     * Obtiene todos los pagos realizados por un usuario.
     *
     * @param usuarioId ID del usuario.
     * @return Lista de pagos realizados por el usuario.
     */
    public List<Pago> obtenerPagosRealizadosPorUsuario(long usuarioId) {
        return pagoRepository.findByAutorId(usuarioId);
    }

    /**
     * Obtiene todos los pagos recibidos por un usuario.
     *
     * @param usuarioId ID del usuario.
     * @return Lista de pagos recibidos por el usuario.
     */
    public List<Pago> obtenerPagosRecibidosPorUsuario(long usuarioId) {
        return pagoRepository.findByDestinatarioId(usuarioId);
    }

    /**
     * Guarda un nuevo pago utilizando la información proporcionada en un DTO.
     * 
     * @param pagoDTO El DTO que contiene la información del pago a guardar.
     * @return El pago que ha sido guardado con éxito.
     * @throws RecursoNoEncontradoException Si alguno de los recursos (Usuario,
     *                                      Grupo o Deuda) no se encuentra.
     */
    @Transactional
    public Pago guardarPagoDesdeDTO(PagoDTO pagoDTO) {
        validarPagoDTO(pagoDTO);

        Usuario autor = usuarioService.getById(pagoDTO.getAutorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", pagoDTO.getAutorId()));

        Usuario destinatario = usuarioService.getById(pagoDTO.getDestinatarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", pagoDTO.getDestinatarioId()));

        Grupo grupo = grupoService.getGroupById(pagoDTO.getGrupoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo", pagoDTO.getGrupoId()));

        DeudaUsuario deudaUsuario = deudaUsuarioService.obtenerDeudaEntreUsuariosEnGrupo(grupo.getId(),
                autor.getId(), destinatario.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Deuda", "Entre " + autor.getUsername() + " y "
                        + destinatario.getUsername() + " en el grupo con ID " + grupo.getId()));

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

    /**
     * Valida los datos de un objeto PagoDTO antes de crear un nuevo pago.
     *
     * @param pagoDTO El objeto PagoDTO a validar.
     * @throws PagoException Si el monto es menor o igual a cero, si no se
     *                       proporciona el autor o destinatario del pago,
     *                       o si el autor y destinatario son el mismo usuario.
     */
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

    /**
     * Valida que el monto del pago no sea mayor que la deuda existente.
     *
     * @param montoPago  El monto del pago.
     * @param montoDeuda El monto de la deuda existente.
     * @throws PagoException Si el monto del pago es mayor que la deuda.
     */
    private void validarMontoDePago(double montoPago, double montoDeuda) {
        if (montoPago > montoDeuda) {
            throw new PagoException("El monto del pago es superior a la deuda");
        }
    }
}
