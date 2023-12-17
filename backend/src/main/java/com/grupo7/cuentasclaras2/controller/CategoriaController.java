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

	/**
	 * Obtiene los detalles de una categoría por su identificador.
	 *
	 * @param categoryId Identificador único de la categoría.
	 * @return ResponseEntity con la información de la categoría en formato
	 *         CategoriaDTO si existe, o ResponseEntity.notFound() si la categoría
	 *         no se encuentra.
	 */
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

	/**
	 * Obtiene todas las categorías de grupo.
	 *
	 * @return ResponseEntity con la lista de categorías de grupo en formato
	 *         CategoriaDTO.
	 */
	@GetMapping("/groups")
	public ResponseEntity<List<CategoriaDTO>> getGroupCategories() {
		List<Categoria> groupCategories = categoriaService.getGroupCategories();
		List<CategoriaDTO> groupCategoriesDTO = groupCategories.stream()
				.map(CategoriaDTO::new)
				.collect(Collectors.toList());
		return ResponseEntity.ok(groupCategoriesDTO);
	}

	/**
	 * Obtiene todas las categorías de gastos.
	 *
	 * @return ResponseEntity con la lista de categorías de gastos en formato
	 *         CategoriaDTO.
	 */
	@GetMapping("/expenses")
	public ResponseEntity<List<CategoriaDTO>> getExpenseCategories() {
		List<Categoria> expenseCategories = categoriaService.getExpenseCategories();
		List<CategoriaDTO> expenseCategoriesDTO = expenseCategories.stream()
				.map(CategoriaDTO::new)
				.collect(Collectors.toList());
		return ResponseEntity.ok(expenseCategoriesDTO);
	}
}
