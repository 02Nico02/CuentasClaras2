package com.grupo7.cuentasclaras2.serviceTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Pago;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.GrupoRepository;
import com.grupo7.cuentasclaras2.repositories.PagoRepository;
import com.grupo7.cuentasclaras2.repositories.UsuarioRepository;
import com.grupo7.cuentasclaras2.services.PagoService;

@DataJpaTest
public class PagoServiceTests {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Test
    void testObtenerTodosLosPagos() {
        List<Pago> pagos = pagoService.obtenerTodosLosPagos();
        assertNotNull(pagos);
    }

    @Test
    void testObtenerPagoPorId() {
        Pago pago = new Pago();
        pago.setMonto(50.0);
        pagoRepository.save(pago);

        Long pagoId = pago.getId();
        assertNotNull(pagoId);

        Optional<Pago> pagoObtenido = pagoService.obtenerPagoPorId(pagoId);
        assertTrue(pagoObtenido.isPresent());
        assertEquals(pagoId, pagoObtenido.get().getId());
    }

    @Test
    void testGuardarPago() {
        Pago pago = new Pago();
        pago.setMonto(75.0);

        Pago pagoGuardado = pagoService.guardarPago(pago);
        assertNotNull(pagoGuardado.getId());

        Optional<Pago> pagoObtenido = pagoRepository.findById(pagoGuardado.getId());
        assertTrue(pagoObtenido.isPresent());
        assertEquals(pago.getMonto(), pagoObtenido.get().getMonto());
    }

    @Test
    void testEliminarPago() {
        Pago pago = new Pago();
        pago.setMonto(30.0);
        pagoRepository.save(pago);

        Long pagoId = pago.getId();
        assertNotNull(pagoId);

        pagoService.eliminarPago(pagoId);

        Optional<Pago> pagoEliminado = pagoRepository.findById(pagoId);
        assertFalse(pagoEliminado.isPresent());
    }

    @Test
    void testObtenerPagosPorGrupo() {
        Grupo grupo = new Grupo();
        grupoRepository.save(grupo);

        Pago pago1 = new Pago();
        pago1.setGrupo(grupo);
        pago1.setMonto(20.0);
        pagoRepository.save(pago1);

        Pago pago2 = new Pago();
        pago2.setGrupo(grupo);
        pago2.setMonto(25.0);
        pagoRepository.save(pago2);

        List<Pago> pagos = pagoService.obtenerPagosPorGrupo(grupo.getId());
        assertNotNull(pagos);
        assertEquals(2, pagos.size());
    }

    @Test
    void testObtenerPagosRealizadosPorUsuario() {
        Usuario autor = new Usuario("autor", "NombreAutor", "ApellidoAutor", "autor@example.com", "password");
        usuarioRepository.save(autor);

        Pago pago1 = new Pago();
        pago1.setAutor(autor);
        pago1.setMonto(15.0);
        pagoRepository.save(pago1);

        Pago pago2 = new Pago();
        pago2.setAutor(autor);
        pago2.setMonto(30.0);
        pagoRepository.save(pago2);

        List<Pago> pagos = pagoService.obtenerPagosRealizadosPorUsuario(autor.getId());
        assertNotNull(pagos);
        assertEquals(2, pagos.size());
    }

    @Test
    void testObtenerPagosRecibidosPorUsuario() {
        Usuario destinatario = new Usuario("destinatario", "NombreDestinatario", "ApellidoDestinatario",
                "destinatario@example.com", "password");
        usuarioRepository.save(destinatario);

        Pago pago1 = new Pago();
        pago1.setDestinatario(destinatario);
        pago1.setMonto(40.0);
        pagoRepository.save(pago1);

        Pago pago2 = new Pago();
        pago2.setDestinatario(destinatario);
        pago2.setMonto(50.0);
        pagoRepository.save(pago2);

        List<Pago> pagos = pagoService.obtenerPagosRecibidosPorUsuario(destinatario.getId());
        assertNotNull(pagos);
        assertEquals(2, pagos.size());
    }

    @Test
    void testActualizarPago() {
        Pago pago = new Pago();
        pago.setMonto(50.0);

        Pago nuevoPago = pagoService.guardarPago(pago);

        assertNotNull(nuevoPago);
        assertEquals(50.0, nuevoPago.getMonto());

        nuevoPago.setMonto(75.0);
        Pago pagoActualizado = pagoService.guardarPago(nuevoPago);

        assertNotNull(pagoActualizado);
        assertEquals(75.0, pagoActualizado.getMonto());
    }

    @Test
    void testEliminarPagoInexistente() {
        List<Pago> pagos = pagoService.obtenerTodosLosPagos();
        int cantidadDePagosAntesDeBorrado = pagos.size();

        pagoService.eliminarPago(-1L);

        List<Pago> pagosDespuesDelBorrado = pagoService.obtenerTodosLosPagos();
        assertEquals(cantidadDePagosAntesDeBorrado, pagosDespuesDelBorrado.size());

    }

    // NO SE QUE HACER
    // @Test
    // void testPagosEnGruposEliminado() {
    // // Pago pago = new Pago();
    // // pago.setMonto(100.0);

    // }

    @Test
    void testPagosSinGrupo() {
        Pago pago = new Pago();
        pago.setMonto(200.0);

        Pago nuevoPago = pagoService.guardarPago(pago);

        assertNotNull(nuevoPago);
        assertEquals(200.0, nuevoPago.getMonto());

        assertNull(nuevoPago.getGrupo());
    }

    @Test
    void testPagosConMontosMuyGrandes() {
        Pago pago = new Pago();
        pago.setMonto(Double.MAX_VALUE);

        Pago nuevoPago = pagoService.guardarPago(pago);

        assertNotNull(nuevoPago);
        assertEquals(Double.MAX_VALUE, nuevoPago.getMonto());
    }

}
