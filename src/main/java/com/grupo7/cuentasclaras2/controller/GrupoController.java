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
		return new ResponseEntity<>(groups, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<GrupoDTO> getGroupById(@PathVariable Long id) {
		return grupoService.getGroupById(id)
				.map(group -> new ResponseEntity<>(new GrupoDTO(group), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/newGroup")
	public ResponseEntity<GrupoDTO> saveGroup(@RequestBody GrupoDTO grupoDTO) {

		Optional<Grupo> grupoOptional = grupoService.newGroupByDTO(grupoDTO);

		if (grupoOptional.isPresent()) {
			Grupo grupoGuardado = grupoOptional.get();
			return new ResponseEntity<>(new GrupoDTO(grupoGuardado), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
		grupoService.deleteGroup(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/byNombre/{nombre}")
	public ResponseEntity<GrupoDTO> getGroupByNombre(@PathVariable String nombre) {
		return grupoService.getGroupByNombre(nombre)
				.map(group -> new ResponseEntity<>(new GrupoDTO(group), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/addMember/{groupId}/{userId}")
	public ResponseEntity<Void> addMemberToGroup(
			@PathVariable Long groupId,
			@PathVariable Long userId) {
		boolean success = grupoService.addMemberToGroup(groupId, userId);
		if (success) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/removeMember/{groupId}/{memberId}")
	public ResponseEntity<Void> removeMemberFromGroup(
			@PathVariable Long groupId,
			@PathVariable Long memberId) {
		boolean success = grupoService.removeMemberFromGroup(groupId, memberId);
		if (success) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<GrupoDTO> updateGroupName(
			@PathVariable Long id,
			@RequestBody GrupoDTO grupoDTO) {
		if (grupoService.getGroupById(id).isPresent()) {
			Grupo grupo = grupoService.getGroupById(id).get();
			grupo.setNombre(grupoDTO.getNombre());
			Grupo updatedGroup = grupoService.saveGroup(grupo);
			return new ResponseEntity<>(new GrupoDTO(updatedGroup), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
