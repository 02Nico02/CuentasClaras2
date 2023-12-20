package com.grupo7.cuentasclaras2.DTO;

import java.util.Date;

public class ActividadDTO {
    private long id;
    private String type;
    private String data;
    private Date fecha;

    public ActividadDTO() {
    }

    public ActividadDTO(long id, String type, String data, Date fecha) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.fecha = fecha;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

}
