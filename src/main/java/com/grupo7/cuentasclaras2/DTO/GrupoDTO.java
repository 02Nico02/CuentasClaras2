package com.grupo7.cuentasclaras2.DTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public class GrupoDTO {
    private long id;
    private String nombre;
    private boolean esPareja;
    private Date fechaCreacion;
    private List<IdEmailUsuarioDTO> miembros;

    public GrupoDTO(Grupo grupo) {
        this.id = grupo.getId();
        this.nombre = grupo.getNombre();
        this.esPareja = grupo.getEsPareja();
        this.fechaCreacion = grupo.getFechaCreacion();
        this.miembros = convertirUsuariosAMiembrosDTO(grupo.getMiembros());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEsPareja() {
        return esPareja;
    }

    public void setEsPareja(boolean esPareja) {
        this.esPareja = esPareja;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    private List<IdEmailUsuarioDTO> convertirUsuariosAMiembrosDTO(List<Usuario> usuarios) {
        List<IdEmailUsuarioDTO> miembrosDTO = new ArrayList<>();
        if (usuarios != null) {
            for (Usuario usuario : usuarios) {
                IdEmailUsuarioDTO usuarioDTO = new IdEmailUsuarioDTO(usuario);
                miembrosDTO.add(usuarioDTO);
            }
        }
        return miembrosDTO;
    }

    public List<IdEmailUsuarioDTO> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<IdEmailUsuarioDTO> miembros) {
        this.miembros = miembros;
    }

}
