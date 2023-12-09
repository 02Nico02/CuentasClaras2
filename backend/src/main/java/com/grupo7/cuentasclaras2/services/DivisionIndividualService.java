package com.grupo7.cuentasclaras2.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo7.cuentasclaras2.DTO.DivisionIndividualDTO;
import com.grupo7.cuentasclaras2.exception.BDErrorException;
import com.grupo7.cuentasclaras2.exception.GastoException;
import com.grupo7.cuentasclaras2.exception.UserException;
import com.grupo7.cuentasclaras2.modelos.DivisionIndividual;
import com.grupo7.cuentasclaras2.modelos.FormaDividir;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.DivisionIndividualRepository;
import com.grupo7.cuentasclaras2.repositories.UsuarioRepository;

@Service
public class DivisionIndividualService {

	@Autowired
	private DivisionIndividualRepository divisionIndividualRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Transactional
	public DivisionIndividual createDivisionIndividualByDTO(DivisionIndividualDTO divisionIndividualDTO,
			FormaDividir formaDividir) {
		DivisionIndividual divisionIndividual = new DivisionIndividual();

		if (divisionIndividualDTO.getMonto() < 0) {
			throw new GastoException("El monto debe no ser negativo");
		}

		Optional<Usuario> usuarioOptional = usuarioRepository.findById(divisionIndividualDTO.getUserId());
		Usuario usuario = usuarioOptional.orElseThrow(() -> new UserException("Usuario no encontrado"));
		divisionIndividual.setUsuario(usuario);

		divisionIndividual.setMonto(divisionIndividualDTO.getMonto());
		divisionIndividual.setFormaDividir(formaDividir);

		try {
			return divisionIndividualRepository.save(divisionIndividual);
		} catch (Exception e) {
			throw new BDErrorException("Error al guardar la entidad DivisionIndividual", e);
		}
	}

	@Transactional
	public DivisionIndividual updateDivisionIndividual(DivisionIndividual existingDivisionIndividual,
			DivisionIndividualDTO updatedDivisionIndividualDTO) {
		if (updatedDivisionIndividualDTO.getMonto() < 0) {
			throw new GastoException("El monto debe no ser negativo");
		}

		existingDivisionIndividual.setMonto(updatedDivisionIndividualDTO.getMonto());

		try {
			return divisionIndividualRepository.save(existingDivisionIndividual);
		} catch (Exception e) {
			throw new BDErrorException("Error al actualizar la entidad DivisionIndividual", e);
		}
	}

}
