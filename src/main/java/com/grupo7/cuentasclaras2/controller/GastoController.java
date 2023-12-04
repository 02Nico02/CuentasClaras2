package com.grupo7.cuentasclaras2.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo7.cuentasclaras2.DTO.GastoDTO;
import com.grupo7.cuentasclaras2.modelos.Gasto;
import com.grupo7.cuentasclaras2.services.GastoService;

@RestController
@RequestMapping("/api/spent")
public class GastoController {
	@Autowired
	private GastoService gastoService;

	@GetMapping("/{id}")
	public ResponseEntity<GastoDTO> getSpentById(@PathVariable Long id) {
		Optional<Gasto> gasOptional = gastoService.getGastoById(id);
		return gasOptional.map(value -> new ResponseEntity<>(new GastoDTO(value), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/create")
	public ResponseEntity<GastoDTO> createGasto(@RequestBody GastoDTO gastoDTO) {
		Gasto gasto = gastoService.newSpendingByDTO(gastoDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(new GastoDTO(gasto));
	}
}
