package com.grupo7.cuentasclaras2.DTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.grupo7.cuentasclaras2.modelos.Gasto;

public class GastoDTO {
    private long id;
    private List<GastoAutorDTO> gastoAutor = new ArrayList<>();
    private String nombre;
    private Date fecha;
    private String imagen;
    private Long grupoId;
    private FormaDividirDTO formaDividir;
    private CategoriaDTO categoria;
    private Boolean editable;
    private Boolean esDeGrupoPareja;

    public GastoDTO() {
    }

    public GastoDTO(Gasto gasto) {
        this.id = gasto.getId();
        this.gastoAutor = gasto.getGastoAutor().stream()
                .map(GastoAutorDTO::new)
                .collect(Collectors.toList());
        this.nombre = gasto.getNombre();
        this.fecha = gasto.getFecha();
        this.imagen = gasto.getImagen();
        this.grupoId = gasto.getGrupo().getId();
        this.formaDividir = new FormaDividirDTO(gasto.getFormaDividir());
        this.categoria = new CategoriaDTO(gasto.getCategoria());
        this.editable = gasto.isEditable();
        this.esDeGrupoPareja = gasto.getGrupo().getEsPareja();
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public FormaDividirDTO getFormaDividir() {
        return formaDividir;
    }

    public void setFormaDividir(FormaDividirDTO formaDividir) {
        this.formaDividir = formaDividir;
    }

    public CategoriaDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDTO categoria) {
        this.categoria = categoria;
    }

    public List<GastoAutorDTO> getGastoAutor() {
        return gastoAutor;
    }

    public void setGastoAutor(List<GastoAutorDTO> gastoAutor) {
        this.gastoAutor = gastoAutor;
    }

    public Boolean isEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getEditable() {
        return editable;
    }

    public Boolean getEsDeGrupoPareja() {
        return esDeGrupoPareja;
    }

    public void setEsDeGrupoPareja(Boolean esDeGrupoPareja) {
        this.esDeGrupoPareja = esDeGrupoPareja;
    }

}
