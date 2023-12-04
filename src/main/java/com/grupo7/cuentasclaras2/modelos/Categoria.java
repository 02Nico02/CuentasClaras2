package com.grupo7.cuentasclaras2.modelos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String icono;

    @Column(nullable = false)
    private boolean grupo;

    @OneToMany(mappedBy = "categoria")
    private List<Grupo> grupos = new ArrayList<>();

    @OneToMany(mappedBy = "categoria")
    private List<Gasto> gastos = new ArrayList<>();

    @CreationTimestamp
    private Date fechaCreacion;

    @UpdateTimestamp
    private Date fechaActualizacion;

    public Categoria() {

    }

    public Categoria(String nombre, String icono, boolean esGrupo, List<Grupo> grupos, List<Gasto> gastos) {
        this.nombre = nombre;
        this.icono = icono;
        this.grupo = esGrupo;
        this.grupos = grupos;
        this.gastos = gastos;
    }

    public Categoria(String nombre, String icono, boolean grupo) {
        this.nombre = nombre;
        this.icono = icono;
        this.grupo = grupo;
    }

    public boolean isGrupo() {
        return grupo;
    }

    public void setEsGrupo(boolean grupo) {
        this.grupo = grupo;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }

    public List<Gasto> getGastos() {
        return gastos;
    }

    public void setGastos(List<Gasto> gastos) {
        this.gastos = gastos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public void addGroup(Grupo grupo) {
        if (this.grupos != null) {
            this.grupos = new ArrayList<>();
        }
        if (this.isGrupo() && !this.grupos.contains(grupo)) {
            this.grupos.add(grupo);
            grupo.setCategoria(this);
        }
    }

    public void agregarGasto(Gasto gasto) {
        if (!gastos.contains(gasto)) {
            gastos.add(gasto);
            gasto.setCategoria(this);
        }
    }

}
