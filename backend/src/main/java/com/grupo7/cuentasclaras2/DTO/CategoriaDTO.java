package com.grupo7.cuentasclaras2.DTO;

import com.grupo7.cuentasclaras2.modelos.Categoria;

public class CategoriaDTO {
    private long id;
    private String nombre;
    private String icon;

    public CategoriaDTO() {
    }

    public CategoriaDTO(Categoria categoria) {
        this.id = categoria.getId();
        this.nombre = categoria.getNombre();
        this.icon = categoria.getIcono();
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
