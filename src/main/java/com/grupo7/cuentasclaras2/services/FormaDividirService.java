package com.grupo7.cuentasclaras2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo7.cuentasclaras2.DTO.DivisionIndividualDTO;
import com.grupo7.cuentasclaras2.DTO.FormaDividirDTO;
import com.grupo7.cuentasclaras2.exception.GastoException;
import com.grupo7.cuentasclaras2.modelos.DivisionIndividual;
import com.grupo7.cuentasclaras2.modelos.FormaDividir;
import com.grupo7.cuentasclaras2.modelos.Gasto;
import com.grupo7.cuentasclaras2.repositories.FormaDividirRepository;

@Service
public class FormaDividirService {
	@Autowired
	private FormaDividirRepository formaDividirRepository;

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
}
