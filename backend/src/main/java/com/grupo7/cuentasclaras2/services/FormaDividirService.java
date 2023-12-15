package com.grupo7.cuentasclaras2.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	/**
	 * Crea una nueva instancia de {@link FormaDividir} a partir de un DTO y la
	 * asocia con un {@link Gasto}.
	 *
	 * @param formaDividirDTO El DTO que contiene la información para crear la
	 *                        FormaDividir.
	 * @param gasto           El Gasto al que se asociará la nueva FormaDividir.
	 * @return La FormaDividir creada y guardada.
	 * @throws GastoException Si el DTO no contiene divisiones individuales.
	 */
	@Transactional
	public FormaDividir createFormaDividirByDTO(FormaDividirDTO formaDividirDTO, Gasto gasto) {
		FormaDividir formaDividir = new FormaDividir();
		formaDividir.setFormaDividir(formaDividirDTO.getFormaDividir());
		formaDividir.setGasto(gasto);

		formaDividir = formaDividirRepository.save(formaDividir);

		List<DivisionIndividual> newDivisionIndividuals = createDivisionIndividuals(formaDividirDTO, formaDividir);
		formaDividir.setDivisionIndividual(newDivisionIndividuals);

		return formaDividir;
	}

	/**
	 * Actualiza una instancia de {@link FormaDividir} existente con la información
	 * proporcionada en un DTO.
	 *
	 * @param formaDividirDTO El DTO que contiene la información para actualizar la
	 *                        FormaDividir.
	 * @param formaDividir    La FormaDividir existente que se actualizará.
	 * @return La FormaDividir actualizada y guardada.
	 * @throws GastoException Si el DTO no contiene divisiones individuales.
	 * @see #saveFormaDividir(FormaDividirDTO, FormaDividir)
	 */
	@Transactional
	public FormaDividir updateFormaDividirByDTO(FormaDividirDTO formaDividirDTO, FormaDividir formaDividir) {
		return saveFormaDividir(formaDividirDTO, formaDividir);
	}

	/**
	 * Guarda una instancia de {@link FormaDividir} con la información proporcionada
	 * en un DTO.
	 *
	 * @param formaDividirDTO El DTO que contiene la información para guardar la
	 *                        FormaDividir.
	 * @param formaDividir    La FormaDividir existente a la que se aplicarán las
	 *                        actualizaciones.
	 * @return La FormaDividir guardada.
	 * @throws GastoException Si el DTO no contiene divisiones individuales.
	 */
	private FormaDividir saveFormaDividir(FormaDividirDTO formaDividirDTO, FormaDividir formaDividir) {
		formaDividir.setFormaDividir(formaDividirDTO.getFormaDividir());

		List<DivisionIndividual> newDivisionIndividuals = createDivisionIndividuals(formaDividirDTO, formaDividir);
		formaDividir.getDivisionIndividual().retainAll(newDivisionIndividuals);

		formaDividir.setDivisionIndividual(newDivisionIndividuals);

		return formaDividirRepository.save(formaDividir);
	}

	/**
	 * Crea y retorna una lista de divisiones individuales a partir de un DTO y una
	 * FormaDividir.
	 *
	 * @param formaDividirDTO El DTO que contiene la información para crear las
	 *                        divisiones individuales.
	 * @param formaDividir    La FormaDividir a la que se asociarán las divisiones
	 *                        individuales.
	 * @return La lista de divisiones individuales creadas.
	 * @throws GastoException Si el DTO no contiene divisiones individuales.
	 */
	private List<DivisionIndividual> createDivisionIndividuals(FormaDividirDTO formaDividirDTO,
			FormaDividir formaDividir) {
		List<DivisionIndividual> newDivisionIndividuals = new ArrayList<>();

		if (formaDividirDTO.getDivisionIndividual() != null) {
			for (DivisionIndividualDTO divisionIndividualDTO : formaDividirDTO.getDivisionIndividual()) {
				DivisionIndividual divisionIndividual = createOrUpdateDivisionIndividual(
						divisionIndividualDTO,
						formaDividir);
				newDivisionIndividuals.add(divisionIndividual);
			}
		} else {
			throw new GastoException("Faltan las divisiones individuales");
		}

		return newDivisionIndividuals;
	}

	/**
	 * Crea o actualiza una instancia de {@link DivisionIndividual} a partir de un
	 * DTO y una FormaDividir.
	 *
	 * @param divisionIndividualDTO El DTO que contiene la información para crear o
	 *                              actualizar la división individual.
	 * @param formaDividir          La FormaDividir a la que se asociará la división
	 *                              individual.
	 * @return La división individual creada o actualizada.
	 */
	private DivisionIndividual createOrUpdateDivisionIndividual(DivisionIndividualDTO divisionIndividualDTO,
			FormaDividir formaDividir) {
		Optional<DivisionIndividual> existingDivisionIndividualOptional = divisionIndividualRepository
				.findByIdAndFormaDividirIdAndUsuarioId(divisionIndividualDTO.getId(), formaDividir.getId(),
						divisionIndividualDTO.getUserId());

		if (existingDivisionIndividualOptional.isPresent()) {
			DivisionIndividual existingDivisionIndividual = existingDivisionIndividualOptional.get();
			divisionIndividualService.updateDivisionIndividual(existingDivisionIndividual, divisionIndividualDTO);
			return existingDivisionIndividual;
		} else {
			return divisionIndividualService.createDivisionIndividualByDTO(divisionIndividualDTO, formaDividir);
		}
	}
}
