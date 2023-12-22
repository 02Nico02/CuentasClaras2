package com.grupo7.cuentasclaras2.DTO;

public class DeudaUsuarioPreviewDTO {
    private long id;
    private double monto;
    private boolean usuarioDebe;
    private String data;
    private long idAcreedor;

    public DeudaUsuarioPreviewDTO() {
    }

    public DeudaUsuarioPreviewDTO(long id, double monto, boolean usuarioDebe, String data, long idAcreedor) {
        this.id = id;
        this.monto = monto;
        this.usuarioDebe = usuarioDebe;
        this.data = data;
        this.idAcreedor = idAcreedor;
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

    public boolean getUsuarioDebe() {
        return usuarioDebe;
    }

    public void setUsuarioDebe(boolean usuarioDebe) {
        this.usuarioDebe = usuarioDebe;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getIdAcreedor() {
        return idAcreedor;
    }

    public void setIdAcreedor(long id) {
        this.idAcreedor = id;
    }

}
