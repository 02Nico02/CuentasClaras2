package com.grupo7.cuentasclaras2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.grupo7.cuentasclaras2.modelos.Gasto;
import com.grupo7.cuentasclaras2.repositories.GastoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class GastoService {
	@Autowired
	private GastoRepository gastoRepository;

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

}
