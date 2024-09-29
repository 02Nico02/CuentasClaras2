package com.grupo7.cuentasclaras2.DTO;

import java.util.Date;
import java.util.List;

public class CrearGastoDTO {
    private String nombre;
    private Date fecha;
    private Long grupoId;
    private FormaDividirDTO formaDividir;
    private Long categoriaId;
    private List<GastoAutorDTO> gastoAutor;

    public CrearGastoDTO() {
    }

    public CrearGastoDTO(String nombre, Date fecha, Long grupoId, FormaDividirDTO formaDividir,
            Long categoriaId, List<GastoAutorDTO> gastoAutor) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.grupoId = grupoId;
        this.formaDividir = formaDividir;
        this.categoriaId = categoriaId;
        this.gastoAutor = gastoAutor;
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

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public List<GastoAutorDTO> getGastoAutor() {
        return gastoAutor;
    }

    public void setGastoAutor(List<GastoAutorDTO> gastoAutor) {
        this.gastoAutor = gastoAutor;
    }

}
