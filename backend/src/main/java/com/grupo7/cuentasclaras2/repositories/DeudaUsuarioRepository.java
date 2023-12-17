package com.grupo7.cuentasclaras2.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo7.cuentasclaras2.modelos.DeudaUsuario;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;

@Repository
public interface DeudaUsuarioRepository extends JpaRepository<DeudaUsuario, Long> {

    List<DeudaUsuario> findByGrupo(Grupo grupo);

    List<DeudaUsuario> findByAcreedorAndDeudor(Usuario acreedor, Usuario deudor);

    List<DeudaUsuario> findByAcreedor(Usuario acreedor);

    List<DeudaUsuario> findByDeudor(Usuario deudor);

    List<DeudaUsuario> findByMontoGreaterThan(double monto);

    List<DeudaUsuario> findByMontoLessThan(double monto);

    Optional<DeudaUsuario> findTopByOrderByMontoDesc();

    Optional<DeudaUsuario> findByAcreedorAndDeudorAndGrupo(Usuario acreedor, Usuario deudor, Grupo grupo);

    Optional<DeudaUsuario> findByDeudorIdAndAcreedorIdAndGrupoId(long deudorId, long acreedorId, long grupoId);

    List<DeudaUsuario> findByDeudorAndGrupo(Usuario usuario, Grupo grupo);

    List<DeudaUsuario> findByAcreedorAndGrupo(Usuario deudor, Grupo grupo);

    List<DeudaUsuario> findByDeudorAndAcreedorAndMonto(Usuario deudor, Usuario acreedor, double monto);

}
