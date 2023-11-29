package com.grupo7.cuentasclaras2.modelos;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class DeudaUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private double monto;

    @ManyToOne
    private Grupo grupo;

    @ManyToOne
    private Usuario acreedor;

    @ManyToOne
    private Usuario deudor;

    @CreationTimestamp
    private Date fechaCreacion;

    @UpdateTimestamp
    private Date fechaActualizacion;

    public DeudaUsuario() {

    }

    public DeudaUsuario(long id, double monto, Grupo grupo, Usuario acreedor, Usuario deudor) {
        super();
        this.id = id;
        this.monto = monto;
        this.grupo = grupo;
        this.acreedor = acreedor;
        this.deudor = deudor;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public void pagar(double unMonto) {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Usuario getAcreedor() {
        return acreedor;
    }

    public void setAcreedor(Usuario acreedor) {
        this.acreedor = acreedor;
    }

    public Usuario getDeudor() {
        return deudor;
    }

    public void setDeudor(Usuario deudor) {
        this.deudor = deudor;
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
