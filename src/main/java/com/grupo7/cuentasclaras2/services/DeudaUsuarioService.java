package com.grupo7.cuentasclaras2.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public Optional<DeudaUsuario> getById(long deudaId) {
		return deudaUsuarioRepository.findById(deudaId);
	}

	public List<DeudaUsuario> obtenerDeudasPorGrupo(Grupo grupo) {
		return deudaUsuarioRepository.findByGrupo(grupo);
	}

	public List<DeudaUsuario> obtenerDeudasPorIdGrupo(long grupoId) {
		Optional<Grupo> grupoOptional = grupoRepository.findById(grupoId);
		if (grupoOptional.isPresent()) {
			Grupo grupo = grupoOptional.get();
			return deudaUsuarioRepository.findByGrupo(grupo);
		}
		return Collections.emptyList();
	}

	public List<DeudaUsuario> obtenerDeudasEntreUsuarios(Usuario acreedor, Usuario deudor) {
		return deudaUsuarioRepository.findByAcreedorAndDeudor(acreedor, deudor);
	}

	public List<DeudaUsuario> obtenerDeudasDeAcreedor(Usuario acreedor) {
		return deudaUsuarioRepository.findByAcreedor(acreedor);
	}

	public List<DeudaUsuario> obtenerDeudasDeAcreedorID(long acreedorID) {
		Optional<Usuario> acreedorOptional = usuarioRepository.findById(acreedorID);
		if (acreedorOptional.isPresent()) {
			Usuario acreedor = acreedorOptional.get();
			return deudaUsuarioRepository.findByAcreedor(acreedor);
		}
		return Collections.emptyList();
	}

	public List<DeudaUsuario> obtenerDeudasDeDeudor(Usuario deudor) {
		return deudaUsuarioRepository.findByDeudor(deudor);
	}

	public List<DeudaUsuario> obtenerDeudasDeDeudorID(long deudorID) {
		Optional<Usuario> deudorOptional = usuarioRepository.findById(deudorID);
		if (deudorOptional.isPresent()) {
			Usuario deudor = deudorOptional.get();
			return deudaUsuarioRepository.findByDeudor(deudor);
		}
		return Collections.emptyList();
	}

	public List<DeudaUsuario> obtenerDeudasConMontoMayorQue(double monto) {
		return deudaUsuarioRepository.findByMontoGreaterThan(monto);
	}

	public List<DeudaUsuario> obtenerDeudasConMontoMenorQue(double monto) {
		return deudaUsuarioRepository.findByMontoLessThan(monto);
	}

	public boolean realizarPago(long deudaId, double montoPago) {
		Optional<DeudaUsuario> deudaOptional = deudaUsuarioRepository.findById(deudaId);

		if (deudaOptional.isPresent()) {
			DeudaUsuario deuda = deudaOptional.get();
			double montoActual = deuda.getMonto();
			double nuevoMonto = montoActual - montoPago;

			if (nuevoMonto <= 0) {
				deudaUsuarioRepository.delete(deuda);
			} else {
				deuda.setMonto(nuevoMonto);
				deudaUsuarioRepository.save(deuda);
			}
		}

		return false;
	}

	public Optional<DeudaUsuario> obtenerDeudaEntreUsuariosEnGrupo(long groupId, long deudorId, long acreedorId) {
		Optional<Grupo> grupoOptional = grupoRepository.findById(groupId);

		if (grupoOptional.isPresent()) {
			Grupo grupo = grupoOptional.get();

			Optional<Usuario> deudorOptional = usuarioRepository.findById(deudorId);
			Optional<Usuario> acreedorOptional = usuarioRepository.findById(acreedorId);

			if (deudorOptional.isPresent() && acreedorOptional.isPresent()) {
				Usuario deudor = deudorOptional.get();
				Usuario acreedor = acreedorOptional.get();

				Optional<DeudaUsuario> deudaUsuarioOptional = deudaUsuarioRepository
						.findByAcreedorAndDeudorAndGrupo(acreedor, deudor, grupo);

				return deudaUsuarioOptional;
			}
		}

		return Optional.empty();
	}

	public Optional<DeudaUsuario> crearDeudaUsuario(long deudorId, long acreedorId, double monto, long grupoId) {
		Optional<Usuario> deudorOptional = usuarioRepository.findById(deudorId);
		Optional<Usuario> acreedorOptional = usuarioRepository.findById(acreedorId);
		Optional<Grupo> grupoOptional = grupoRepository.findById(grupoId);

		if (deudorOptional.isPresent() && acreedorOptional.isPresent() && grupoOptional.isPresent()) {
			Usuario deudor = deudorOptional.get();
			Usuario acreedor = acreedorOptional.get();
			Grupo grupo = grupoOptional.get();

			DeudaUsuario nuevaDeuda = new DeudaUsuario();
			nuevaDeuda.setDeudor(deudor);
			nuevaDeuda.setAcreedor(acreedor);
			nuevaDeuda.setMonto(monto);
			nuevaDeuda.setGrupo(grupo);

			return Optional.of(deudaUsuarioRepository.save(nuevaDeuda));
		}

		return Optional.empty();
	}

}
