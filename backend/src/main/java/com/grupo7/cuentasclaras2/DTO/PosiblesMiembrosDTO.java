package com.grupo7.cuentasclaras2.DTO;

import java.util.List;

public class PosiblesMiembrosDTO {
    private List<IdEmailUsuarioDTO> amigos;
    private List<IdEmailUsuarioDTO> usuarios;

    public PosiblesMiembrosDTO() {
    }

    public PosiblesMiembrosDTO(List<IdEmailUsuarioDTO> amigos, List<IdEmailUsuarioDTO> usuarios) {
        this.amigos = amigos;
        this.usuarios = usuarios;
    }

    public List<IdEmailUsuarioDTO> getAmigos() {
        return amigos;
    }

    public void setAmigos(List<IdEmailUsuarioDTO> amigos) {
        this.amigos = amigos;
    }

    public List<IdEmailUsuarioDTO> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<IdEmailUsuarioDTO> usuarios) {
        this.usuarios = usuarios;
    }

}
