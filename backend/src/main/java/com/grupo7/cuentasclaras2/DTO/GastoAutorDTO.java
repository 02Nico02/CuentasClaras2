package com.grupo7.cuentasclaras2.DTO;

import com.grupo7.cuentasclaras2.modelos.GastoAutor;

public class GastoAutorDTO {
    private Long id;
    private Double monto;
    private Long userId;
    private String userName;

    public GastoAutorDTO() {
    }

    public GastoAutorDTO(GastoAutor gastoAutor) {
        this.id = gastoAutor.getId();
        this.monto = gastoAutor.getMonto();
        this.userId = gastoAutor.getIntegrante().getId();
        this.userName = gastoAutor.getIntegrante().getUsername();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
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

}
