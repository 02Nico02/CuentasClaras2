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

	@Transactional
	public GastoAutor createGastoAutorByDTO(GastoAutorDTO gastoAutorDTO, Gasto gasto) {
		if (gastoAutorDTO.getMonto() < 0) {
			throw new IllegalArgumentException("El monto debe ser mayor que cero");
		}

		Usuario usuario = usuarioRepository.findById(gastoAutorDTO.getUserId())
				.orElseThrow(() -> new GastoException("Usuario no encontrado con ID: " + gastoAutorDTO.getUserId()));

		GastoAutor gastoAutor = new GastoAutor();
		gastoAutor.setMonto(gastoAutorDTO.getMonto());
		gastoAutor.setIntegrante(usuario);
		gasto.agregarGastoAutor(gastoAutor);

		return gastoAutorRepository.save(gastoAutor);
	}
}
