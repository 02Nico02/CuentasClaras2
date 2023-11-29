package com.grupo7.cuentasclaras2.DTO;

import com.grupo7.cuentasclaras2.modelos.Usuario;

public class UsuarioDTO {
    private long id;
    private String username;
    private String nombres;
    private String apellido;
    private String email;
    private int cantidadAmigos;
    private int cantidadGrupos;
    private int cantidadInvitacionesAmigosRecibidas;
    private int cantidadInvitacionesGrupo;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.nombres = usuario.getNombres();
        this.apellido = usuario.getApellido();
        this.email = usuario.getEmail();
        this.cantidadAmigos = usuario.getAmigos() != null ? usuario.getAmigos().size() : 0;
        this.cantidadGrupos = usuario.getGrupos() != null ? usuario.getGrupos().size() : 0;
        this.cantidadInvitacionesAmigosRecibidas = usuario.getInvitacionesAmigosRecibidas() != null
                ? usuario.getInvitacionesAmigosRecibidas().size()
                : 0;
        this.cantidadInvitacionesGrupo = usuario.getInvitacionesGrupo() != null ? usuario.getInvitacionesGrupo().size()
                : 0;
    }

    public int getCantidadAmigos() {
        return cantidadAmigos;
    }

    public void setCantidadAmigos(int cantidadAmigos) {
        this.cantidadAmigos = cantidadAmigos;
    }

    public int getCantidadGrupos() {
        return cantidadGrupos;
    }

    public void setCantidadGrupos(int cantidadGrupos) {
        this.cantidadGrupos = cantidadGrupos;
    }

    public int getCantidadInvitacionesAmigosRecibidas() {
        return cantidadInvitacionesAmigosRecibidas;
    }

    public void setCantidadInvitacionesAmigosRecibidas(int cantidadInvitacionesAmigosRecibidas) {
        this.cantidadInvitacionesAmigosRecibidas = cantidadInvitacionesAmigosRecibidas;
    }

    public int getCantidadInvitacionesGrupo() {
        return cantidadInvitacionesGrupo;
    }

    public void setCantidadInvitacionesGrupo(int cantidadInvitacionesGrupo) {
        this.cantidadInvitacionesGrupo = cantidadInvitacionesGrupo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
