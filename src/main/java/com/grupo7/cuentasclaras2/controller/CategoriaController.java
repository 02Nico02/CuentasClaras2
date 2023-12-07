package com.grupo7.cuentasclaras2.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo7.cuentasclaras2.DTO.CategoriaDTO;
import com.grupo7.cuentasclaras2.modelos.Categoria;
import com.grupo7.cuentasclaras2.services.CategoriaService;

@RestController
@RequestMapping("/api/category")
public class CategoriaController {
	@Autowired
	private CategoriaService categoriaService;

	@GetMapping("/{categoryId}")
	public ResponseEntity<CategoriaDTO> getCategoriaById(@PathVariable Long categoryId) {
		Optional<Categoria> categoriaOptional = categoriaService.getCategoriaById(categoryId);

		if (categoriaOptional.isPresent()) {
			Categoria categoria = categoriaOptional.get();
			CategoriaDTO categoriaDTO = new CategoriaDTO(categoria);
			return ResponseEntity.ok(categoriaDTO);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/groups")
	public ResponseEntity<List<CategoriaDTO>> getGroupCategories() {
		List<Categoria> groupCategories = categoriaService.getGroupCategories();
		List<CategoriaDTO> groupCategoriesDTO = groupCategories.stream()
				.map(CategoriaDTO::new)
				.collect(Collectors.toList());
		return ResponseEntity.ok(groupCategoriesDTO);
	}

	@GetMapping("/expenses")
	public ResponseEntity<List<CategoriaDTO>> getExpenseCategories() {
		List<Categoria> expenseCategories = categoriaService.getExpenseCategories();
		List<CategoriaDTO> expenseCategoriesDTO = expenseCategories.stream()
				.map(CategoriaDTO::new)
				.collect(Collectors.toList());
		return ResponseEntity.ok(expenseCategoriesDTO);
	}
}
