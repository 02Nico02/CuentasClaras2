package com.grupo7.cuentasclaras2.DTO;

import com.grupo7.cuentasclaras2.modelos.Usuario;

public class MiembrosGrupoDTO {
    private long idUsuario;
    private String userName;
    private double balance;

    public MiembrosGrupoDTO() {
    }

    public MiembrosGrupoDTO(Usuario usuario) {
        this.idUsuario = usuario.getId();
        this.userName = usuario.getUsername();
        this.balance = 0.0;
    }

    public MiembrosGrupoDTO(Usuario usuario, double balance) {
        this.idUsuario = usuario.getId();
        this.userName = usuario.getUsername();
        this.balance = balance;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

}
