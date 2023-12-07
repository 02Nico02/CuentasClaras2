package com.grupo7.cuentasclaras2.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo7.cuentasclaras2.DTO.DivisionIndividualDTO;
import com.grupo7.cuentasclaras2.DTO.FormaDividirDTO;
import com.grupo7.cuentasclaras2.exception.GastoException;
import com.grupo7.cuentasclaras2.modelos.DivisionIndividual;
import com.grupo7.cuentasclaras2.modelos.FormaDividir;
import com.grupo7.cuentasclaras2.modelos.Gasto;
import com.grupo7.cuentasclaras2.repositories.DivisionIndividualRepository;
import com.grupo7.cuentasclaras2.repositories.FormaDividirRepository;

@Service
public class FormaDividirService {
	@Autowired
	private FormaDividirRepository formaDividirRepository;

	@Autowired
	private DivisionIndividualRepository divisionIndividualRepository;

	@Autowired
	private DivisionIndividualService divisionIndividualService;

	@Transactional
	public FormaDividir createFormaDividirByDTO(FormaDividirDTO formaDividirDTO, Gasto gasto) {
		FormaDividir formaDividir = new FormaDividir();
		formaDividir.setFormaDividir(formaDividirDTO.getFormaDividir());
		formaDividir.setGasto(gasto);

		formaDividir = formaDividirRepository.save(formaDividir);

		if (formaDividirDTO.getDivisionIndividual() != null) {
			for (DivisionIndividualDTO divisionIndividualDTO : formaDividirDTO.getDivisionIndividual()) {
				DivisionIndividual divisionIndividual = divisionIndividualService.createDivisionIndividualByDTO(
						divisionIndividualDTO,
						formaDividir);
				formaDividir.getDivisionIndividual().add(divisionIndividual);
			}
		} else {
			throw new GastoException("Falta las divisiones individuales");
		}

		return formaDividir;
	}

	@Transactional
	public FormaDividir updateFormaDividirByDTO(FormaDividirDTO formaDividirDTO, FormaDividir formaDividir) {
		return saveFormaDividir(formaDividirDTO, formaDividir);
	}

	private FormaDividir saveFormaDividir(FormaDividirDTO formaDividirDTO, FormaDividir formaDividir) {
		formaDividir.setFormaDividir(formaDividirDTO.getFormaDividir());

		if (formaDividirDTO.getDivisionIndividual() != null) {
			Set<DivisionIndividual> newDivisionIndividuals = new HashSet<>();

			for (DivisionIndividualDTO divisionIndividualDTO : formaDividirDTO.getDivisionIndividual()) {
				Optional<DivisionIndividual> existingDivisionIndividualOptional = divisionIndividualRepository
						.findByIdAndFormaDividirIdAndUsuarioId(
								divisionIndividualDTO.getId(),
								formaDividir.getId(),
								divisionIndividualDTO.getUserId());

				if (existingDivisionIndividualOptional.isPresent()) {
					DivisionIndividual existingDivisionIndividual = existingDivisionIndividualOptional.get();
					divisionIndividualService.updateDivisionIndividual(existingDivisionIndividual,
							divisionIndividualDTO);
					newDivisionIndividuals.add(existingDivisionIndividual);
				} else {
					DivisionIndividual divisionIndividual = divisionIndividualService.createDivisionIndividualByDTO(
							divisionIndividualDTO, formaDividir);
					newDivisionIndividuals.add(divisionIndividual);
				}
			}

			formaDividir.getDivisionIndividual().retainAll(newDivisionIndividuals);

			formaDividir.getDivisionIndividual().clear();
			formaDividir.getDivisionIndividual().addAll(newDivisionIndividuals);
		} else {
			throw new GastoException("Falta las divisiones individuales");
		}

		return formaDividirRepository.save(formaDividir);
	}
}
