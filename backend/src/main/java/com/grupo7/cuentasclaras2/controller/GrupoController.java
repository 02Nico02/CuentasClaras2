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

import com.grupo7.cuentasclaras2.DTO.GrupoDTO;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.services.GrupoService;

@RestController
@RequestMapping("/api/group")
public class GrupoController {
	@Autowired
	private GrupoService grupoService;

	@GetMapping("/by-user/{userId}")
	public ResponseEntity<List<GrupoDTO>> getGroupsByUserId(@PathVariable Long userId) {
		List<Grupo> groups = grupoService.getGroupsByUserId(userId);
		List<GrupoDTO> groupDTOs = groups.stream()
				.map(GrupoDTO::new)
				.collect(Collectors.toList());
		return ResponseEntity.ok(groupDTOs);
	}

	@GetMapping("/pareja/by-user/{userId}")
	public ResponseEntity<List<GrupoDTO>> getGroupsWhereEsPareja(@PathVariable Long userId) {
		List<Grupo> groups = grupoService.getGroupsWhereEsPareja(userId);
		List<GrupoDTO> groupDTOs = groups.stream()
				.map(GrupoDTO::new)
				.collect(Collectors.toList());
		return ResponseEntity.ok(groupDTOs);
	}

	@GetMapping("/{id}")
	public ResponseEntity<GrupoDTO> getGroupById(@PathVariable Long id) {
		return grupoService.getGroupById(id)
				.map(group -> ResponseEntity.ok(new GrupoDTO(group)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping("/newGroup")
	public ResponseEntity<GrupoDTO> saveGroup(@RequestBody GrupoDTO grupoDTO) {
		return grupoService.newGroupByDTO(grupoDTO)
				.map(grupoGuardado -> new ResponseEntity<>(new GrupoDTO(grupoGuardado), HttpStatus.CREATED))
				.orElseGet(() -> ResponseEntity.badRequest().build());
	}

	@PostMapping("/couple")
	public ResponseEntity<GrupoDTO> crearGrupoPareja(@RequestBody GrupoDTO grupoDTO) {
		try {
			Optional<Grupo> grupoOptional = grupoService.newCoupleGroupByDTO(grupoDTO);
			GrupoDTO nuevoGrupoDTO = new GrupoDTO(grupoOptional.orElse(null));
			return ResponseEntity.status(HttpStatus.CREATED).body(nuevoGrupoDTO);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// @DeleteMapping("/delete/{id}")
	// public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
	// grupoService.deleteGroup(id);
	// return ResponseEntity.noContent().build();
	// }

	@PostMapping("/addMember/byGroup/{groupId}/user/{userId}")
	public ResponseEntity<Void> addMemberToGroup(
			@PathVariable Long groupId,
			@PathVariable Long userId) {
		boolean success = grupoService.addMemberToGroup(groupId, userId);
		return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}

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
		Optional<Grupo> updatedGroup = grupoService.updateGroup(id, grupoDTO);

		return updatedGroup.map(group -> ResponseEntity.ok(new GrupoDTO(group)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

}
