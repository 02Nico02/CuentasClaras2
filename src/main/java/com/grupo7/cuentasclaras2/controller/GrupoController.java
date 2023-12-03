package com.grupo7.cuentasclaras2.controller;

import java.util.List;
import java.util.Optional;

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

	@GetMapping("/all")
	public ResponseEntity<List<Grupo>> getAllGroups() {
		List<Grupo> groups = grupoService.getAllGroups();
		return ResponseEntity.ok(groups);
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

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
		grupoService.deleteGroup(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/byNombre/{nombre}")
	public ResponseEntity<GrupoDTO> getGroupByNombre(@PathVariable String nombre) {
		return grupoService.getGroupByNombre(nombre)
				.map(group -> new ResponseEntity<>(new GrupoDTO(group), HttpStatus.OK))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping("/addMember/{groupId}/{userId}")
	public ResponseEntity<Void> addMemberToGroup(
			@PathVariable Long groupId,
			@PathVariable Long userId) {
		boolean success = grupoService.addMemberToGroup(groupId, userId);
		return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}

	@DeleteMapping("/removeMember/{groupId}/{memberId}")
	public ResponseEntity<Void> removeMemberFromGroup(
			@PathVariable Long groupId,
			@PathVariable Long memberId) {
		boolean success = grupoService.removeMemberFromGroup(groupId, memberId);
		return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<GrupoDTO> updateGroupName(
			@PathVariable Long id,
			@RequestBody GrupoDTO grupoDTO) {
		return grupoService.getGroupById(id)
				.map(grupo -> {
					grupo.setNombre(grupoDTO.getNombre());
					Grupo updatedGroup = grupoService.saveGroup(grupo);
					return ResponseEntity.ok(new GrupoDTO(updatedGroup));
				})
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

}
