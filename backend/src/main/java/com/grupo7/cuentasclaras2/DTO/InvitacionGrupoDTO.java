package com.grupo7.cuentasclaras2.DTO;

import java.util.Date;

import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Invitacion;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public class InvitacionGrupoDTO {
    private long id;
    private long idGrupo;
    private String nombreGrupo;
    private long idRemitente;
    private String usernameRemitente;
    private Date fechaCreacion;

    public InvitacionGrupoDTO() {
    }

    public InvitacionGrupoDTO(Invitacion invitacion) {
        this.id = invitacion.getId();
        Grupo grupo = invitacion.getGrupo();
        this.idGrupo = grupo.getId();
        this.nombreGrupo = grupo.getNombre();
        Usuario usuario = invitacion.getRemitente();
        this.idRemitente = usuario.getId();
        this.usernameRemitente = usuario.getUsername();
        this.fechaCreacion = invitacion.getFechaCreacion();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(long idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
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
