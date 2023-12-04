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

	@Transactional(readOnly = true)
	public Optional<DeudaUsuario> getById(long deudaId) {
		return deudaUsuarioRepository.findById(deudaId);
	}

	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasPorGrupo(Grupo grupo) {
		return deudaUsuarioRepository.findByGrupo(grupo);
	}

	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasPorIdGrupo(long grupoId) {
		return grupoRepository.findById(grupoId)
				.map(deudaUsuarioRepository::findByGrupo)
				.orElse(Collections.emptyList());
	}

	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasEntreUsuarios(Usuario acreedor, Usuario deudor) {
		return deudaUsuarioRepository.findByAcreedorAndDeudor(acreedor, deudor);
	}

	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasDeAcreedor(Usuario acreedor) {
		return deudaUsuarioRepository.findByAcreedor(acreedor);
	}

	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasDeAcreedorID(long acreedorID) {
		return usuarioRepository.findById(acreedorID)
				.map(deudaUsuarioRepository::findByAcreedor)
				.orElse(Collections.emptyList());
	}

	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasDeDeudor(Usuario deudor) {
		return deudaUsuarioRepository.findByDeudor(deudor);
	}

	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasDeDeudorID(long deudorID) {
		return usuarioRepository.findById(deudorID)
				.map(deudaUsuarioRepository::findByDeudor)
				.orElse(Collections.emptyList());
	}

	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasConMontoMayorQue(double monto) {
		return deudaUsuarioRepository.findByMontoGreaterThan(monto);
	}

	@Transactional(readOnly = true)
	public List<DeudaUsuario> obtenerDeudasConMontoMenorQue(double monto) {
		return deudaUsuarioRepository.findByMontoLessThan(monto);
	}

	@Transactional
	public boolean realizarPago(long deudaId, double montoPago) {
		return deudaUsuarioRepository.findById(deudaId)
				.map(deuda -> {
					double nuevoMonto = deuda.getMonto() - montoPago;
					if (nuevoMonto <= 0) {
						deudaUsuarioRepository.delete(deuda);
					} else {
						deuda.setMonto(nuevoMonto);
						deudaUsuarioRepository.save(deuda);
					}
					return true;
				})
				.orElse(false);
	}

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
											deudaUsuarioRepository.delete(deudaInvertida.get());

											// Crear nueva DeudaUsuario
											DeudaUsuario nuevaDeuda = new DeudaUsuario();
											nuevaDeuda.setDeudor(deudor);
											nuevaDeuda.setAcreedor(acreedor);
											nuevaDeuda.setMonto(-diferencia);
											nuevaDeuda.setGrupo(grupo);
											return deudaUsuarioRepository.save(nuevaDeuda);
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

	@Transactional
	public void consolidarDeudasEnGrupo(Grupo grupo) {
		List<Usuario> miembros = grupo.getMiembros();

		for (Usuario usuario : miembros) {
			List<DeudaUsuario> deudasUsuario = deudaUsuarioRepository.findByDeudorAndGrupo(usuario, grupo);

			for (DeudaUsuario deudaUsuario : deudasUsuario) {
				List<DeudaUsuario> deudasAcreedor = deudaUsuarioRepository.findByDeudorAndGrupo(
						deudaUsuario.getAcreedor(),
						grupo);

				double montoPendiente = deudaUsuario.getMonto();
				Map<Long, Double> nuevasDeudasAsumidas = new HashMap<>();

				for (DeudaUsuario deudaAcreedor : deudasAcreedor) {
					if (montoPendiente > 0) {
						double montoAcreedor = deudaAcreedor.getMonto();

						if (montoAcreedor >= montoPendiente) {
							// Pagar completamente la deuda
							deudaAcreedor.setMonto(montoAcreedor - montoPendiente);
							deudaUsuarioRepository.save(deudaAcreedor);
							nuevasDeudasAsumidas.put(deudaAcreedor.getAcreedor().getId(), montoPendiente);
							montoPendiente = 0;
						} else {
							// Pagar parcialmente la deuda y continuar con la siguiente
							nuevasDeudasAsumidas.put(deudaAcreedor.getAcreedor().getId(), deudaAcreedor.getMonto());
							deudaUsuarioRepository.delete(deudaAcreedor);
							montoPendiente -= montoAcreedor;
						}
					} else {
						break; // Ya se ha pagado toda la deuda del usuario
					}
				}

				// Si sobró monto, actualizar la deuda existente; sino, eliminar la deuda
				// existente
				if (montoPendiente > 0) {
					deudaUsuario.setMonto(montoPendiente);
					deudaUsuarioRepository.save(deudaUsuario);
				} else {
					deudaUsuarioRepository.delete(deudaUsuario);
				}

				// Crear las nuevas deudas asumidas
				for (Map.Entry<Long, Double> entry : nuevasDeudasAsumidas.entrySet()) {
					crearDeudaUsuario(usuario.getId(), entry.getKey(), entry.getValue(), grupo.getId());
				}
			}
		}
	}

}
