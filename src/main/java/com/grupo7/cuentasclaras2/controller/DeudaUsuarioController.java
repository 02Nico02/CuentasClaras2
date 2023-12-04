package com.grupo7.cuentasclaras2.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grupo7.cuentasclaras2.DTO.DeudaUsuarioDTO;
import com.grupo7.cuentasclaras2.modelos.DeudaUsuario;
import com.grupo7.cuentasclaras2.services.DeudaUsuarioService;

@RestController
@RequestMapping("/api/user-debt")
public class DeudaUsuarioController {

	@Autowired
	private DeudaUsuarioService deudaUsuarioService;

	@GetMapping("/{debtId}")
	public ResponseEntity<DeudaUsuarioDTO> getDebtById(@PathVariable long debtId) {
		return deudaUsuarioService.getById(debtId)
				.map(deuda -> new ResponseEntity<>(new DeudaUsuarioDTO(deuda), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/by-group/{groupId}")
	public ResponseEntity<List<DeudaUsuarioDTO>> getDebtsByGroup(@PathVariable long groupId) {
		List<DeudaUsuarioDTO> debts = deudaUsuarioService.obtenerDeudasPorIdGrupo(groupId)
				.stream()
				.map(DeudaUsuarioDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(debts, HttpStatus.OK);
	}

	@GetMapping("/by-creditor/{userId}")
	public ResponseEntity<List<DeudaUsuarioDTO>> getDebtsByCreditor(@PathVariable long userId) {
		List<DeudaUsuarioDTO> debts = deudaUsuarioService.obtenerDeudasDeAcreedorID(userId)
				.stream()
				.map(DeudaUsuarioDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(debts, HttpStatus.OK);
	}

	@GetMapping("/by-debtor/{userId}")
	public ResponseEntity<List<DeudaUsuarioDTO>> getDebtsByDebtor(@PathVariable long userId) {
		List<DeudaUsuarioDTO> debts = deudaUsuarioService.obtenerDeudasDeDeudorID(userId)
				.stream()
				.map(DeudaUsuarioDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(debts, HttpStatus.OK);
	}

	@GetMapping("/between-users-and-group")
	public ResponseEntity<DeudaUsuarioDTO> getDebtsBetweenUsersInGroup(
			@RequestParam long groupId,
			@RequestParam long deudorId,
			@RequestParam long acreedorId) {

		Optional<DeudaUsuario> deudaUsuarioOptional = deudaUsuarioService.obtenerDeudaEntreUsuariosEnGrupo(groupId,
				deudorId, acreedorId);

		if (deudaUsuarioOptional.isPresent()) {
			return new ResponseEntity<>(new DeudaUsuarioDTO(deudaUsuarioOptional.get()), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// @PostMapping("/new")
	// public ResponseEntity<DeudaUsuarioDTO> createNewDebt(@RequestBody
	// DeudaUsuarioDTO deudaUsuarioDTO) {
	// Optional<DeudaUsuario> deudaUsuario = deudaUsuarioService.crearDeudaUsuario(
	// deudaUsuarioDTO.getDeudorId(),
	// deudaUsuarioDTO.getAcreedorId(),
	// deudaUsuarioDTO.getMonto(),
	// deudaUsuarioDTO.getGrupoId());

	// if (deudaUsuario.isPresent()) {
	// return new ResponseEntity<>(new DeudaUsuarioDTO(deudaUsuario.get()),
	// HttpStatus.CREATED);
	// } else {
	// return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	// }
	// }

}
