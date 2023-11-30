package com.grupo7.cuentasclaras2.DTO;

import com.grupo7.cuentasclaras2.modelos.Pago;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public class PagoDTO {

    private long id;
    private double monto;
    private long autorId;
    private String nombreAutor;
    private long destinatarioId;
    private String nombreDestinatario;
    private long grupoId;

    public PagoDTO() {
    }

    public PagoDTO(Pago pago) {
        this.id = pago.getId();
        this.monto = pago.getMonto();

        Usuario autor = pago.getAutor();
        this.autorId = autor.getId();
        this.nombreAutor = autor.getNombres();

        Usuario destinatario = pago.getDestinatario();
        this.destinatarioId = destinatario.getId();
        this.nombreDestinatario = destinatario.getNombres();
        this.grupoId = pago.getGrupo().getId();
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

    public long getAutorId() {
        return autorId;
    }

    public void setAutorId(long autorId) {
        this.autorId = autorId;
    }

    public long getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(long destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(long grupoId) {
        this.grupoId = grupoId;
    }

    public String getNombreAutor() {
        return nombreAutor;
    }

    public void setNombreAutor(String nombreAutor) {
        this.nombreAutor = nombreAutor;
    }

    public String getNombreDestinatario() {
        return nombreDestinatario;
    }

    public void setNombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
    }

}
