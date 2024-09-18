package com.grupo7.cuentasclaras2.DTO;

import com.grupo7.cuentasclaras2.modelos.DeudaUsuario;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public class DeudaUsuarioDTO {

    private long id;
    private double monto;
    private long acreedorId;
    private String nombreAcreedor;
    private long deudorId;
    private String nombreDeudor;
    private long grupoId;

    public DeudaUsuarioDTO() {
    }

    public DeudaUsuarioDTO(DeudaUsuario deudaUsuario) {
        this.id = deudaUsuario.getId();
        this.monto = deudaUsuario.getMonto();

        Usuario acreedor = deudaUsuario.getAcreedor();
        this.acreedorId = acreedor.getId();
        this.nombreAcreedor = acreedor.getUsername();

        Usuario deudor = deudaUsuario.getDeudor();
        this.deudorId = deudor.getId();
        this.nombreDeudor = deudor.getUsername();
        this.grupoId = deudaUsuario.getGrupo().getId();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public long getAcreedorId() {
        return acreedorId;
    }

    public void setAcreedorId(long acreedorId) {
        this.acreedorId = acreedorId;
    }

    public String getNombreAcreedor() {
        return nombreAcreedor;
    }

    public void setNombreAcreedor(String nombreAcreedor) {
        this.nombreAcreedor = nombreAcreedor;
    }

    public long getDeudorId() {
        return deudorId;
    }

    public void setDeudorId(long deudorId) {
        this.deudorId = deudorId;
    }

    public String getNombreDeudor() {
        return nombreDeudor;
    }

    public void setNombreDeudor(String nombreDeudor) {
        this.nombreDeudor = nombreDeudor;
    }

    public long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(long grupoId) {
        this.grupoId = grupoId;
    }

}
