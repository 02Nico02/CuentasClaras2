package com.grupo7.cuentasclaras2.DTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grupo7.cuentasclaras2.modelos.DeudaUsuario;
import com.grupo7.cuentasclaras2.modelos.Gasto;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Pago;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public class GrupoDTO {
    private long id;
    private String nombre;
    private boolean pareja;
    private Date fechaCreacion;
    private CategoriaDTO categoria;
    private List<MiembrosGrupoDTO> miembros;
    private List<GastoDTO> gastos;
    private List<PagoDTO> pagos;
    private List<DeudaUsuarioDTO> deudasUsuarios;

    public GrupoDTO() {
    }

    public GrupoDTO(Grupo grupo, Usuario usuarioAutenticado) {
        this.id = grupo.getId();
        this.nombre = grupo.getNombre();
        this.pareja = grupo.getEsPareja();
        this.fechaCreacion = grupo.getFechaCreacion();
        this.miembros = convertirUsuariosAMiembrosDTO(grupo, usuarioAutenticado);
        if (grupo.getCategoria() != null) {
            this.categoria = new CategoriaDTO(grupo.getCategoria());
        }
        this.gastos = convertirGastosADTOs(grupo.getGastos());
        this.pagos = convertirPagosADTOs(grupo.getPagos());
        this.deudasUsuarios = convertirDeudasUsuariosADTOs(grupo.getDeudas());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isPareja() {
        return pareja;
    }

    public void setPareja(boolean pareja) {
        this.pareja = pareja;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<MiembrosGrupoDTO> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<MiembrosGrupoDTO> miembros) {
        this.miembros = miembros;
    }

    public CategoriaDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDTO categoria) {
        this.categoria = categoria;
    }

    public List<GastoDTO> getGastos() {
        return gastos;
    }

    public void setGastos(List<GastoDTO> gastos) {
        this.gastos = gastos;
    }

    public List<PagoDTO> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoDTO> pagos) {
        this.pagos = pagos;
    }

    public List<DeudaUsuarioDTO> getDeudasUsuarios() {
        return deudasUsuarios;
    }

    public void setDeudasUsuarios(List<DeudaUsuarioDTO> deudasUsuarios) {
        this.deudasUsuarios = deudasUsuarios;
    }

    private List<MiembrosGrupoDTO> convertirUsuariosAMiembrosDTO(Grupo grupo, Usuario usuarioAutenticado) {
        List<MiembrosGrupoDTO> miembrosDTO = new ArrayList<>();
        List<DeudaUsuario> deudas = grupo.getDeudas();
        Map<Usuario, Double> saldos = new HashMap<>();

        for (DeudaUsuario deuda : deudas) {
            if (deuda.getDeudor().equals(usuarioAutenticado)) {
                saldos.put(deuda.getAcreedor(), -deuda.getMonto());
            } else if (deuda.getAcreedor().equals(usuarioAutenticado)) {
                saldos.put(deuda.getDeudor(), deuda.getMonto());
            }
        }

        if (grupo.getMiembros() != null) {
            for (Usuario usuario : grupo.getMiembros()) {
                if (usuario.equals(usuarioAutenticado)) {
                    continue;
                }
                double balance = saldos.getOrDefault(usuario, 0.0);
                MiembrosGrupoDTO miembroDTO = new MiembrosGrupoDTO(usuario, balance);
                miembrosDTO.add(miembroDTO);
            }
        }
        return miembrosDTO;
    }

    private List<GastoDTO> convertirGastosADTOs(List<Gasto> gastos) {
        List<GastoDTO> gastosDTO = new ArrayList<>();
        if (gastos != null) {
            for (Gasto gasto : gastos) {
                GastoDTO gastoDTO = new GastoDTO(gasto);
                gastosDTO.add(gastoDTO);
            }
        }
        return gastosDTO;
    }

    private List<PagoDTO> convertirPagosADTOs(List<Pago> pagos) {
        List<PagoDTO> pagoDTOs = new ArrayList<>();
        if (pagos != null) {
            for (Pago pago : pagos) {
                PagoDTO pagoDTO = new PagoDTO(pago);
                pagoDTOs.add(pagoDTO);
            }
        }
        return pagoDTOs;
    }

    private List<DeudaUsuarioDTO> convertirDeudasUsuariosADTOs(List<DeudaUsuario> deudaUsuarios) {
        List<DeudaUsuarioDTO> deudaUsuarioDTOs = new ArrayList<>();
        if (deudaUsuarios != null) {
            for (DeudaUsuario deudaUsuario : deudaUsuarios) {
                DeudaUsuarioDTO deudaUsuarioDTO = new DeudaUsuarioDTO(deudaUsuario);
                deudaUsuarioDTOs.add(deudaUsuarioDTO);
            }
        }
        return deudaUsuarioDTOs;
    }
}
