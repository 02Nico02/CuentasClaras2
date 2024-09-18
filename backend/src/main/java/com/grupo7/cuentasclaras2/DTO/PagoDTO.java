package com.grupo7.cuentasclaras2.DTO;

import com.grupo7.cuentasclaras2.modelos.Pago;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public class PagoDTO {

    private Long id;
    private double monto;
    private Long autorId;
    private String nombreAutor;
    private Long destinatarioId;
    private String nombreDestinatario;
    private Long grupoId;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Long getAutorId() {
        return autorId;
    }

    public void setAutorId(Long autorId) {
        this.autorId = autorId;
    }

    public Long getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(Long destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
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
