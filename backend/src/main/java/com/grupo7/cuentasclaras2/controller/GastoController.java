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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.grupo7.cuentasclaras2.DTO.GastoDTO;
import com.grupo7.cuentasclaras2.modelos.Gasto;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.services.GastoService;
import com.grupo7.cuentasclaras2.services.GrupoService;
import com.grupo7.cuentasclaras2.services.UsuarioService;

@RestController
@CrossOrigin(origins = { "http://localhost:4200" })
@RequestMapping("/api/spent")
public class GastoController {
	@Autowired
	private GastoService gastoService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private GrupoService grupoService;

	/**
	 * Obtiene los detalles de un gasto específico por su identificador.
	 *
	 * @param id Identificador único del gasto.
	 * @return ResponseEntity con el detalle del gasto en formato GastoDTO, o
	 *         HttpStatus.NOT_FOUND si el gasto no existe o no es accesible.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<GastoDTO> getSpentById(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();

		Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

		if (!usuarioOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Optional<Gasto> gastoOptional = gastoService.getGastoById(id);

		if (!gastoOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Gasto gasto = gastoOptional.get();

		boolean esMiembro = gastoService.esUsuarioMiembroDelGrupo(gasto, usuarioOptional.get());

		if (!esMiembro) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<>(new GastoDTO(gasto), HttpStatus.OK);
	}

	/**
	 * Crea un nuevo gasto utilizando la información proporcionada en el objeto
	 * GastoDTO.
	 *
	 * @param gastoDTO Objeto que contiene los detalles del nuevo gasto.
	 * @return ResponseEntity con el detalle del gasto recién creado en formato
	 *         GastoDTO, o HttpStatus.UNAUTHORIZED si el usuario no está autenticado
	 *         o no es miembro del grupo, o HttpStatus.FORBIDDEN si el usuario no es
	 *         miembro del grupo especificado.
	 */
	@PostMapping("/create")
	public ResponseEntity<GastoDTO> createGasto(@RequestBody GastoDTO gastoDTO) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();

		Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

		if (!usuarioOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		boolean esMiembro = grupoService.usuarioPerteneceAlGrupo(usuarioOptional.get().getId(), gastoDTO.getGrupoId());

		if (!esMiembro) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		Gasto gasto = gastoService.newSpendingByDTO(gastoDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(new GastoDTO(gasto));
	}

	/**
	 * Actualiza los detalles de un gasto existente por su identificador.
	 *
	 * @param id       Identificador único del gasto que se va a actualizar.
	 * @param gastoDTO Objeto que contiene los detalles actualizados del gasto.
	 * @return ResponseEntity con el detalle del gasto actualizado en formato
	 *         GastoDTO, o HttpStatus.UNAUTHORIZED si el usuario no está
	 *         autenticado, HttpStatus.FORBIDDEN si el usuario no es miembro del
	 *         grupo especificado, o HttpStatus.NOT_FOUND si el gasto no existe.
	 */
	@PutMapping("/update/{id}")
	public ResponseEntity<GastoDTO> updateGasto(@PathVariable Long id, @RequestBody GastoDTO gastoDTO) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();

		Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

		if (!usuarioOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		boolean esMiembro = grupoService.usuarioPerteneceAlGrupo(usuarioOptional.get().getId(), gastoDTO.getGrupoId());

		if (!esMiembro) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		if (!gastoService.existsGasto(id)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Gasto gasto = gastoService.updateSpendingByDTO(id, gastoDTO);

		return ResponseEntity.status(HttpStatus.OK).body(new GastoDTO(gasto));
	}

	/**
	 * Obtiene todos los gastos de un grupo y una categoría específicos.
	 *
	 * @param groupId    Identificador único del grupo.
	 * @param categoryId Identificador único de la categoría.
	 * @return ResponseEntity con la lista de gastos en formato GastoDTO, o
	 *         HttpStatus.UNAUTHORIZED si el usuario no está autenticado,
	 *         HttpStatus.FORBIDDEN si el usuario no es miembro del grupo
	 *         especificado, o HttpStatus.NOT_FOUND si no hay gastos disponibles.
	 */
	@GetMapping("/all/by-group-and-category/{groupId}/{categoryId}")
	public ResponseEntity<List<GastoDTO>> getGastosByGroupAndCategory(
			@PathVariable Long groupId,
			@PathVariable Long categoryId) {
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

		List<Gasto> gastos = gastoService.getGastosByGroupAndCategory(groupId, categoryId);
		if (gastos.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		List<GastoDTO> gastoDTOs = gastos.stream().map(GastoDTO::new).collect(Collectors.toList());
		return new ResponseEntity<>(gastoDTOs, HttpStatus.OK);
	}

}
