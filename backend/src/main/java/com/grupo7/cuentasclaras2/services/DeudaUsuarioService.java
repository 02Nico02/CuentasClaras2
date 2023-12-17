package com.grupo7.cuentasclaras2.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo7.cuentasclaras2.exception.GroupException;
import com.grupo7.cuentasclaras2.exception.UserException;
import com.grupo7.cuentasclaras2.modelos.DeudaUsuario;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.DeudaUsuarioRepository;
import com.grupo7.cuentasclaras2.repositories.GrupoRepository;
import com.grupo7.cuentasclaras2.repositories.UsuarioRepository;

@Service
public class DeudaUsuarioService {

	@Autowired
	private DeudaUsuarioRepository deudaUsuarioRepository;

	@Autowired
	private GrupoRepository grupoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	/**
	 * Obtiene una deuda de usuario por su identificador.
	 *
	 * @param deudaId El identificador de la deuda de usuario.
	 * @return La deuda de usuario encontrada, si existe.
	 */
	@Transactional(readOnly = true)
	public Optional<DeudaUsuario> getById(long deudaId) {
		return deudaUsuarioRepository.findById(deudaId);
	}

	/**
	 * Obtiene las deudas de usuario asociadas a un grupo.
	 *
	 * @param grupo El grupo del cual se obtendrán las deudas.
	 * @return Lista de deudas de usuario asociadas al grupo.
	 */
	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasPorGrupo(Grupo grupo) {
		return deudaUsuarioRepository.findByGrupo(grupo);
	}

	/**
	 * Obtiene las deudas de usuario asociadas a un grupo mediante su identificador.
	 *
	 * @param grupoId El identificador del grupo del cual se obtendrán las deudas.
	 * @return Lista de deudas de usuario asociadas al grupo.
	 */
	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasPorIdGrupo(long grupoId) {
		return grupoRepository.findById(grupoId)
				.map(deudaUsuarioRepository::findByGrupo)
				.orElse(Collections.emptyList());
	}

	/**
	 * Obtiene las deudas de usuario entre dos usuarios.
	 *
	 * @param acreedor El usuario acreedor.
	 * @param deudor   El usuario deudor.
	 * @return Lista de deudas de usuario entre los dos usuarios.
	 */
	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasEntreUsuarios(Usuario acreedor, Usuario deudor) {
		return deudaUsuarioRepository.findByAcreedorAndDeudor(acreedor, deudor);
	}

	/**
	 * Obtiene las deudas de usuario donde el usuario es el acreedor.
	 *
	 * @param acreedor El usuario acreedor.
	 * @return Lista de deudas de usuario donde el usuario es el acreedor.
	 */
	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasDeAcreedor(Usuario acreedor) {
		return deudaUsuarioRepository.findByAcreedor(acreedor);
	}

	/**
	 * Obtiene las deudas de usuario donde el usuario es el acreedor mediante su
	 * identificador.
	 *
	 * @param acreedorID El identificador del usuario acreedor.
	 * @return Lista de deudas de usuario donde el usuario es el acreedor.
	 */
	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasDeAcreedorID(long acreedorID) {
		return usuarioRepository.findById(acreedorID)
				.map(deudaUsuarioRepository::findByAcreedor)
				.orElse(Collections.emptyList());
	}

	/**
	 * Obtiene las deudas de usuario donde el usuario es el deudor.
	 *
	 * @param deudor El usuario deudor.
	 * @return Lista de deudas de usuario donde el usuario es el deudor.
	 */
	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasDeDeudor(Usuario deudor) {
		return deudaUsuarioRepository.findByDeudor(deudor);
	}

	/**
	 * Obtiene las deudas de usuario donde el usuario es el deudor mediante su
	 * identificador.
	 *
	 * @param deudorID El identificador del usuario deudor.
	 * @return Lista de deudas de usuario donde el usuario es el deudor.
	 */
	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasDeDeudorID(long deudorID) {
		return usuarioRepository.findById(deudorID)
				.map(deudaUsuarioRepository::findByDeudor)
				.orElse(Collections.emptyList());
	}

	/**
	 * Obtiene las deudas de usuario con un monto mayor que el especificado.
	 *
	 * @param monto El monto mínimo para las deudas a recuperar.
	 * @return Lista de deudas de usuario con un monto mayor que el especificado.
	 */
	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasConMontoMayorQue(double monto) {
		return deudaUsuarioRepository.findByMontoGreaterThan(monto);
	}

	/**
	 * Obtiene las deudas de usuario con un monto menor que el especificado.
	 *
	 * @param monto El monto máximo para las deudas a recuperar.
	 * @return Lista de deudas de usuario con un monto menor que el especificado.
	 */
	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasConMontoMenorQue(double monto) {
		return deudaUsuarioRepository.findByMontoLessThan(monto);
	}

	/**
	 * Realiza un pago para reducir una deuda de usuario.
	 *
	 * @param deudaId   El identificador de la deuda de usuario.
	 * @param montoPago El monto a pagar.
	 * @return `true` si el pago se realizó con éxito, `false` si no se encontró la
	 *         deuda.
	 */
	@Transactional
	public boolean realizarPago(long deudaId, double montoPago) {
		return deudaUsuarioRepository.findById(deudaId)
				.map(deuda -> {
					double nuevoMonto = deuda.getMonto() - montoPago;
					if (nuevoMonto <= 0) {
						deuda.getGrupo().eliminarDeudaUsuario(deuda);
						grupoRepository.save(deuda.getGrupo());
						deudaUsuarioRepository.delete(deuda);
					} else {
						deuda.setMonto(nuevoMonto);
						deudaUsuarioRepository.save(deuda);
					}
					return true;
				})
				.orElse(false);
	}

	/**
	 * Obtiene una deuda de usuario entre dos usuarios en un grupo específico.
	 *
	 * @param groupId    El identificador del grupo.
	 * @param deudorId   El identificador del usuario deudor.
	 * @param acreedorId El identificador del usuario acreedor.
	 * @return La deuda de usuario encontrada, si existe.
	 * @throws GroupException Si el grupo no se encuentra.
	 * @throws UserException  Si no se encuentran los usuarios deudor o acreedor.
	 */
	@Transactional(readOnly = true)
	public Optional<DeudaUsuario> obtenerDeudaEntreUsuariosEnGrupo(long groupId, long deudorId, long acreedorId) {
		Optional<Grupo> grupoOptional = grupoRepository.findById(groupId);
		if (grupoOptional.isEmpty()) {
			throw new GroupException("Grupo no encontrado");
		}

		Grupo grupo = grupoOptional.get();

		Optional<Usuario> deudorOptional = usuarioRepository.findById(deudorId);
		if (deudorOptional.isEmpty()) {
			throw new UserException("Usuario deudor no encontrado");
		}

		Usuario deudor = deudorOptional.get();

		Optional<Usuario> acreedorOptional = usuarioRepository.findById(acreedorId);
		if (acreedorOptional.isEmpty()) {
			throw new UserException("Usuario acreedor no encontrado");
		}

		Usuario acreedor = acreedorOptional.get();

		return deudaUsuarioRepository.findByAcreedorAndDeudorAndGrupo(acreedor, deudor, grupo);
	}

	/**
	 * Crea una nueva deuda de usuario entre dos usuarios en un grupo específico.
	 *
	 * @param deudorId   El identificador del usuario deudor.
	 * @param acreedorId El identificador del usuario acreedor.
	 * @param monto      El monto de la nueva deuda.
	 * @param grupoId    El identificador del grupo.
	 * @return La deuda de usuario creada.
	 */
	@Transactional
	public Optional<DeudaUsuario> crearDeudaUsuario(long deudorId, long acreedorId, double monto, long grupoId) {
		return usuarioRepository.findById(deudorId)
				.flatMap(deudor -> usuarioRepository.findById(acreedorId)
						.flatMap(acreedor -> grupoRepository.findById(grupoId)
								.map(grupo -> {
									// Buscar DeudaUsuario existente entre estos dos usuarios, en este grupo
									Optional<DeudaUsuario> deudaExistente = deudaUsuarioRepository
											.findByDeudorIdAndAcreedorIdAndGrupoId(deudorId, acreedorId, grupoId);

									Optional<DeudaUsuario> deudaInvertida = deudaUsuarioRepository
											.findByDeudorIdAndAcreedorIdAndGrupoId(acreedorId, deudorId, grupoId);

									if (deudaExistente.isPresent()) {
										deudaExistente.get().setMonto(deudaExistente.get().getMonto() + monto);
										return deudaUsuarioRepository.save(deudaExistente.get());
									} else if (deudaInvertida.isPresent()) {
										// Calcular la diferencia y actualizar o crear la DeudaUsuario
										double diferencia = deudaInvertida.get().getMonto() - monto;
										if (diferencia > 0) {
											// Actualizar la DeudaUsuario invertida
											deudaInvertida.get().setMonto(diferencia);
											return deudaUsuarioRepository.save(deudaInvertida.get());
										} else {
											// Eliminar la DeudaUsuario invertida
											deudaInvertida.get().getGrupo().eliminarDeudaUsuario(deudaInvertida.get());
											deudaUsuarioRepository.delete(deudaInvertida.get());

											// Crear nueva DeudaUsuario solo si la diferencia no es 0
											if (diferencia < 0) {
												DeudaUsuario nuevaDeuda = new DeudaUsuario();
												nuevaDeuda.setDeudor(deudor);
												nuevaDeuda.setAcreedor(acreedor);
												nuevaDeuda.setMonto(-diferencia);
												nuevaDeuda.setGrupo(grupo);
												return deudaUsuarioRepository.save(nuevaDeuda);
											}
											return null;
										}
									} else {
										// Crear nueva DeudaUsuario
										DeudaUsuario nuevaDeuda = new DeudaUsuario();
										nuevaDeuda.setDeudor(deudor);
										nuevaDeuda.setAcreedor(acreedor);
										nuevaDeuda.setMonto(monto);
										nuevaDeuda.setGrupo(grupo);
										return deudaUsuarioRepository.save(nuevaDeuda);
									}
								})));
	}

	/**
	 * Consolida las deudas dentro de un grupo, redistribuyendo los montos entre los
	 * usuarios.
	 *
	 * @param grupo El grupo en el cual se consolidarán las deudas.
	 */
	@Transactional
	public void consolidarDeudasEnGrupo(Grupo grupo) {
		List<Usuario> miembros = grupo.getMiembros();

		for (Usuario usuario : miembros) {
			List<DeudaUsuario> deudasUsuario = deudaUsuarioRepository.findByDeudorAndGrupo(usuario, grupo);

			for (DeudaUsuario deudaUsuario : deudasUsuario) {
				List<DeudaUsuario> deudasAcreedor = deudaUsuarioRepository.findByDeudorAndGrupo(
						deudaUsuario.getAcreedor(),
						grupo);

				if (deudasAcreedor.size() == 0) {
					continue;
				}
				double montoPendiente = deudaUsuario.getMonto();
				Map<Long, Double> nuevasDeudasAsumidas = new HashMap<>();

				for (DeudaUsuario deudaAcreedor : deudasAcreedor) {
					if (montoPendiente > 0) {
						double montoAcreedor = deudaAcreedor.getMonto();

						if (montoAcreedor > montoPendiente) {
							deudaAcreedor.setMonto(montoAcreedor - montoPendiente);
							deudaUsuarioRepository.save(deudaAcreedor);
							nuevasDeudasAsumidas.put(deudaAcreedor.getAcreedor().getId(), montoPendiente);
							montoPendiente = 0;
						} else {
							nuevasDeudasAsumidas.put(deudaAcreedor.getAcreedor().getId(), deudaAcreedor.getMonto());
							grupo.eliminarDeudaUsuario(deudaAcreedor);
							deudaUsuarioRepository.delete(deudaAcreedor);
							montoPendiente -= montoAcreedor;
						}
					} else {
						break;
					}
				}

				if (montoPendiente > 0) {
					deudaUsuario.setMonto(montoPendiente);
					deudaUsuarioRepository.save(deudaUsuario);
				} else {
					grupo.eliminarDeudaUsuario(deudaUsuario);
					deudaUsuarioRepository.delete(deudaUsuario);
				}

				for (Map.Entry<Long, Double> entry : nuevasDeudasAsumidas.entrySet()) {
					crearDeudaUsuario(usuario.getId(), entry.getKey(), entry.getValue(), grupo.getId());
				}
			}
		}
	}

}
