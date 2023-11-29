package com.grupo7.cuentasclaras2.modelos;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Gasto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "gasto")
    private List<GastoAutor> gastoAutor;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Date fecha;

    private String imagen;

    @ManyToOne
    private Grupo grupo;

    @ManyToOne
    private FormaDividir formaDividir;

    @ManyToOne
    private Categoria categoria;

    @CreationTimestamp
    private Date fechaCreacion;

    @UpdateTimestamp
    private Date fechaActualizacion;

    public Gasto() {

    }

    public Gasto(long id, List<GastoAutor> gastoAutor, String nombre, Date fecha, String imagen, Grupo grupo,
            FormaDividir formaDividir, Categoria categoria) {
        super();
        this.id = id;
        this.gastoAutor = gastoAutor;
        this.nombre = nombre;
        this.fecha = fecha;
        this.imagen = imagen;
        this.grupo = grupo;
        this.formaDividir = formaDividir;
        this.categoria = categoria;
    }

    public void editarInformacion(Double monto, Date fecha, String imagen, Usuario integrante,
            FormaDividir formaDividir, Categoria categoria) {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getMontoTotal() {
        double montoTotal = 0.0;
        if (gastoAutor != null) {
            for (GastoAutor autor : gastoAutor) {
                montoTotal += autor.getMonto();
            }
        }

        return montoTotal;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public List<GastoAutor> getGastoAutor() {
        return gastoAutor;
    }

    public void setGastoAutor(List<GastoAutor> gastoAutor) {
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public FormaDividir getFormaDividir() {
        return formaDividir;
    }

    public void setFormaDividir(FormaDividir formaDividir) {
        this.formaDividir = formaDividir;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
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