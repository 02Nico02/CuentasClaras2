package com.grupo7.cuentasclaras2.DTO;

import java.util.ArrayList;
import java.util.List;

import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public class GrupoPreviewDTO {
    private long id;
    private String nombre;
    private CategoriaDTO categoria;
    private List<IdEmailUsuarioDTO> miembros;
    private String img;
    private double balance;

    public GrupoPreviewDTO() {
    }

    public GrupoPreviewDTO(Grupo grupo, double balance) {
        this.id = grupo.getId();
        this.nombre = grupo.getNombre();
        this.miembros = convertirUsuariosAMiembrosDTO(grupo.getMiembros());
        if (grupo.getCategoria() != null) {
            this.categoria = new CategoriaDTO(grupo.getCategoria());
        }
        this.balance = balance;
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

    public CategoriaDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDTO categoria) {
        this.categoria = categoria;
    }

    public List<IdEmailUsuarioDTO> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<IdEmailUsuarioDTO> miembros) {
        this.miembros = miembros;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
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
}
