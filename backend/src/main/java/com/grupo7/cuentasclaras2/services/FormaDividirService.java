package com.grupo7.cuentasclaras2.services;

import java.util.HashSet;
import java.util.List;
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

	/**
	 * Crea una nueva FormaDividir a partir de un DTO y la asocia a un Gasto.
	 *
	 * @param formaDividirDTO El DTO que contiene la información de la FormaDividir.
	 * @param gasto           El Gasto al cual se asociará la FormaDividir.
	 * @return La FormaDividir creada y guardada en la base de datos.
	 */
	@Transactional
	public FormaDividir createFormaDividirByDTO(FormaDividirDTO formaDividirDTO, Gasto gasto) {
		FormaDividir formaDividir = new FormaDividir();
		formaDividir.setFormaDividir(formaDividirDTO.getFormaDividir());
		formaDividir.setGasto(gasto);

		formaDividir = formaDividirRepository.save(formaDividir);
		saveDivisionIndividuals(formaDividirDTO.getDivisionIndividual(), formaDividir);

		return formaDividirRepository.save(formaDividir);
	}

	/**
	 * Actualiza una FormaDividir existente a partir de un DTO.
	 *
	 * @param formaDividirDTO El DTO que contiene la información actualizada de la
	 *                        FormaDividir.
	 * @param formaDividir    La FormaDividir existente que se actualizará.
	 * @return La FormaDividir actualizada y guardada en la base de datos.
	 * @throws GastoException Si la FormaDividir no existe.
	 */
	@Transactional
	public FormaDividir updateFormaDividirByDTO(FormaDividirDTO formaDividirDTO, FormaDividir formaDividir) {
		if (formaDividir == null) {
			throw new GastoException("La forma de dividir no existe");
		}

		formaDividir.setFormaDividir(formaDividirDTO.getFormaDividir());

		saveDivisionIndividuals(formaDividirDTO.getDivisionIndividual(), formaDividir);

		return formaDividirRepository.save(formaDividir);
	}

	/**
	 * Guarda las DivisionIndividual a partir de los DTOs proporcionados y las
	 * asocia a una FormaDividir.
	 *
	 * @param divisionIndividualDTOs La lista de DivisionIndividualDTO que contiene
	 *                               la información de las DivisionIndividual.
	 * @param formaDividir           La FormaDividir a la cual se asociarán las
	 *                               DivisionIndividual.
	 * @throws GastoException Si la lista de DivisionIndividualDTOs es nula.
	 */
	private void saveDivisionIndividuals(List<DivisionIndividualDTO> divisionIndividualDTOs,
			FormaDividir formaDividir) {
		if (divisionIndividualDTOs != null) {
			Set<DivisionIndividual> newDivisionIndividuals = new HashSet<>();

			for (DivisionIndividualDTO divisionIndividualDTO : divisionIndividualDTOs) {
				DivisionIndividual existingDivisionIndividual = getExistingDivisionIndividual(divisionIndividualDTO,
						formaDividir);

				if (existingDivisionIndividual != null) {
					divisionIndividualService.updateDivisionIndividual(existingDivisionIndividual,
							divisionIndividualDTO);
					newDivisionIndividuals.add(existingDivisionIndividual);
				} else {
					DivisionIndividual divisionIndividual = divisionIndividualService
							.createDivisionIndividualByDTO(divisionIndividualDTO, formaDividir);
					newDivisionIndividuals.add(divisionIndividual);
				}
			}

			formaDividir.getDivisionIndividual().retainAll(newDivisionIndividuals);
			formaDividir.getDivisionIndividual().clear();
			formaDividir.getDivisionIndividual().addAll(newDivisionIndividuals);
		} else {
			throw new GastoException("Falta las divisiones individuales");
		}
	}

	/**
	 * Obtiene una DivisionIndividual existente a partir de un DTO y una
	 * FormaDividir.
	 *
	 * @param divisionIndividualDTO El DTO que contiene la información de la
	 *                              DivisionIndividual.
	 * @param formaDividir          La FormaDividir a la cual está asociada la
	 *                              DivisionIndividual.
	 * @return La DivisionIndividual existente o null si no existe.
	 */
	private DivisionIndividual getExistingDivisionIndividual(DivisionIndividualDTO divisionIndividualDTO,
			FormaDividir formaDividir) {
		return divisionIndividualRepository.findByIdAndFormaDividirIdAndUsuarioId(
				divisionIndividualDTO.getId(),
				formaDividir.getId(),
				divisionIndividualDTO.getUserId())
				.orElse(null);
	}
}