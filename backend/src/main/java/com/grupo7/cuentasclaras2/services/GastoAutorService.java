package com.grupo7.cuentasclaras2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo7.cuentasclaras2.DTO.GastoAutorDTO;
import com.grupo7.cuentasclaras2.exception.GastoException;
import com.grupo7.cuentasclaras2.modelos.Gasto;
import com.grupo7.cuentasclaras2.modelos.GastoAutor;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.GastoAutorRepository;
import com.grupo7.cuentasclaras2.repositories.UsuarioRepository;

@Service
public class GastoAutorService {
	@Autowired
	private GastoAutorRepository gastoAutorRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	/**
	 * Crea un nuevo autor de gasto a partir de un DTO y lo asocia a un gasto.
	 *
	 * @param gastoAutorDTO El DTO que contiene la información del autor de gasto.
	 * @param gasto         El gasto al cual se asociará el nuevo autor.
	 * @return El autor de gasto creado y guardado en la base de datos.
	 * @throws GastoException Si el monto proporcionado es negativo o si el usuario
	 *                        no se encuentra.
	 */
	@Transactional
	public GastoAutor createGastoAutorByDTO(GastoAutorDTO gastoAutorDTO, Gasto gasto) {
		if (gastoAutorDTO.getMonto() < 0) {
			throw new GastoException("El monto debe no ser negativo");
		}

		Usuario usuario = usuarioRepository.findById(gastoAutorDTO.getUserId())
				.orElseThrow(() -> new GastoException("Usuario no encontrado con ID: " + gastoAutorDTO.getUserId()));

		GastoAutor gastoAutor = new GastoAutor();
		gastoAutor.setMonto(gastoAutorDTO.getMonto());
		gastoAutor.setIntegrante(usuario);
		gasto.agregarGastoAutor(gastoAutor);

		return gastoAutorRepository.save(gastoAutor);
	}

	/**
	 * Actualiza la información de un autor de gasto existente a partir de un DTO.
	 *
	 * @param gastoAutorDTO       El DTO que contiene la nueva información del autor
	 *                            de gasto.
	 * @param gastoAutorExistente El autor de gasto existente que se actualizará.
	 * @return El autor de gasto actualizado y guardado en la base de datos.
	 * @throws GastoException Si el monto proporcionado es negativo o si el usuario
	 *                        no se encuentra.
	 */
	@Transactional
	public GastoAutor updateGastoAutorByDTO(GastoAutorDTO gastoAutorDTO, GastoAutor gastoAutorExistente) {
		if (gastoAutorDTO.getMonto() < 0) {
			throw new GastoException("El monto debe no ser negativo");
		}

		Usuario usuario = usuarioRepository.findById(gastoAutorDTO.getUserId())
				.orElseThrow(() -> new GastoException("Usuario no encontrado con ID: " + gastoAutorDTO.getUserId()));

		// Actualizar los campos del gastoAutorExistente
		gastoAutorExistente.setMonto(gastoAutorDTO.getMonto());
		gastoAutorExistente.setIntegrante(usuario);

		return gastoAutorRepository.save(gastoAutorExistente);
	}

}
