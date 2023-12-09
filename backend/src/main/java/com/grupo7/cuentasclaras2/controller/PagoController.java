package com.grupo7.cuentasclaras2.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo7.cuentasclaras2.DTO.PagoDTO;
import com.grupo7.cuentasclaras2.services.PagoService;

@RestController
@RequestMapping("/api/pay")
public class PagoController {

	@Autowired
	private PagoService pagoService;

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

}
