package com.grupo7.cuentasclaras2.DTO;

import java.util.Date;

public class NotificationDTO {
    private long id;
    private String type; // Puedes usar un enum para distinguir entre invitación de amigo y de grupo
    private Date fechaCreacion;

    // Campos específicos de InvitacionAmistadDTO
    private long idRemitenteAmistad;
    private String usernameRemitenteAmistad;

    // Campos específicos de InvitacionGrupoDTO
    private long idGrupo;
    private String nombreGrupo;
    private long idRemitenteGrupo;
    private String usernameRemitenteGrupo;

    public NotificationDTO() {
    }

    public NotificationDTO(InvitacionAmistadDTO amistadDTO) {
        this.id = amistadDTO.getId();
        this.type = "Amistad";
        this.fechaCreacion = amistadDTO.getFechaCreacion();
        this.idRemitenteAmistad = amistadDTO.getIdRemitente();
        this.usernameRemitenteAmistad = amistadDTO.getUsernameRemitente();
    }

    public NotificationDTO(InvitacionGrupoDTO grupoDTO) {
        this.id = grupoDTO.getId();
        this.type = "Grupo";
        this.fechaCreacion = grupoDTO.getFechaCreacion();
        this.idGrupo = grupoDTO.getIdGrupo();
        this.nombreGrupo = grupoDTO.getNombreGrupo();
        this.idRemitenteGrupo = grupoDTO.getIdRemitente();
        this.usernameRemitenteGrupo = grupoDTO.getUsernameRemitente();
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

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public long getIdRemitenteAmistad() {
        return idRemitenteAmistad;
    }

    public void setIdRemitenteAmistad(long idRemitenteAmistad) {
        this.idRemitenteAmistad = idRemitenteAmistad;
    }

    public String getUsernameRemitenteAmistad() {
        return usernameRemitenteAmistad;
    }

    public void setUsernameRemitenteAmistad(String usernameRemitenteAmistad) {
        this.usernameRemitenteAmistad = usernameRemitenteAmistad;
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

    public long getIdRemitenteGrupo() {
        return idRemitenteGrupo;
    }

    public void setIdRemitenteGrupo(long idRemitenteGrupo) {
        this.idRemitenteGrupo = idRemitenteGrupo;
    }

    public String getUsernameRemitenteGrupo() {
        return usernameRemitenteGrupo;
    }

    public void setUsernameRemitenteGrupo(String usernameRemitenteGrupo) {
        this.usernameRemitenteGrupo = usernameRemitenteGrupo;
    }

}
