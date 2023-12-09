package com.grupo7.cuentasclaras2.DTO;

import com.grupo7.cuentasclaras2.modelos.Categoria;

public class CategoriaDTO {
    private long id;
    private String nombre;

    public CategoriaDTO() {
    }

    public CategoriaDTO(Categoria categoria) {
        this.id = categoria.getId();
        this.nombre = categoria.getNombre();
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

}
