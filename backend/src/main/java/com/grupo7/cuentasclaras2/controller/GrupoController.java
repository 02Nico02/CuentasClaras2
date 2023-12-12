package com.grupo7.cuentasclaras2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo7.cuentasclaras2.DTO.GastoDTO;
import com.grupo7.cuentasclaras2.DTO.GrupoDTO;
import com.grupo7.cuentasclaras2.DTO.IdEmailUsuarioDTO;
import com.grupo7.cuentasclaras2.DTO.PagoDTO;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.services.GastoService;
import com.grupo7.cuentasclaras2.services.GrupoService;
import com.grupo7.cuentasclaras2.services.PagoService;
import com.grupo7.cuentasclaras2.services.UsuarioService;

@RestController
@RequestMapping("/api/group")
public class GrupoController {
	@Autowired
	private GrupoService grupoService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private PagoService pagoService;

	@Autowired
	private GastoService gastoService;

	// Hay que ver si necesita permisos para ver el grupo, y ver que datos se mandan
	@GetMapping("/{id}")
	public ResponseEntity<GrupoDTO> getGroupById(@PathVariable Long id) {
		return grupoService.getGroupById(id)
				.map(group -> ResponseEntity.ok(new GrupoDTO(group)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping("/newGroup")
	public ResponseEntity<GrupoDTO> saveGroup(@RequestBody GrupoDTO grupoDTO) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();

		Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

		if (!usuarioOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Usuario usuarioAutenticado = usuarioOptional.get();

		List<IdEmailUsuarioDTO> miembrosDTO = new ArrayList<>();
		miembrosDTO.add(new IdEmailUsuarioDTO(usuarioAutenticado));
		grupoDTO.setMiembros(miembrosDTO);

		return grupoService.newGroupByDTO(grupoDTO)
				.map(grupoGuardado -> new ResponseEntity<>(new GrupoDTO(grupoGuardado), HttpStatus.CREATED))
				.orElseGet(() -> ResponseEntity.badRequest().build());
	}

	// Hay que ver cuando se va a crear las parejas. al agregarse un amigo o en
	// algun momento especifico. Dependiendo de eso, este endpoint puede desaparecer
	@PostMapping("/couple")
	public ResponseEntity<GrupoDTO> crearGrupoPareja(@RequestBody GrupoDTO grupoDTO) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication.getPrincipal();

			Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

			if (!usuarioOptional.isPresent()) {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}

			Usuario usuarioAutenticado = usuarioOptional.get();

			List<IdEmailUsuarioDTO> miembrosDTO = grupoDTO.getMiembros();
			if (miembrosDTO == null || miembrosDTO.isEmpty()
					|| !contieneUsuario(miembrosDTO, usuarioAutenticado.getId())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}

			Optional<Grupo> grupoOptional = grupoService.newCoupleGroupByDTO(grupoDTO);
			GrupoDTO nuevoGrupoDTO = new GrupoDTO(grupoOptional.orElse(null));
			return ResponseEntity.status(HttpStatus.CREATED).body(nuevoGrupoDTO);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// Hay que ver en que contexto y bajo que reglas se puede eliminar un grupo y/o
	// pareja
	// @DeleteMapping("/delete/{id}")
	// public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
	// grupoService.deleteGroup(id);
	// return ResponseEntity.noContent().build();
	// }

	// Esta logica se debe trasladar al aceptar una solicitud de union al grupo.
	// @PostMapping("/addMember/byGroup/{groupId}/user/{userId}")
	// public ResponseEntity<Void> addMemberToGroup(
	// @PathVariable Long groupId,
	// @PathVariable Long userId) {
	// boolean success = grupoService.addMemberToGroup(groupId, userId);
	// return success ? ResponseEntity.ok().build() :
	// ResponseEntity.notFound().build();
	// }

	// Hay que ver en que contexto y bajo que reglas se puede eliminar un miembro de
	// un grupo.
	@DeleteMapping("/removeMember/byGroup/{groupId}/user/{memberId}")
	public ResponseEntity<Void> removeMemberFromGroup(
			@PathVariable Long groupId,
			@PathVariable Long memberId) {
		boolean success = grupoService.removeMemberFromGroup(groupId, memberId);
		return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<GrupoDTO> updateGroup(
			@PathVariable Long id,
			@RequestBody GrupoDTO grupoDTO) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();

		Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

		if (!usuarioOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Usuario usuarioAutenticado = usuarioOptional.get();
		boolean esMiembro = grupoService.usuarioPerteneceAlGrupo(usuarioAutenticado.getId(), id);

		if (!esMiembro) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		Optional<Grupo> updatedGroup = grupoService.updateGroup(id, grupoDTO);

		return updatedGroup.map(group -> ResponseEntity.ok(new GrupoDTO(group)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/{grupoId}/payments")
	public ResponseEntity<List<PagoDTO>> getPaymentsByGroup(@PathVariable long grupoId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();

		Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

		if (!usuarioOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Usuario usuario = usuarioOptional.get();
		if (!grupoService.usuarioPerteneceAlGrupo(usuario.getId(), grupoId)) {
			return ResponseEntity.notFound().build();
		}

		List<PagoDTO> payments = pagoService.obtenerPagosPorGrupo(grupoId)
				.stream()
				.map(PagoDTO::new)
				.collect(Collectors.toList());

		return ResponseEntity.ok(payments);
	}

	@GetMapping("/{grupoId}/expenses")
	public ResponseEntity<List<GastoDTO>> getExpensesByGroup(@PathVariable long grupoId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();

		Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

		if (!usuarioOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Usuario usuario = usuarioOptional.get();
		if (!grupoService.usuarioPerteneceAlGrupo(usuario.getId(), grupoId)) {
			return ResponseEntity.notFound().build();
		}

		List<GastoDTO> gastos = gastoService.getGastosByGroup(grupoId)
				.stream()
				.map(GastoDTO::new)
				.collect(Collectors.toList());

		return ResponseEntity.ok(gastos);
	}

	private boolean contieneUsuario(List<IdEmailUsuarioDTO> miembrosDTO, long usuarioId) {
		for (IdEmailUsuarioDTO usuarioDTO : miembrosDTO) {
			if (usuarioDTO.getId() == usuarioId) {
				return true;
			}
		}
		return false;
	}

}
