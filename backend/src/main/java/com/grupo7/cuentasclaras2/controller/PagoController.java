package com.grupo7.cuentasclaras2.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.grupo7.cuentasclaras2.DTO.PagoDTO;
import com.grupo7.cuentasclaras2.modelos.Pago;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.services.PagoService;
import com.grupo7.cuentasclaras2.services.UsuarioService;

@RestController
@CrossOrigin(origins = { "http://localhost:4200" })
@RequestMapping("/api/pay")
public class PagoController {

	@Autowired
	private PagoService pagoService;

	@Autowired
	private UsuarioService usuarioService;

	// No se si se va a usar.
	/**
	 * Obtiene un pago por su ID.
	 *
	 * @param id ID del pago.
	 * @return ResponseEntity con el PagoDTO correspondiente si se encuentra, o
	 *         ResponseEntity.notFound() si no se encuentra.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<PagoDTO> obtenerPagoPorId(@PathVariable long id) {
		return pagoService.obtenerPagoPorId(id)
				.map(pago -> ResponseEntity.ok(new PagoDTO(pago)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	/**
	 * Crea un nuevo pago.
	 *
	 * @param pagoDTO Datos del pago a crear.
	 * @return ResponseEntity con el PagoDTO del pago creado y HttpStatus
	 *         correspondiente.
	 */
	@PostMapping("/new")
	public ResponseEntity<PagoDTO> crearPago(@RequestBody PagoDTO pagoDTO) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();

		Optional<Usuario> user = usuarioService.getByUsername((String) principal);

		if (!user.isPresent())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		Usuario usuario = user.get();
		if (pagoDTO.getAutorId() != usuario.getId())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		Pago pagoGuardado = pagoService.guardarPagoDesdeDTO(pagoDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(new PagoDTO(pagoGuardado));
	}

}
