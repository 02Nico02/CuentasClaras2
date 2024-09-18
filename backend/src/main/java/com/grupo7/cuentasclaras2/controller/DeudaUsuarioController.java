package com.grupo7.cuentasclaras2.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.grupo7.cuentasclaras2.DTO.DeudaUsuarioDTO;
import com.grupo7.cuentasclaras2.modelos.DeudaUsuario;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.services.DeudaUsuarioService;
import com.grupo7.cuentasclaras2.services.GrupoService;
import com.grupo7.cuentasclaras2.services.UsuarioService;

@RestController
@CrossOrigin(origins = { "http://localhost:4200" })
@RequestMapping("/api/user-debt")
public class DeudaUsuarioController {

	@Autowired
	private DeudaUsuarioService deudaUsuarioService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private GrupoService grupoService;

	/**
	 * Obtiene los detalles de una deuda por su identificador.
	 *
	 * @param debtId Identificador único de la deuda.
	 * @return ResponseEntity con la información de la deuda en formato
	 *         DeudaUsuarioDTO, o HttpStatus.UNAUTHORIZED si el usuario no está
	 *         autenticado, HttpStatus.NOT_FOUND si la deuda no existe, o
	 *         HttpStatus.FORBIDDEN si el usuario no es miembro del grupo al que
	 *         pertenece la deuda.
	 */
	@GetMapping("/{debtId}")
	public ResponseEntity<DeudaUsuarioDTO> getDebtById(@PathVariable long debtId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();

		Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

		if (!usuarioOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Optional<DeudaUsuario> deudaUsuarioOptional = deudaUsuarioService.getById(debtId);

		if (!deudaUsuarioOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		DeudaUsuario deudaUsuario = deudaUsuarioOptional.get();

		Grupo grupo = deudaUsuario.getGrupo();

		boolean esMiembro = grupoService.usuarioPerteneceAlGrupo(usuarioOptional.get().getId(), grupo.getId());

		if (!esMiembro) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<>(new DeudaUsuarioDTO(deudaUsuario), HttpStatus.OK);
	}

	/**
	 * Obtiene todas las deudas de un grupo específico.
	 *
	 * @param groupId Identificador único del grupo.
	 * @return ResponseEntity con la lista de deudas en formato DeudaUsuarioDTO, o
	 *         HttpStatus.UNAUTHORIZED si el usuario no está autenticado,
	 *         HttpStatus.FORBIDDEN si el usuario no es miembro del grupo
	 *         especificado, o HttpStatus.OK con la lista de deudas.
	 */
	@GetMapping("/by-group/{groupId}")
	public ResponseEntity<List<DeudaUsuarioDTO>> getDebtsByGroup(@PathVariable long groupId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();

		Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

		if (!usuarioOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		boolean esMiembro = grupoService.usuarioPerteneceAlGrupo(usuarioOptional.get().getId(), groupId);

		if (!esMiembro) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		List<DeudaUsuarioDTO> debts = deudaUsuarioService.obtenerDeudasPorIdGrupo(groupId)
				.stream()
				.map(DeudaUsuarioDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(debts, HttpStatus.OK);
	}

	/**
	 * Obtiene los detalles de una deuda entre dos usuarios en un grupo específico.
	 *
	 * @param groupId    Identificador único del grupo.
	 * @param deudorId   Identificador único del deudor.
	 * @param acreedorId Identificador único del acreedor.
	 * @return ResponseEntity con la información de la deuda en formato
	 *         DeudaUsuarioDTO, o HttpStatus.UNAUTHORIZED si el usuario no está
	 *         autenticado, HttpStatus.FORBIDDEN si el usuario no es miembro del
	 *         grupo especificado, o HttpStatus.OK si la deuda entre los usuarios
	 *         existe.
	 */
	@GetMapping("/between-users-and-group")
	public ResponseEntity<DeudaUsuarioDTO> getDebtsBetweenUsersInGroup(
			@RequestParam long groupId,
			@RequestParam long deudorId,
			@RequestParam long acreedorId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();

		Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

		if (!usuarioOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		boolean esMiembro = grupoService.usuarioPerteneceAlGrupo(usuarioOptional.get().getId(), groupId);

		if (!esMiembro) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		Optional<DeudaUsuario> deudaUsuarioOptional = deudaUsuarioService.obtenerDeudaEntreUsuariosEnGrupo(groupId,
				deudorId, acreedorId);

		if (deudaUsuarioOptional.isPresent()) {
			return new ResponseEntity<>(new DeudaUsuarioDTO(deudaUsuarioOptional.get()), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
