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

	public List<Categoria> getAllCategorias() {
		return categoriaRepository.findAll();
	}

	public Optional<Categoria> getCategoriaById(long categoriaId) {
		return categoriaRepository.findById(categoriaId);
	}

	public Categoria saveCategoria(Categoria categoria) {
		return categoriaRepository.save(categoria);
	}

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

	public void deleteCategoria(long categoriaId) {
		categoriaRepository.deleteById(categoriaId);
	}
}
