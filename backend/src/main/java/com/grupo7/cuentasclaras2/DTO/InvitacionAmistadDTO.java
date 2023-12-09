package com.grupo7.cuentasclaras2.DTO;

import java.util.Date;

import com.grupo7.cuentasclaras2.modelos.InvitacionAmistad;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public class InvitacionAmistadDTO {
    private long id;
    private long idRemitente;
    private String usernameRemitente;
    private Date fechaCreacion;

    public InvitacionAmistadDTO() {
    }

    public InvitacionAmistadDTO(InvitacionAmistad invitacionAmistad) {
        this.id = invitacionAmistad.getId();
        Usuario usuario = invitacionAmistad.getRemitente();
        this.idRemitente = usuario.getId();
        this.usernameRemitente = usuario.getUsername();
        this.fechaCreacion = invitacionAmistad.getFechaCreacion();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdRemitente() {
        return idRemitente;
    }

    public void setIdRemitente(long idRemitente) {
        this.idRemitente = idRemitente;
    }

    public String getUsernameRemitente() {
        return usernameRemitente;
    }

    public void setUsernameRemitente(String usernameRemitente) {
        this.usernameRemitente = usernameRemitente;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

}
