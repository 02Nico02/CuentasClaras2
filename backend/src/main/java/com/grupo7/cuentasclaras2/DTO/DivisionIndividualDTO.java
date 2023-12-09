package com.grupo7.cuentasclaras2.DTO;

import com.grupo7.cuentasclaras2.modelos.DivisionIndividual;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public class DivisionIndividualDTO {
    private Long id;
    private Long userId;
    private String userName;
    private double monto;

    public DivisionIndividualDTO() {
    }

    public DivisionIndividualDTO(DivisionIndividual divisionIndividual) {
        this.id = divisionIndividual.getId();
        Usuario usuario = divisionIndividual.getUsuario();
        this.userId = usuario.getId();
        this.userName = usuario.getUsername();
        this.monto = divisionIndividual.getMonto();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

}
