package com.grupo7.cuentasclaras2.modelos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nombre;

    @Column(nullable = false)
    private boolean esPareja;

    @ManyToMany(mappedBy = "grupos", fetch = FetchType.LAZY)
    private List<Usuario> miembros = new ArrayList<>();

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Gasto> gastos = new ArrayList<>();

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Pago> pagos = new ArrayList<>();

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DeudaUsuario> deudas = new ArrayList<>();

    @ManyToOne
    private Categoria categoria;

    @CreationTimestamp
    private Date fechaCreacion;

    @UpdateTimestamp
    private Date fechaActualizacion;

    public Grupo() {
        super();
    }

    public Grupo(String nombre, Categoria categoria, long id, List<Usuario> miembros, List<Gasto> gastos,
            List<Pago> pagos, List<DeudaUsuario> deudas, boolean esPareja) {
        super();
        this.nombre = nombre;
        this.categoria = categoria;
        this.id = id;
        this.miembros = miembros;
        this.gastos = gastos;
        this.pagos = pagos;
        this.deudas = deudas;
        this.esPareja = esPareja;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Usuario> getMiembros() {
        if (miembros == null) {
            miembros = new ArrayList<>();
        }
        return miembros;
    }

    public void setMiembros(List<Usuario> miembros) {
        this.miembros = miembros;
    }

    public void agregarMiembro(Usuario usuario) {
        if (miembros == null) {
            miembros = new ArrayList<>();
        }
        if (!miembros.contains(usuario)) {
            miembros.add(usuario);
            usuario.unirseAGrupo(this);
        }
    }

    public void agregarMiembros(List<Usuario> usuarios) {
        if (miembros == null) {
            miembros = new ArrayList<>();
        }
        List<Usuario> usuariosCopia = new ArrayList<>(usuarios);
        for (Usuario usuario : usuariosCopia) {
            if (!miembros.contains(usuario)) {
                miembros.add(usuario);
                usuario.unirseAGrupo(this);
            }
        }
    }

    public void eliminarMiembro(Usuario usuario) {
        if (miembros != null) {
            miembros.remove(usuario);
        }
    }

    public List<Gasto> getGastos() {
        return gastos;
    }

    public void setGastos(List<Gasto> gastos) {
        this.gastos = gastos;
    }

    public void agregarGasto(Gasto gasto) {
        if (!gastos.contains(gasto)) {
            gastos.add(gasto);
            gasto.setGrupo(this);
        }
    }

    public void eliminarGasto(Gasto gasto) {
        if (gastos != null) {
            gastos.remove(gasto);
        }
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public void agregarPago(Pago pago) {
        if (pagos == null) {
            pagos = new ArrayList<>();
        }
        pagos.add(pago);
    }

    public void eliminarPago(Pago pago) {
        if (pagos != null) {
            pagos.remove(pago);
        }
    }

    public List<DeudaUsuario> getDeudas() {
        return deudas;
    }

    public void setDeudas(List<DeudaUsuario> deudas) {
        this.deudas = deudas;
    }

    public void agregarDeudaUsuario(DeudaUsuario deuda) {
        if (!deudas.contains(deuda)) {
            deudas.add(deuda);
        }
    }

    public void eliminarDeudaUsuario(DeudaUsuario deuda) {
        if (deudas != null) {
            deudas.remove(deuda);
        }
    }

    public void setEsPareja(boolean esPareja) {
        this.esPareja = esPareja;
    }

    public boolean getEsPareja() {
        return esPareja;
    }

    public double calcularTotalGastos() {
        double total = 0;
        if (gastos != null) {
            for (Gasto gasto : gastos) {
                total += gasto.getMontoTotal();
            }
        }
        return total;
    }

    public double calcularTotalPagos() {
        double total = 0;
        if (pagos != null) {
            for (Pago pago : pagos) {
                total += pago.getMonto();
            }
        }
        return total;
    }

    public double calcularTotalDeudas() {
        double total = 0;
        if (deudas != null) {
            for (DeudaUsuario deuda : deudas) {
                total += deuda.getMonto();
            }
        }
        return total;
    }

    public void liquidarDeuda(Usuario usuario) {
        if (deudas != null) {
            for (DeudaUsuario deuda : deudas) {
                if (deuda.getDeudor().equals(usuario)) {
                    deuda.setMonto(0.0);
                    break;
                }
            }
        }
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
