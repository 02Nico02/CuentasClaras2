package com.grupo7.cuentasclaras2.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo7.cuentasclaras2.DTO.PagoDTO;
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
				.map(pago -> new ResponseEntity<>(new PagoDTO(pago), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/byGroup/{grupoId}")
	public ResponseEntity<List<PagoDTO>> obtenerPagosPorGrupo(@PathVariable long grupoId) {
		List<PagoDTO> pagos = pagoService.obtenerPagosPorGrupo(grupoId)
				.stream()
				.map(PagoDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(pagos, HttpStatus.OK);
	}

	@GetMapping("/bySender/{usuarioId}")
	public ResponseEntity<List<PagoDTO>> obtenerPagosRealizadosPorUsuario(@PathVariable long usuarioId) {
		List<PagoDTO> pagos = pagoService.obtenerPagosRealizadosPorUsuario(usuarioId)
				.stream()
				.map(PagoDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(pagos, HttpStatus.OK);
	}

	@GetMapping("/byRecipient/{usuarioId}")
	public ResponseEntity<List<PagoDTO>> obtenerPagosRecibidosPorUsuario(@PathVariable long usuarioId) {
		List<PagoDTO> pagos = pagoService.obtenerPagosRecibidosPorUsuario(usuarioId)
				.stream()
				.map(PagoDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(pagos, HttpStatus.OK);
	}

	@PostMapping("/new")
	public ResponseEntity<PagoDTO> crearPago(@RequestBody PagoDTO pagoDTO) {
		System.out.println("monto: " + pagoDTO.getMonto());
		System.out.println("id autor: " + pagoDTO.getAutorId());
		System.out.println("id destinatario: " + pagoDTO.getDestinatarioId());
		System.out.println("id grupo: " + pagoDTO.getGrupoId());

		System.out.println("antes de NuevoPago");
		Pago nuevoPago = convertirDTOaPago(pagoDTO);
		System.out.println("antes de PagoGuardado");
		Pago pagoGuardado = pagoService.guardarPago(nuevoPago);

		return new ResponseEntity<>(new PagoDTO(pagoGuardado), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PagoDTO> actualizarPago(@PathVariable long id, @RequestBody PagoDTO pagoDTO) {
		if (pagoService.obtenerPagoPorId(id).isPresent()) {
			Pago pagoActualizado = convertirDTOaPago(pagoDTO);
			pagoActualizado.setId(id);

			Pago pagoGuardado = pagoService.guardarPago(pagoActualizado);
			return new ResponseEntity<>(new PagoDTO(pagoGuardado), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarPago(@PathVariable long id) {
		if (pagoService.obtenerPagoPorId(id).isPresent()) {
			pagoService.eliminarPago(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	private Pago convertirDTOaPago(PagoDTO pagoDTO) {
		Pago pago = new Pago();
		pago.setMonto(pagoDTO.getMonto());
		Optional<Usuario> autor = usuarioService.getById(pagoDTO.getAutorId());
		autor.ifPresent(pago::setAutor);
		Optional<Usuario> destinatario = usuarioService.getById(pagoDTO.getDestinatarioId());
		destinatario.ifPresent(pago::setDestinatario);
		Optional<Grupo> grupo = grupoService.getGroupById(pagoDTO.getGrupoId());
		grupo.ifPresent(pago::setGrupo);

		return pago;
	}
}
