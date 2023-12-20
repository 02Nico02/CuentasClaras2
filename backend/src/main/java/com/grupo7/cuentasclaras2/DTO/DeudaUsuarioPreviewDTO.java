package com.grupo7.cuentasclaras2.DTO;

public class DeudaUsuarioPreviewDTO {
    private long id;
    private double monto;
    private boolean usuarioDebe;
    private String data;

    public DeudaUsuarioPreviewDTO() {
    }

    public DeudaUsuarioPreviewDTO(long id, double monto, boolean usuarioDebe, String data) {
        this.id = id;
        this.monto = monto;
        this.usuarioDebe = usuarioDebe;
        this.data = data;
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

}
