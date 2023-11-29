package com.grupo7.cuentasclaras2.modelos;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class GastoRecurrente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private Gasto gasto;

    @Column(nullable = false)
    private Date fechaFinalizacion;
    @Column(nullable = false)
    private int diasFrecuencia;
    @Column(nullable = false)
    private Date proximaActualizacion;

    @CreationTimestamp
    private Date fechaCreacion;

    @UpdateTimestamp
    private Date fechaActualizacion;

    public GastoRecurrente() {

    }

    public GastoRecurrente(long id, Gasto gasto, Date fechaFinalizacion, int diasFrecuencia,
            Date proximaActualizacion) {
        this.id = id;
        this.gasto = gasto;
        this.fechaFinalizacion = fechaFinalizacion;
        this.diasFrecuencia = diasFrecuencia;
        this.proximaActualizacion = proximaActualizacion;
    }

    public void generarNuevoGasto() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Gasto getGasto() {
        return gasto;
    }

    public void setGasto(Gasto gasto) {
        this.gasto = gasto;
    }

    public Date getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(Date fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public int getDiasFrecuencia() {
        return diasFrecuencia;
    }

    public void setDiasFrecuencia(int diasFrecuencia) {
        this.diasFrecuencia = diasFrecuencia;
    }

    public Date getProximaActualizacion() {
        return proximaActualizacion;
    }

    public void setProximaActualizacion(Date proximaActualizacion) {
        this.proximaActualizacion = proximaActualizacion;
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

}
