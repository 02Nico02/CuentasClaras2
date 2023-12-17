package com.grupo7.cuentasclaras2.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo7.cuentasclaras2.modelos.Categoria;
import com.grupo7.cuentasclaras2.repositories.CategoriaRepository;

@Service
public class CategoriaService {
	@Autowired
	private CategoriaRepository categoriaRepository;

	/**
	 * Obtiene todas las categorías.
	 *
	 * @return Lista de todas las categorías.
	 */
	public List<Categoria> getAllCategorias() {
		return categoriaRepository.findAll();
	}

	/**
	 * Obtiene una categoría por su identificador.
	 *
	 * @param categoriaId El identificador de la categoría.
	 * @return La categoría correspondiente al identificador proporcionado, si
	 *         existe.
	 */
	public Optional<Categoria> getCategoriaById(long categoriaId) {
		return categoriaRepository.findById(categoriaId);
	}

	/**
	 * Guarda una nueva categoría.
	 *
	 * @param categoria La categoría a guardar.
	 * @return La categoría guardada.
	 */
	public Categoria saveCategoria(Categoria categoria) {
		return categoriaRepository.save(categoria);
	}

	/**
	 * Actualiza una categoría existente por su identificador.
	 *
	 * @param categoriaId    El identificador de la categoría existente.
	 * @param nuevaCategoria La nueva información de la categoría.
	 * @return La categoría actualizada, si existe.
	 */
	public Categoria updateCategoria(long categoriaId, Categoria nuevaCategoria) {
		Optional<Categoria> categoriaExistenteOptional = categoriaRepository.findById(categoriaId);

		if (categoriaExistenteOptional.isPresent()) {
			Categoria categoriaExistente = categoriaExistenteOptional.get();
			categoriaExistente.setNombre(nuevaCategoria.getNombre());
			categoriaExistente.setIcono(nuevaCategoria.getIcono());
			categoriaExistente.setEsGrupo(nuevaCategoria.isGrupo());

			return categoriaRepository.save(categoriaExistente);
		} else {
			return null;
		}
	}

	/**
	 * Elimina una categoría por su identificador.
	 *
	 * @param categoriaId El identificador de la categoría a eliminar.
	 */
	public void deleteCategoria(long categoriaId) {
		categoriaRepository.deleteById(categoriaId);
	}

	/**
	 * Obtiene todas las categorías que son de grupos.
	 *
	 * @return Lista de categorías que son de grupos.
	 */
	public List<Categoria> getGroupCategories() {
		return categoriaRepository.findByGrupoTrue();
	}

	/**
	 * Obtiene todas las categorías que son de gastos.
	 *
	 * @return Lista de categorías que son gastos.
	 */
	public List<Categoria> getExpenseCategories() {
		return categoriaRepository.findByGrupoFalse();
	}

}