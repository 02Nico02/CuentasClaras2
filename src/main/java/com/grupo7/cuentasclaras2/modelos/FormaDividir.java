package com.grupo7.cuentasclaras2.modelos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class FormaDividir {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormatosDivision formaDividir;

    @OneToMany(mappedBy = "formaDividir")
    private List<DivisionIndividual> divisionIndividual = new ArrayList<>();

    @ManyToOne
    private Gasto gasto;

    @CreationTimestamp
    private Date fechaCreacion;

    @UpdateTimestamp
    private Date fechaActualizacion;

    public FormaDividir() {

    }

    public FormaDividir(long id, FormatosDivision formaDividir,
            List<DivisionIndividual> divisionIndividual,
            Gasto gasto) {
        super();
        this.id = id;
        this.formaDividir = formaDividir;
        this.divisionIndividual = divisionIndividual;
        this.gasto = gasto;
    }

    public Gasto getGasto() {
        return gasto;
    }

    public void setGasto(Gasto gasto) {
        this.gasto = gasto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public FormatosDivision getFormaDividir() {
        return formaDividir;
    }

    public void setFormaDividir(FormatosDivision formaDividir) {
        this.formaDividir = formaDividir;
    }

    public List<DivisionIndividual> getDivisionIndividual() {
        return divisionIndividual;
    }

    public void setDivisionIndividual(List<DivisionIndividual> divisionIndividual) {
        this.divisionIndividual = divisionIndividual;
    }

    public double getGastoUsuario(Usuario usuario) {
        double gastoUsuario = 0.0;

        for (DivisionIndividual division : divisionIndividual) {
            if (division.getUsuario().equals(usuario)) {
                if (formaDividir == FormatosDivision.PORCENTAJE) {
                    gastoUsuario = (gasto.getMontoTotal() * division.getMonto()) / 100.0;
                } else if (formaDividir == FormatosDivision.MONTO) {
                    gastoUsuario = division.getMonto();
                }
                break;
            }
        }
        return gastoUsuario;
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
