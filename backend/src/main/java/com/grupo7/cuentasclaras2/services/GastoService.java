package com.grupo7.cuentasclaras2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.grupo7.cuentasclaras2.DTO.CategoriaDTO;
import com.grupo7.cuentasclaras2.DTO.CrearGastoDTO;
import com.grupo7.cuentasclaras2.DTO.DivisionIndividualDTO;
import com.grupo7.cuentasclaras2.DTO.FormaDividirDTO;
import com.grupo7.cuentasclaras2.DTO.GastoAutorDTO;
import com.grupo7.cuentasclaras2.DTO.GastoDTO;
import com.grupo7.cuentasclaras2.exception.GastoException;
import com.grupo7.cuentasclaras2.exception.GroupException;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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

	/**
	 * Obtiene todos los gastos almacenados en el sistema.
	 *
	 * @return Una lista que contiene todos los gastos almacenados.
	 */
	public List<Gasto> getAllGastos() {
		return gastoRepository.findAll();
	}

	/**
	 * Obtiene un gasto específico por su identificador.
	 *
	 * @param id El identificador del gasto que se desea obtener.
	 * @return Un objeto Optional que contiene el gasto si se encuentra, o un
	 *         Optional vacío si no existe.
	 */
	public Optional<Gasto> getGastoById(Long id) {
		return gastoRepository.findById(id);
	}

	/**
	 * Guarda un nuevo gasto en el sistema o actualiza uno existente.
	 *
	 * @param gasto El gasto que se desea guardar o actualizar.
	 * @return El gasto guardado o actualizado.
	 */
	public Gasto saveGasto(Gasto gasto) {
		return gastoRepository.save(gasto);
	}

	/**
	 * Elimina un gasto del sistema por su identificador.
	 *
	 * @param id El identificador del gasto que se desea eliminar.
	 */
	public void deleteGasto(Long id) {
		gastoRepository.deleteById(id);
	}

	/**
	 * Verifica si existe un gasto con el identificador especificado.
	 *
	 * @param id El identificador del gasto que se desea verificar.
	 * @return true si existe un gasto con el identificador especificado, false en
	 *         caso contrario.
	 */
	public boolean existsGasto(Long id) {
		return gastoRepository.existsById(id);
	}

	/**
	 * Crea un nuevo gasto en el sistema utilizando la información proporcionada en
	 * un objeto CrearGastoDTO.
	 *
	 * @param crearGastoDTO El objeto CrearGastoDTO que contiene la información para
	 *                      crear el
	 *                      nuevo gasto.
	 * @return El gasto recién creado y guardado en el sistema.
	 * @throws GroupException Si la validación de los datos del gasto no es exitosa.
	 */
	@Transactional
	public Gasto newSpendingByDTO(CrearGastoDTO crearGastoDTO) {
		validateNombreAndFecha(crearGastoDTO.getNombre(), crearGastoDTO.getFecha());
		validateFormaDividir(crearGastoDTO.getFormaDividir(), crearGastoDTO.getGastoAutor());

		Categoria categoria = validateAndGetCategoria(crearGastoDTO.getCategoriaId());
		validateCategoria(categoria);

		Grupo grupo = validateGroupExistente(crearGastoDTO.getGrupoId());
		List<Usuario> miembros = grupo.getMiembros();
		validateGroupMembers(crearGastoDTO.getGastoAutor(), crearGastoDTO.getFormaDividir().getDivisionIndividual(),
				miembros);

		Gasto gasto = new Gasto();
		gasto.setNombre(crearGastoDTO.getNombre());
		gasto.setFecha(crearGastoDTO.getFecha());
		gasto.setEditable(true);
		grupo.agregarGasto(gasto);

		FormaDividir formaDividir = formaDividirService.createFormaDividirByDTO(crearGastoDTO.getFormaDividir(), gasto);
		gasto.setFormaDividir(formaDividir);

		categoria.agregarGasto(gasto);

		for (GastoAutorDTO gastoAutorDTO : crearGastoDTO.getGastoAutor()) {
			gastoAutorService.createGastoAutorByDTO(gastoAutorDTO, gasto);
		}

		categoriaRepository.save(categoria);
		Gasto gastoGuardado = gastoRepository.save(gasto);

		crearDeudasUsuarios(crearGastoDTO.getFormaDividir(), gastoGuardado);

		return gastoGuardado;
	}

	public Gasto saveImagenComprobante(Gasto gasto, MultipartFile file) {
		validateImagen(file);
		File imageSave = saveFileToDisk(file);
		gasto.setImagen(imageSave.getName());
		gastoRepository.save(gasto);
		return gasto;
	}

	/**
	 * Guarda el archivo proporcionado en el sistema de archivos en una ubicación
	 * específica.
	 * El archivo se guarda con un nombre único generado utilizando UUID para evitar
	 * colisiones.
	 *
	 * @param file MultipartFile que representa el archivo a guardar.
	 * @return File que representa el archivo guardado en el sistema de archivos.
	 * @throws GastoException Si ocurre un error durante la transferencia o guardado
	 *                        del archivo.
	 */
	private File saveFileToDisk(MultipartFile file) {
		String directoryPath = "src/main/resources/static/images/gasto/";
		String originalFilename = file.getOriginalFilename();
		String fileExtension = "";
		if (originalFilename != null && originalFilename.contains(".")) {
			fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
		} else {
			throw new GastoException("No se pudo determinar la extensión del archivo o el nombre del archivo es nulo.");
		}

		String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;

		Path directory = Paths.get(directoryPath);
		if (!Files.exists(directory)) {
			try {
				Files.createDirectories(directory);
			} catch (IOException e) {
				throw new GastoException("Error al crear el directorio: " + e.getMessage());
			}
		}

		File storedFile = new File(directoryPath + uniqueFileName);
		try (OutputStream os = new FileOutputStream(storedFile)) {
			os.write(file.getBytes());
		} catch (IOException e) {
			throw new GastoException("Error al guardar el archivo: " + e.getMessage());
		}
		return storedFile;
	}

	/**
	 * Actualiza un gasto existente en el sistema utilizando la información
	 * proporcionada en un objeto GastoDTO.
	 *
	 * @param gastoId  El identificador del gasto que se desea actualizar.
	 * @param gastoDTO El objeto GastoDTO que contiene la información para
	 *                 actualizar el gasto.
	 * @return El gasto actualizado y guardado en el sistema.
	 * @throws GastoException Si la validación de los datos del gasto no es exitosa
	 *                        o si el gasto no es editable.
	 */
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

	/**
	 * Obtiene todos los gastos asociados a un grupo específico.
	 *
	 * @param groupId El identificador del grupo del que se desean obtener los
	 *                gastos.
	 * @return Una lista que contiene todos los gastos asociados al grupo
	 *         especificado.
	 */
	public List<Gasto> getGastosByGroup(Long groupId) {
		return gastoRepository.findByGrupoId(groupId);
	}

	/**
	 * Obtiene todos los gastos asociados a un grupo y una categoría específicos.
	 *
	 * @param groupId    El identificador del grupo del que se desean obtener los
	 *                   gastos.
	 * @param categoryId El identificador de la categoría de la que se desean
	 *                   obtener los gastos.
	 * @return Una lista que contiene todos los gastos asociados al grupo y la
	 *         categoría especificados.
	 */
	public List<Gasto> getGastosByGroupAndCategory(Long groupId, Long categoryId) {
		return gastoRepository.findByGrupoIdAndCategoriaId(groupId, categoryId);
	}

	/**
	 * Valida la existencia de un gasto por su identificador y lo retorna.
	 *
	 * @param gastoId El identificador del gasto que se desea validar y obtener.
	 * @return El gasto validado.
	 * @throws GastoException Si no se encuentra un gasto con el identificador
	 *                        especificado.
	 */
	private Gasto validateAndGetGasto(Long gastoId) {
		return gastoRepository.findById(gastoId)
				.orElseThrow(() -> new GastoException("Gasto no encontrado con ID: " + gastoId));
	}

	/**
	 * Valida el nombre y la fecha de un gasto.
	 *
	 * @param nombre El nombre del gasto.
	 * @param fecha  La fecha del gasto.
	 * @throws GastoException Si el nombre está vacío, la fecha es nula o la fecha
	 *                        es posterior a la fecha actual.
	 */
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

	/**
	 * Valida la existencia de una categoría por su identificador y la retorna.
	 *
	 * @param categoriaId El identificador de la categoría que se desea validar y
	 *                    obtener.
	 * @return La categoría validada.
	 * @throws GastoException Si no se encuentra una categoría con el identificador
	 *                        especificado.
	 */
	private Categoria validateAndGetCategoria(Long categoriaId) {
		Categoria categoria = categoriaRepository.findById(categoriaId)
				.orElseThrow(() -> new GastoException("Categoria no encontrada con ID: " + categoriaId));
		return categoria;
	}

	/**
	 * Valida la categoría para asegurar que no pertenezca a un grupo.
	 *
	 * @param categoria La categoría que se desea validar.
	 * @throws GastoException Si la categoría pertenece a un grupo.
	 */
	private void validateCategoria(Categoria categoria) {
		if (categoria.isGrupo()) {
			throw new GastoException("La categoria no pertenece a un gasto");
		}
	}

	/**
	 * Valida la imagen proporcionada para asegurarse de que cumpla con los
	 * requisitos de tamaño y tipo especificados.
	 *
	 * @param imagen MultipartFile que representa la imagen a validar.
	 * @throws GastoException Si la imagen supera el tamaño máximo permitido o si su
	 *                        tipo no está entre los tipos permitidos.
	 */
	private void validateImagen(MultipartFile imagen) {
		if (imagen == null || imagen.isEmpty()) {
			return;
		}

		long maxSize = 5 * 1024 * 1024; // 5MB
		String[] allowedTypes = { "image/jpeg", "image/png", "image/jpg", "application/pdf" };

		try {
			if (imagen.getSize() > maxSize) {
				throw new GastoException("El tamaño de la imagen supera el límite permitido de 5MB.");
			}

			String contentType = imagen.getContentType();
			if (contentType == null || !Arrays.asList(allowedTypes).contains(contentType)) {
				throw new GastoException(
						"El tipo de archivo no es válido. Solo se permiten imágenes (JPG, JPEG, PNG) y PDF.");
			}
		} catch (Exception e) {
			throw new GastoException("Error al validar la imagen: " + e.getMessage());
		}
	}

	/**
	 * Valida la forma de dividir y la lista de porcentajes o montos asociados.
	 *
	 * @param formaDividirDTO El objeto FormaDividirDTO que contiene la información
	 *                        sobre la forma de dividir.
	 * @param gastoAutorDTOs  La lista de GastoAutorDTO que contiene información
	 *                        sobre los autores del gasto.
	 * @throws GastoException Si la forma de dividir no es válida o si hay errores
	 *                        en los porcentajes o montos.
	 */
	private void validateFormaDividir(FormaDividirDTO formaDividirDTO, List<GastoAutorDTO> gastoAutorDTOs) {
		if (formaDividirDTO.getFormaDividir().equals(FormatosDivision.PORCENTAJE)) {
			validatePorcentajeDivision(formaDividirDTO.getDivisionIndividual());
		} else if (formaDividirDTO.getFormaDividir().equals(FormatosDivision.MONTO)) {
			validateMontoDivision(formaDividirDTO.getDivisionIndividual(), gastoAutorDTOs);
		} else {
			throw new GastoException("Forma de dividir no válida");
		}
	}

	/**
	 * Valida la suma de los porcentajes para asegurar que sea igual a 100.
	 *
	 * @param divisionIndividualDTOs La lista de DivisionIndividualDTO que contiene
	 *                               los porcentajes.
	 * @throws GastoException Si la suma de los porcentajes no es igual a 100.
	 */
	private void validatePorcentajeDivision(List<DivisionIndividualDTO> divisionIndividualDTOs) {
		double totalPorcentaje = divisionIndividualDTOs.stream().mapToDouble(DivisionIndividualDTO::getMonto).sum();

		if (totalPorcentaje != 100.0) {
			throw new GastoException("La suma de los porcentajes debe ser 100");
		}
	}

	/**
	 * Valida la suma de los montos de DivisionIndividual para asegurar que sea
	 * igual a la de GastoAutor.
	 *
	 * @param divisionIndividualDTOs La lista de DivisionIndividualDTO que contiene
	 *                               los montos.
	 * @param gastoAutorDTOs         La lista de GastoAutorDTO que contiene los
	 *                               montos de los autores del gasto.
	 * @throws GastoException Si la suma de los montos de DivisionIndividual no es
	 *                        igual a la de GastoAutor.
	 */
	private void validateMontoDivision(List<DivisionIndividualDTO> divisionIndividualDTOs,
			List<GastoAutorDTO> gastoAutorDTOs) {
		double totalDivision = divisionIndividualDTOs.stream().mapToDouble(DivisionIndividualDTO::getMonto).sum();
		double totalGastoAutor = gastoAutorDTOs.stream().mapToDouble(GastoAutorDTO::getMonto).sum();

		if (totalDivision != totalGastoAutor) {
			throw new GastoException("La suma de los montos de DivisionIndividual debe ser igual a la de GastoAutor");
		}
	}

	/**
	 * Valida la existencia de un grupo por su identificador y lo retorna.
	 *
	 * @param grupoId El identificador del grupo que se desea validar y obtener.
	 * @return El grupo validado.
	 * @throws GastoException Si no se encuentra un grupo con el identificador
	 *                        especificado.
	 */
	private Grupo validateGroupExistente(Long grupoId) {
		Grupo grupo = grupoRepository.findById(grupoId)
				.orElseThrow(() -> new GastoException("Grupo no encontrada con ID: " + grupoId));
		return grupo;
	}

	/**
	 * Realiza validaciones comunes para los miembros de un grupo en los objetos
	 * GastoAutorDTO y DivisionIndividualDTO.
	 *
	 * @param gastoAutorDTOs          La lista de GastoAutorDTO que contiene
	 *                                información sobre los autores del gasto.
	 * @param divisionIndividualDTOs  La lista de DivisionIndividualDTO que contiene
	 *                                información sobre la división individual.
	 * @param userIdsInGroup          El conjunto de identificadores de usuario en
	 *                                el grupo.
	 * @param uniqueUserIdsInDivision El conjunto de identificadores de usuario en
	 *                                la división individual.
	 * @throws GastoException Si hay usuarios repetidos en GastoAutorDTO o
	 *                        DivisionIndividualDTO,
	 *                        si un usuario en GastoAutorDTO no es miembro del
	 *                        grupo, o
	 *                        si DivisionIndividualDTO no contiene todos los
	 *                        miembros del grupo.
	 */
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

	/**
	 * Realiza validaciones de los miembros de un grupo comunes para la creación de
	 * gastos.
	 *
	 * @param gastoAutorDTOs         La lista de GastoAutorDTO que contiene
	 *                               información sobre los autores del gasto.
	 * @param divisionIndividualDTOs La lista de DivisionIndividualDTO que contiene
	 *                               información sobre la división individual.
	 * @param miembros               La lista de usuarios que son miembros del
	 *                               grupo.
	 * @throws GastoException Si hay usuarios repetidos en GastoAutorDTO o
	 *                        DivisionIndividualDTO,
	 *                        si un usuario en GastoAutorDTO no es miembro del
	 *                        grupo, o
	 *                        si DivisionIndividualDTO no contiene todos los
	 *                        miembros del grupo.
	 */
	private void validateGroupMembers(List<GastoAutorDTO> gastoAutorDTOs,
			List<DivisionIndividualDTO> divisionIndividualDTOs,
			List<Usuario> miembros) {

		Set<Long> userIdsInGroup = miembros.stream().map(Usuario::getId).collect(Collectors.toSet());
		Set<Long> uniqueUserIdsInDivision = divisionIndividualDTOs.stream().map(DivisionIndividualDTO::getUserId)
				.collect(Collectors.toSet());

		validateGroupMembersCommon(gastoAutorDTOs, divisionIndividualDTOs, userIdsInGroup, uniqueUserIdsInDivision);
	}

	/**
	 * Realiza validaciones de los miembros de un grupo comunes para la
	 * actualización de gastos.
	 *
	 * @param gastoAutorDTOs         La lista de GastoAutorDTO que contiene
	 *                               información sobre los autores del gasto.
	 * @param divisionIndividualDTOs La lista de DivisionIndividualDTO que contiene
	 *                               información sobre la división individual.
	 * @param divisionesIndividuales La lista de DivisionIndividual existente en el
	 *                               gasto que se está actualizando.
	 * @throws GastoException Si hay usuarios repetidos en GastoAutorDTO o
	 *                        DivisionIndividualDTO,
	 *                        si un usuario en GastoAutorDTO no es miembro del
	 *                        grupo, o
	 *                        si DivisionIndividualDTO no contiene todos los
	 *                        miembros del grupo.
	 */
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

	/**
	 * Crea las deudas entre los usuarios del grupo basándose en la forma de dividir
	 * especificada en el gasto.
	 *
	 * @param formaDividirDTO El objeto FormaDividirDTO que contiene información
	 *                        sobre la forma de dividir.
	 * @param gasto           El gasto asociado al cual se le generarán las deudas
	 *                        entre usuarios.
	 */
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

	/**
	 * Actualiza los saldos de deudas entre los miembros de un grupo.
	 *
	 * @param grupo El grupo del cual se actualizarán los saldos de deudas.
	 */
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

	/**
	 * Actualiza el saldo de pagos y gastos para un gasto específico en el mapa de
	 * saldos de pagos y gastos.
	 *
	 * @param gastoAux           El gasto del cual se actualizarán los saldos.
	 * @param balancePagosGastos El mapa que contiene los saldos de pagos y gastos
	 *                           de los usuarios.
	 */
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

	/**
	 * Obtiene la diferencia entre los saldos de deudas y pagos para cada usuario.
	 *
	 * @param balanceDeudas      El mapa que contiene los saldos de deudas de los
	 *                           usuarios.
	 * @param balancePagosGastos El mapa que contiene los saldos de pagos y gastos
	 *                           de los usuarios.
	 * @return Un mapa que representa la diferencia entre los saldos de deudas y
	 *         pagos para cada usuario.
	 */
	private Map<Long, Double> obtenerDiferenciaBalanceDeudasPagos(Map<Long, Double> balanceDeudas,
			Map<Long, Double> balancePagosGastos) {
		Map<Long, Double> diferenciaBalance = new HashMap<>();

		balanceDeudas.forEach((usuarioId, balanceDeuda) -> diferenciaBalance.merge(usuarioId,
				balancePagosGastos.getOrDefault(usuarioId, 0.0) - balanceDeuda,
				(oldValue, newValue) -> oldValue + newValue));

		return diferenciaBalance;
	}

	/**
	 * Procesa las deudas entre un deudor y una lista de acreedores en un grupo,
	 * ajustando los saldos en el mapa de diferencias de balance.
	 *
	 * @param grupo             El grupo al que pertenecen los usuarios involucrados
	 *                          en las deudas.
	 * @param deudorId          El ID del usuario deudor.
	 * @param acreedores        La lista de IDs de los usuarios acreedores.
	 * @param diferenciaBalance El mapa que contiene las diferencias de balance
	 *                          entre deudas y pagos.
	 */
	private void procesarDeudas(Grupo grupo, Long deudorId, List<Long> acreedores,
			Map<Long, Double> diferenciaBalance) {

		for (Long acreedorId : acreedores) {
			if (diferenciaBalance.get(deudorId) < 0) {
				Double montoDeuda = Math.min(Math.abs(diferenciaBalance.get(deudorId)),
						diferenciaBalance.get(acreedorId));

				deudaUsuarioService.crearDeudaUsuario(deudorId, acreedorId,
						montoDeuda, grupo.getId());

				diferenciaBalance.put(deudorId, diferenciaBalance.get(deudorId) + montoDeuda);
				diferenciaBalance.put(acreedorId, diferenciaBalance.get(acreedorId) - montoDeuda);
			} else {
				break;
			}
		}
	}

	/**
	 * Busca y devuelve una lista de IDs de usuarios acreedores cuyos saldos son
	 * mayores a cero en el mapa de diferencias de balance.
	 *
	 * @param diferenciaBalance El mapa que contiene las diferencias de balance
	 *                          entre deudas y pagos.
	 * @return Una lista de IDs de usuarios acreedores con saldos positivos.
	 */
	private List<Long> buscarAcreedoresParaPagar(Map<Long, Double> diferenciaBalance) {
		return diferenciaBalance.entrySet()
				.stream()
				.filter(entry -> entry.getValue() > 0)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	/**
	 * Verifica si un usuario es miembro de un grupo al que pertenece un gasto.
	 *
	 * @param gasto   El gasto del cual se verificará la pertenencia al grupo.
	 * @param usuario El usuario cuya membresía se verificará.
	 * @return true si el usuario es miembro del grupo; false en caso contrario.
	 */
	public boolean esUsuarioMiembroDelGrupo(Gasto gasto, Usuario usuario) {
		Grupo grupo = gasto.getGrupo();
		return grupo.getMiembros().contains(usuario);
	}

}
