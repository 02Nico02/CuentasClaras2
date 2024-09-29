package com.grupo7.cuentasclaras2.DTO;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.grupo7.cuentasclaras2.modelos.DeudaUsuario;
import com.grupo7.cuentasclaras2.modelos.Gasto;
import com.grupo7.cuentasclaras2.modelos.GastoAutor;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Pago;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public class GrupoParejaDTO {
    private long id;
    private double balance;
    private IdEmailUsuarioDTO amigo;
    private List<DeudaUsuarioPreviewDTO> deudasUsuarios;
    private List<ActividadDTO> actividades;

    public GrupoParejaDTO() {
    }

    public GrupoParejaDTO(Grupo grupo, Usuario usuarioAutenticado) {
        this.id = grupo.getId();
        this.amigo = obtenerAmigo(grupo.getMiembros(), usuarioAutenticado);
        this.deudasUsuarios = convertirDeudasUsuariosADTOs(grupo.getDeudas(), usuarioAutenticado);
        this.actividades = convertirActividades(grupo, usuarioAutenticado);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<DeudaUsuarioPreviewDTO> getDeudasUsuarios() {
        return deudasUsuarios;
    }

    public void setDeudasUsuarios(List<DeudaUsuarioPreviewDTO> deudasUsuarios) {
        this.deudasUsuarios = deudasUsuarios;
    }

    private IdEmailUsuarioDTO obtenerAmigo(List<Usuario> miembros, Usuario usuarioAutenticado) {
        return miembros.stream()
                .filter(miembro -> !miembro.equals(usuarioAutenticado))
                .findFirst()
                .map(amigo -> new IdEmailUsuarioDTO(amigo))
                .orElse(null);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<ActividadDTO> getActividades() {
        return actividades;
    }

    public void setActividades(List<ActividadDTO> actividades) {
        this.actividades = actividades;
    }

    public IdEmailUsuarioDTO getAmigo() {
        return amigo;
    }

    public void setAmigo(IdEmailUsuarioDTO amigo) {
        this.amigo = amigo;
    }

    private static DecimalFormat getDecimalFormat() {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.getDefault());
        simbolos.setDecimalSeparator(',');
        simbolos.setGroupingSeparator('.');
        return new DecimalFormat("#,###,##0.00", simbolos);
    }

    private String formatCurrency(double amount) {
        return getDecimalFormat().format(amount);
    }

    private List<ActividadDTO> convertirActividades(Grupo grupo, Usuario usuarioAutenticado) {
        List<ActividadDTO> actividades = new ArrayList<>();

        for (Gasto gasto : grupo.getGastos()) {
            ActividadDTO actividad = new ActividadDTO();
            actividad.setId(gasto.getId());
            actividad.setType("gasto");
            actividad.setFecha(gasto.getFechaCreacion());

            String data = generarTextoGasto(gasto, usuarioAutenticado);
            actividad.setData(data);

            actividades.add(actividad);
        }

        for (Pago pago : grupo.getPagos()) {
            ActividadDTO actividad = new ActividadDTO();
            actividad.setId(pago.getId());
            actividad.setType("pago");
            actividad.setFecha(pago.getFechaCreacion());

            String data = generarTextoPago(pago, usuarioAutenticado);
            actividad.setData(data);

            actividades.add(actividad);
        }

        Collections.sort(actividades, (a1, a2) -> a2.getFecha().compareTo(a1.getFecha()));
        return actividades;
    }

    private String generarTextoGasto(Gasto gasto, Usuario usuarioAutenticado) {
        StringBuilder texto = new StringBuilder();
        List<GastoAutor> gastoAutores = gasto.getGastoAutor();
        double totalMonto = gasto.getMontoTotal();

        String montoFormateado = formatCurrency(totalMonto);

        // Obtener la lista de nombres de los autores del gasto
        List<String> nombresAutores = gastoAutores.stream()
                .map(ga -> ga.getIntegrante().getUsername())
                .collect(Collectors.toList());

        if (nombresAutores.size() == 1 && !gastoAutores.get(0).getIntegrante().equals(usuarioAutenticado)) {
            // Caso: Solo un autor y no es el usuario autenticado
            texto.append(nombresAutores.get(0))
                    .append(" gastó $")
                    .append(montoFormateado)
                    .append(" en ")
                    .append(gasto.getNombre());

        } else if (nombresAutores.size() == 1 && gastoAutores.get(0).getIntegrante().equals(usuarioAutenticado)) {
            // Caso: Solo un autor y es el usuario autenticado
            texto.append("Gastaste $")
                    .append(montoFormateado)
                    .append(" en ")
                    .append(gasto.getNombre());

        } else if (nombresAutores.contains(usuarioAutenticado.getUsername())) {
            nombresAutores.remove(usuarioAutenticado.getUsername());
            // Caso: El usuario autenticado está entre los autores
            texto.append(String.join(", ", nombresAutores))
                    .append(" y vos gastaron $")
                    .append(montoFormateado)
                    .append(" en ")
                    .append(gasto.getNombre());

        }

        return texto.toString();
    }

    private String generarTextoPago(Pago pago, Usuario usuarioAutenticado) {
        String montoFormateado = formatCurrency(pago.getMonto());
        String autor = pago.getAutor().getUsername();
        String destinatario = pago.getDestinatario().getUsername();

        if (pago.getAutor().equals(usuarioAutenticado))
            return "Pagaste $" + montoFormateado + " a " + destinatario;
        return autor + " te pagó $" + montoFormateado;

    }

    private List<DeudaUsuarioPreviewDTO> convertirDeudasUsuariosADTOs(List<DeudaUsuario> deudaUsuarios,
            Usuario usuarioAutenticado) {
        List<DeudaUsuarioPreviewDTO> deudasDTO = new ArrayList<>();
        balance = 0.0;

        for (DeudaUsuario deuda : deudaUsuarios) {
            DeudaUsuarioPreviewDTO deudaDTO = new DeudaUsuarioPreviewDTO();
            deudaDTO.setId(deuda.getId());
            deudaDTO.setMonto(deuda.getMonto());
            deudaDTO.setData(generarTextoDeuda(deuda, usuarioAutenticado));
            deudaDTO.setIdAcreedor(deuda.getAcreedor().getId());

            if (deuda.getDeudor().equals(usuarioAutenticado)) {
                balance -= deuda.getMonto();
                deudaDTO.setUsuarioDebe(true);
            } else if (deuda.getAcreedor().equals(usuarioAutenticado)) {
                balance += deuda.getMonto();
                deudaDTO.setUsuarioDebe(false);
            }

            deudasDTO.add(deudaDTO);
        }

        return deudasDTO;
    }

    private String generarTextoDeuda(DeudaUsuario deuda, Usuario usuarioAutenticado) {
        String montoFormateado = formatCurrency(deuda.getMonto());
        String deudor = deuda.getDeudor().getUsername();
        String acreedor = deuda.getAcreedor().getUsername();

        if (deuda.getDeudor().equals(usuarioAutenticado))
            return "Le debes $" + montoFormateado + " a " + acreedor;
        return deudor + " te debe $" + montoFormateado;
    }

}
