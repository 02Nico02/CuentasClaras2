package com.grupo7.cuentasclaras2.DTO;

import com.grupo7.cuentasclaras2.modelos.Usuario;

public class IdEmailUsuarioDTO {
    private long id;
    private String userName;

    public IdEmailUsuarioDTO() {
    }

    public IdEmailUsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.userName = usuario.getUsername();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

}
