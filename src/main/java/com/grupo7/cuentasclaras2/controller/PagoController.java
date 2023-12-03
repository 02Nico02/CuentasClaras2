package com.grupo7.cuentasclaras2.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo7.cuentasclaras2.DTO.PagoDTO;
import com.grupo7.cuentasclaras2.exception.InvalidPaymentException;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Pago;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.services.GrupoService;
import com.grupo7.cuentasclaras2.services.PagoService;
import com.grupo7.cuentasclaras2.services.UsuarioService;

@RestController
@RequestMapping("/api/pay")
public class PagoController {

	@Autowired
	private PagoService pagoService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private GrupoService grupoService;

	@GetMapping("/{id}")
	public ResponseEntity<PagoDTO> obtenerPagoPorId(@PathVariable long id) {
		return pagoService.obtenerPagoPorId(id)
				.map(pago -> ResponseEntity.ok(new PagoDTO(pago)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/byGroup/{grupoId}")
	public ResponseEntity<List<PagoDTO>> obtenerPagosPorGrupo(@PathVariable long grupoId) {
		List<PagoDTO> pagos = pagoService.obtenerPagosPorGrupo(grupoId)
				.stream()
				.map(PagoDTO::new)
				.collect(Collectors.toList());
		return ResponseEntity.ok(pagos);
	}

	@GetMapping("/bySender/{usuarioId}")
	public ResponseEntity<List<PagoDTO>> obtenerPagosRealizadosPorUsuario(@PathVariable long usuarioId) {
		List<PagoDTO> pagos = pagoService.obtenerPagosRealizadosPorUsuario(usuarioId)
				.stream()
				.map(PagoDTO::new)
				.collect(Collectors.toList());
		return ResponseEntity.ok(pagos);
	}

	@GetMapping("/byRecipient/{usuarioId}")
	public ResponseEntity<List<PagoDTO>> obtenerPagosRecibidosPorUsuario(@PathVariable long usuarioId) {
		List<PagoDTO> pagos = pagoService.obtenerPagosRecibidosPorUsuario(usuarioId)
				.stream()
				.map(PagoDTO::new)
				.collect(Collectors.toList());
		return ResponseEntity.ok(pagos);
	}

	@PostMapping("/new")
	public ResponseEntity<PagoDTO> crearPago(@RequestBody PagoDTO pagoDTO) {
		Pago pagoGuardado = pagoService.guardarPagoDesdeDTO(pagoDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(new PagoDTO(pagoGuardado));
	}

	@PutMapping("/{id}")
	public ResponseEntity<PagoDTO> actualizarPago(@PathVariable long id, @RequestBody PagoDTO pagoDTO) {
		return pagoService.obtenerPagoPorId(id)
				.map(existingPago -> {
					Pago pagoActualizado = convertirDTOaPago(pagoDTO);
					pagoActualizado.setId(id);
					Pago pagoGuardado = pagoService.guardarPago(pagoActualizado);
					return ResponseEntity.ok(new PagoDTO(pagoGuardado));
				})
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarPago(@PathVariable long id) {
		return pagoService.obtenerPagoPorId(id)
				.map(existingPago -> {
					pagoService.eliminarPago(id);
					return ResponseEntity.ok().<Void>build();
				})
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	private Pago convertirDTOaPago(PagoDTO pagoDTO) {
		Pago pago = new Pago();
		pago.setMonto(pagoDTO.getMonto());
		Usuario autor = usuarioService.getById(pagoDTO.getAutorId())
				.orElseThrow(() -> new InvalidPaymentException("No se encontró el autor del pago"));
		pago.setAutor(autor);
		Usuario destinatario = usuarioService.getById(pagoDTO.getDestinatarioId())
				.orElseThrow(() -> new InvalidPaymentException("No se encontró el destinatario del pago"));
		pago.setDestinatario(destinatario);
		Grupo grupo = grupoService.getGroupById(pagoDTO.getGrupoId())
				.orElseThrow(() -> new InvalidPaymentException("No se encontró el grupo especificado"));
		pago.setGrupo(grupo);
		return pago;
	}
}
