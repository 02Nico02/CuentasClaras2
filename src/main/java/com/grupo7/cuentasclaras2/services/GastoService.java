package com.grupo7.cuentasclaras2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo7.cuentasclaras2.DTO.CategoriaDTO;
import com.grupo7.cuentasclaras2.DTO.DivisionIndividualDTO;
import com.grupo7.cuentasclaras2.DTO.FormaDividirDTO;
import com.grupo7.cuentasclaras2.DTO.GastoAutorDTO;
import com.grupo7.cuentasclaras2.DTO.GastoDTO;
import com.grupo7.cuentasclaras2.exception.BDErrorException;
import com.grupo7.cuentasclaras2.exception.GastoException;
import com.grupo7.cuentasclaras2.modelos.Categoria;
import com.grupo7.cuentasclaras2.modelos.DeudaUsuario;
import com.grupo7.cuentasclaras2.modelos.DivisionIndividual;
import com.grupo7.cuentasclaras2.modelos.FormaDividir;
import com.grupo7.cuentasclaras2.modelos.FormatosDivision;
import com.grupo7.cuentasclaras2.modelos.Gasto;
import com.grupo7.cuentasclaras2.modelos.GastoAutor;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.CategoriaRepository;
import com.grupo7.cuentasclaras2.repositories.GastoRepository;
import com.grupo7.cuentasclaras2.repositories.GrupoRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GastoService {

	@Autowired
	private GastoRepository gastoRepository;

	@Autowired
	private FormaDividirService formaDividirService;

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private GastoAutorService gastoAutorService;

	@Autowired
	private GrupoRepository grupoRepository;

	@Autowired
	private DeudaUsuarioService deudaUsuarioService;

	public List<Gasto> getAllGastos() {
		return gastoRepository.findAll();
	}

	public Optional<Gasto> getGastoById(Long id) {
		return gastoRepository.findById(id);
	}

	public Gasto saveGasto(Gasto gasto) {
		return gastoRepository.save(gasto);
	}

	public void deleteGasto(Long id) {
		gastoRepository.deleteById(id);
	}

	public boolean existsGasto(Long id) {
		return gastoRepository.existsById(id);
	}

	@Transactional
	public Gasto newSpendingByDTO(GastoDTO gastoDTO) {
		validateNombreAndFecha(gastoDTO.getNombre(), gastoDTO.getFecha());
		validateFormaDividir(gastoDTO.getFormaDividir(), gastoDTO.getGastoAutor());

		CategoriaDTO categoriaDTO = gastoDTO.getCategoria();
		Categoria categoria = validateAndGetCategoria(categoriaDTO.getId());
		validateCategoria(categoria);

		Grupo grupo = validateGroupExistente(gastoDTO.getGrupoId());
		List<Usuario> miembros = grupo.getMiembros();
		validateGroupMembers(gastoDTO.getGastoAutor(), gastoDTO.getFormaDividir().getDivisionIndividual(), miembros);

		Gasto gasto = new Gasto();
		gasto.setNombre(gastoDTO.getNombre());
		gasto.setFecha(gastoDTO.getFecha());
		gasto.setImagen(gastoDTO.getImagen());
		gasto.setEditable(true);
		grupo.agregarGasto(gasto);

		FormaDividir formaDividir = formaDividirService.createFormaDividirByDTO(gastoDTO.getFormaDividir(), gasto);
		gasto.setFormaDividir(formaDividir);

		categoria.agregarGasto(gasto);

		for (GastoAutorDTO gastoAutorDTO : gastoDTO.getGastoAutor()) {
			gastoAutorService.createGastoAutorByDTO(gastoAutorDTO, gasto);
		}

		categoriaRepository.save(categoria);
		Gasto gastoGuardado = gastoRepository.save(gasto);

		crearDeudasUsuarios(gastoDTO.getFormaDividir(), gastoGuardado);

		return gastoGuardado;
	}

	@Transactional
	public Gasto updateSpendingByDTO(Long gastoId, GastoDTO gastoDTO) {

		Gasto gastoExistente = validateAndGetGasto(gastoId);

		if (!gastoExistente.isEditable()) {
			throw new GastoException("El gasto ya no se puede modificar");
		}

		validateNombreAndFecha(gastoDTO.getNombre(), gastoDTO.getFecha());
		validateFormaDividir(gastoDTO.getFormaDividir(), gastoDTO.getGastoAutor());

		CategoriaDTO categoriaDTO = gastoDTO.getCategoria();
		Categoria categoria = validateAndGetCategoria(categoriaDTO.getId());
		validateCategoria(categoria);

		validateGroupMembersUpdate(gastoDTO.getGastoAutor(), gastoDTO.getFormaDividir().getDivisionIndividual(),
				gastoExistente.getFormaDividir().getDivisionIndividual());

		gastoExistente.setNombre(gastoDTO.getNombre());
		gastoExistente.setFecha(gastoDTO.getFecha());
		gastoExistente.setImagen(gastoDTO.getImagen());

		FormaDividir formaDividir = formaDividirService.updateFormaDividirByDTO(gastoDTO.getFormaDividir(),
				gastoExistente.getFormaDividir());
		gastoExistente.setFormaDividir(formaDividir);

		if (gastoExistente.getCategoria().getId() != categoria.getId()) {
			Categoria categoriaExistente = gastoExistente.getCategoria();
			categoriaExistente.removeGasto(gastoExistente);
			categoriaRepository.save(categoriaExistente);

			categoria.agregarGasto(gastoExistente);
		}

		for (GastoAutorDTO gastoAutorDTO : gastoDTO.getGastoAutor()) {
			Optional<GastoAutor> existingGastoAutorOptional = gastoExistente.getGastoAutor().stream()
					.filter(ga -> ga.getIntegrante().getId() == gastoAutorDTO.getUserId())
					.findFirst();

			if (existingGastoAutorOptional.isPresent()) {
				GastoAutor existingGastoAutor = existingGastoAutorOptional.get();
				gastoAutorService.updateGastoAutorByDTO(gastoAutorDTO, existingGastoAutor);
			} else {
				gastoAutorService.createGastoAutorByDTO(gastoAutorDTO, gastoExistente);
			}
		}

		gastoExistente.getGastoAutor().removeIf(existingGastoAutor -> gastoDTO.getGastoAutor().stream()
				.noneMatch(gastoAutorDTO -> existingGastoAutor.getIntegrante().getId() == gastoAutorDTO.getUserId()));

		categoriaRepository.save(categoria);
		Gasto gastoGuardado = gastoRepository.save(gastoExistente);

		actualizarDeudasUsuarios(gastoGuardado.getGrupo());

		return gastoGuardado;
	}

	public List<Gasto> getGastosByGroup(Long groupId) {
		return gastoRepository.findByGrupoId(groupId);
	}

	public List<Gasto> getGastosByGroupAndCategory(Long groupId, Long categoryId) {
		return gastoRepository.findByGrupoIdAndCategoriaId(groupId, categoryId);
	}

	private Gasto validateAndGetGasto(Long gastoId) {
		return gastoRepository.findById(gastoId)
				.orElseThrow(() -> new GastoException("Gasto no encontrado con ID: " + gastoId));
	}

	private void validateNombreAndFecha(String nombre, Date fecha) {
		if (nombre == null || nombre.isEmpty()) {
			throw new GastoException("El nombre del gasto no puede estar vacío");
		}
		if (fecha == null) {
			throw new GastoException("La fecha del gasto no puede ser nula");
		}

		Date hoy = new Date();
		if (fecha.after(hoy)) {
			throw new GastoException("La fecha del gasto debe ser anterior o igual a hoy");
		}
	}

	private Categoria validateAndGetCategoria(Long categoriaId) {
		Categoria categoria = categoriaRepository.findById(categoriaId)
				.orElseThrow(() -> new GastoException("Categoria no encontrada con ID: " + categoriaId));
		return categoria;
	}

	private void validateCategoria(Categoria categoria) {
		if (categoria.isGrupo()) {
			throw new GastoException("La categoria no pertenece a un gasto");
		}
	}

	private void validateFormaDividir(FormaDividirDTO formaDividirDTO, List<GastoAutorDTO> gastoAutorDTOs) {
		if (formaDividirDTO.getFormaDividir().equals(FormatosDivision.PORCENTAJE)) {
			validatePorcentajeDivision(formaDividirDTO.getDivisionIndividual());
		} else if (formaDividirDTO.getFormaDividir().equals(FormatosDivision.MONTO)) {
			validateMontoDivision(formaDividirDTO.getDivisionIndividual(), gastoAutorDTOs);
		} else {
			throw new GastoException("Forma de dividir no válida");
		}
	}

	private void validatePorcentajeDivision(List<DivisionIndividualDTO> divisionIndividualDTOs) {
		double totalPorcentaje = divisionIndividualDTOs.stream().mapToDouble(DivisionIndividualDTO::getMonto).sum();

		if (totalPorcentaje != 100.0) {
			throw new GastoException("La suma de los porcentajes debe ser 100");
		}
	}

	private void validateMontoDivision(List<DivisionIndividualDTO> divisionIndividualDTOs,
			List<GastoAutorDTO> gastoAutorDTOs) {
		double totalDivision = divisionIndividualDTOs.stream().mapToDouble(DivisionIndividualDTO::getMonto).sum();
		double totalGastoAutor = gastoAutorDTOs.stream().mapToDouble(GastoAutorDTO::getMonto).sum();

		if (totalDivision != totalGastoAutor) {
			throw new GastoException("La suma de los montos de DivisionIndividual debe ser igual a la de GastoAutor");
		}
	}

	private Grupo validateGroupExistente(Long grupoId) {
		Grupo grupo = grupoRepository.findById(grupoId)
				.orElseThrow(() -> new GastoException("Grupo no encontrada con ID: " + grupoId));
		return grupo;
	}

	private void validateGroupMembersCommon(List<GastoAutorDTO> gastoAutorDTOs,
			List<DivisionIndividualDTO> divisionIndividualDTOs,
			Set<Long> userIdsInGroup,
			Set<Long> uniqueUserIdsInDivision) {

		Set<Long> uniqueUserIdsInGastoAutor = gastoAutorDTOs.stream().map(GastoAutorDTO::getUserId)
				.collect(Collectors.toSet());
		if (gastoAutorDTOs.size() != uniqueUserIdsInGastoAutor.size()) {
			throw new GastoException("Usuarios repetidos en GastoAutorDTO");
		}

		if (divisionIndividualDTOs.size() != uniqueUserIdsInDivision.size()) {
			throw new GastoException("Usuarios repetidos en DivisionIndividualDTO");
		}

		for (GastoAutorDTO gastoAutorDTO : gastoAutorDTOs) {
			if (!userIdsInGroup.contains(gastoAutorDTO.getUserId())) {
				throw new GastoException("Usuario en GastoAutorDTO no es miembro del grupo");
			}
		}

		if (!userIdsInGroup.equals(uniqueUserIdsInDivision)) {
			throw new GastoException("DivisionIndividualDTO no contiene todos los miembros del grupo");
		}
	}

	private void validateGroupMembers(List<GastoAutorDTO> gastoAutorDTOs,
			List<DivisionIndividualDTO> divisionIndividualDTOs,
			List<Usuario> miembros) {

		Set<Long> userIdsInGroup = miembros.stream().map(Usuario::getId).collect(Collectors.toSet());
		Set<Long> uniqueUserIdsInDivision = divisionIndividualDTOs.stream().map(DivisionIndividualDTO::getUserId)
				.collect(Collectors.toSet());

		validateGroupMembersCommon(gastoAutorDTOs, divisionIndividualDTOs, userIdsInGroup, uniqueUserIdsInDivision);
	}

	private void validateGroupMembersUpdate(List<GastoAutorDTO> gastoAutorDTOs,
			List<DivisionIndividualDTO> divisionIndividualDTOs, List<DivisionIndividual> divisionesIndividuales) {

		Set<Long> userIdsInGastoExistente = divisionesIndividuales.stream()
				.map(division -> division.getUsuario().getId())
				.collect(Collectors.toSet());

		Set<Long> uniqueUserIdsInDivision = divisionIndividualDTOs.stream().map(DivisionIndividualDTO::getUserId)
				.collect(Collectors.toSet());

		validateGroupMembersCommon(gastoAutorDTOs, divisionIndividualDTOs, userIdsInGastoExistente,
				uniqueUserIdsInDivision);
	}

	private void crearDeudasUsuarios(FormaDividirDTO formaDividirDTO, Gasto gasto) {
		List<DivisionIndividualDTO> divisionIndividualDTOs = formaDividirDTO.getDivisionIndividual();
		double totalMontoGastoAutor = gasto.getGastoAutor().stream().mapToDouble(GastoAutor::getMonto).sum();

		// Los usuarios que ya pagaron
		Map<Long, Double> contribuciones = gasto.getGastoAutor()
				.stream()
				.collect(Collectors.toMap(gastoAutor -> gastoAutor.getIntegrante().getId(), GastoAutor::getMonto));

		Map<Long, Double> miembros = gasto.getGrupo().getMiembros()
				.stream()
				.collect(Collectors.toMap(Usuario::getId, usuario -> 0.0));

		// Actualizar los montos de los miembros con lo que ya pagaron
		gasto.getGastoAutor()
				.forEach(gastoAutor -> miembros.put(gastoAutor.getIntegrante().getId(), gastoAutor.getMonto()));

		Map<Long, Double> deudas = new HashMap<>();
		if (FormatosDivision.PORCENTAJE.equals(formaDividirDTO.getFormaDividir())) {
			// Calcular el total de porcentajes
			double totalPorcentajes = divisionIndividualDTOs.stream().mapToDouble(DivisionIndividualDTO::getMonto)
					.sum();

			// Calcular las deudas de cada usuario
			divisionIndividualDTOs.forEach(divisionIndividualDTO -> {
				double porcentaje = divisionIndividualDTO.getMonto();
				double montoDeuda = (porcentaje / totalPorcentajes) * totalMontoGastoAutor;

				// Ajustar el monto de la deuda restando la contribución del usuario que ya pagó
				montoDeuda -= contribuciones.getOrDefault(divisionIndividualDTO.getUserId(), 0.0);
				// Agregar la deuda de este usuario
				deudas.put(divisionIndividualDTO.getUserId(), montoDeuda);
			});
		} else if (FormatosDivision.MONTO.equals(formaDividirDTO.getFormaDividir())) {
			// Calcular las deudas de cada usuario
			divisionIndividualDTOs.forEach(divisionIndividualDTO -> {
				double montoDeuda = divisionIndividualDTO.getMonto();

				// Ajustar el monto de la deuda restando la contribución del usuario que ya pagó
				montoDeuda -= contribuciones.getOrDefault(divisionIndividualDTO.getUserId(), 0.0);
				// Agregar la deuda de este usuario
				deudas.put(divisionIndividualDTO.getUserId(), montoDeuda);
			});
		}

		Long grupoId = gasto.getGrupo().getId();
		for (Long deudorId : deudas.keySet()) {
			if (deudas.get(deudorId) > 0) {
				for (Long acreedorId : miembros.keySet()) {
					if (!deudorId.equals(acreedorId) && deudas.get(acreedorId) < 0) {
						double cantidad = Math.min(deudas.get(deudorId), Math.abs(deudas.get(acreedorId)));
						deudaUsuarioService.crearDeudaUsuario(deudorId, acreedorId, cantidad, grupoId);

						deudas.put(deudorId, deudas.get(deudorId) - cantidad);
						deudas.put(acreedorId, deudas.get(acreedorId) + cantidad);
					}
				}
			}
		}

		deudaUsuarioService.consolidarDeudasEnGrupo(gasto.getGrupo());
	}

	private void actualizarDeudasUsuarios(Grupo grupo) {

		Map<Long, Double> balanceDeudas = grupo.getMiembros()
				.stream()
				.collect(Collectors.toMap(Usuario::getId, usuario -> 0.0));

		for (DeudaUsuario deuda : grupo.getDeudas()) {
			Long deudorId = deuda.getDeudor().getId();
			Long acreedorId = deuda.getAcreedor().getId();

			balanceDeudas.computeIfPresent(deudorId, (key, balance) -> balance - deuda.getMonto());

			balanceDeudas.computeIfPresent(acreedorId, (key, balance) -> balance + deuda.getMonto());
		}

		Map<Long, Double> balancePagosGastos = grupo.getMiembros()
				.stream()
				.collect(Collectors.toMap(Usuario::getId, usuario -> 0.0));

		grupo.getPagos().forEach(pago -> {
			Long autorId = pago.getAutor().getId();
			balancePagosGastos.computeIfPresent(autorId, (key, balance) -> balance + pago.getMonto());
		});

		grupo.getPagos().forEach(pago -> {
			Long destinatarioId = pago.getDestinatario().getId();
			balancePagosGastos.computeIfPresent(destinatarioId, (key, balance) -> balance - pago.getMonto());
		});

		grupo.getGastos().forEach(gastoAux -> actualizarBalancePagosGastos(gastoAux, balancePagosGastos));

		Map<Long, Double> diferenciaBalance = obtenerDiferenciaBalanceDeudasPagos(balanceDeudas, balancePagosGastos);

		for (Map.Entry<Long, Double> entry : diferenciaBalance.entrySet()) {
			Long usuarioId = entry.getKey();
			Double diferencia = entry.getValue();

			while (Math.abs(diferencia) > 0.01 && diferencia < 0) {
				List<Long> acreedores = buscarAcreedoresParaPagar(diferenciaBalance);
				procesarDeudas(grupo, usuarioId, acreedores, diferenciaBalance);
				diferencia = diferenciaBalance.get(usuarioId);
			}
		}

		deudaUsuarioService.consolidarDeudasEnGrupo(grupo);

	}

	private void actualizarBalancePagosGastos(Gasto gastoAux, Map<Long, Double> balancePagosGastos) {
		gastoAux.getGastoAutor().forEach(gastoAutor -> {
			Long autorId = gastoAutor.getIntegrante().getId();
			balancePagosGastos.computeIfPresent(autorId, (key, balance) -> balance + gastoAutor.getMonto());
		});

		gastoAux.getFormaDividir().getDivisionIndividual().forEach(divisionIndividual -> {
			Long deudorId = divisionIndividual.getUsuario().getId();

			if (gastoAux.getFormaDividir().getFormaDividir().equals(FormatosDivision.MONTO)) {
				balancePagosGastos.computeIfPresent(deudorId,
						(key, balance) -> balance - divisionIndividual.getMonto());
			} else if (gastoAux.getFormaDividir().getFormaDividir().equals(FormatosDivision.PORCENTAJE)) {
				double montoPorcentaje = (divisionIndividual.getMonto() / 100) * gastoAux.getTotal();
				balancePagosGastos.computeIfPresent(deudorId, (key, balance) -> balance - montoPorcentaje);
			}
		});
	}

	private Map<Long, Double> obtenerDiferenciaBalanceDeudasPagos(Map<Long, Double> balanceDeudas,
			Map<Long, Double> balancePagosGastos) {
		Map<Long, Double> diferenciaBalance = new HashMap<>();

		for (Long usuarioId : balanceDeudas.keySet()) {
			Double diferencia = balancePagosGastos.getOrDefault(usuarioId, 0.0)
					- balanceDeudas.getOrDefault(usuarioId, 0.0);
			diferenciaBalance.put(usuarioId, diferencia);
		}

		return diferenciaBalance;
	}

	private void procesarDeudas(Grupo grupo, Long deudorId, List<Long> acreedores,
			Map<Long, Double> diferenciaBalance) {

		for (Long acreedorId : acreedores) {
			if (diferenciaBalance.get(deudorId) < 0) {
				Double montoDeuda = Math.min(Math.abs(diferenciaBalance.get(deudorId)),
						diferenciaBalance.get(acreedorId));

				Optional<DeudaUsuario> deudaActONueva = deudaUsuarioService.crearDeudaUsuario(deudorId, acreedorId,
						montoDeuda, grupo.getId());

				if (deudaActONueva.isPresent()) {
					diferenciaBalance.put(deudorId, diferenciaBalance.get(deudorId) + montoDeuda);
					diferenciaBalance.put(acreedorId, diferenciaBalance.get(acreedorId) - montoDeuda);
				} else {
					throw new BDErrorException("Ocurrio un error inesperado");
				}
			} else {
				break;
			}
		}
	}

	private List<Long> buscarAcreedoresParaPagar(Map<Long, Double> diferenciaBalance) {
		return diferenciaBalance.entrySet()
				.stream()
				.filter(entry -> entry.getValue() > 0)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

}
