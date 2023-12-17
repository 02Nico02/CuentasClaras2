package com.grupo7.cuentasclaras2.DTO;

public class AmigoDTO {
    private long idUser;
    private String userName;
    private long idGroup;
    private double saldoDisponible;

    public AmigoDTO() {
    }

    public AmigoDTO(long idUser, String userName, long idGroup, double saldoDisponible) {
        this.idUser = idUser;
        this.userName = userName;
        this.idGroup = idGroup;
        this.saldoDisponible = saldoDisponible;
    }

    public long getId() {
        return idUser;
    }

    public void setId(long idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getSaldoDisponible() {
        return saldoDisponible;
    }

    public void setSaldoDisponible(double saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }

    public long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(long idGroup) {
        this.idGroup = idGroup;
    }

}
