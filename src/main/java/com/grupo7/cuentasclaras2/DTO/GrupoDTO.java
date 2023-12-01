package com.grupo7.cuentasclaras2.DTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public class GrupoDTO {
    private long id;
    private String nombre;
    private boolean pareja;
    private Date fechaCreacion;
    private List<IdEmailUsuarioDTO> miembros;
    private CategoriaDTO categoria;

    public GrupoDTO() {
    }

    public GrupoDTO(Grupo grupo) {
        this.id = grupo.getId();
        this.nombre = grupo.getNombre();
        this.pareja = grupo.getEsPareja();
        this.fechaCreacion = grupo.getFechaCreacion();
        this.miembros = convertirUsuariosAMiembrosDTO(grupo.getMiembros());
        if (grupo.getCategoria() != null) {
            this.categoria = new CategoriaDTO(grupo.getCategoria());
        }
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

    public boolean isPareja() {
        return pareja;
    }

    public void setPareja(boolean pareja) {
        this.pareja = pareja;
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

    public CategoriaDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDTO categoria) {
        this.categoria = categoria;
    }

}
