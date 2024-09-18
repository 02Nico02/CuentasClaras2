package com.grupo7.cuentasclaras2.DTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.grupo7.cuentasclaras2.modelos.DeudaUsuario;
import com.grupo7.cuentasclaras2.modelos.Gasto;
import com.grupo7.cuentasclaras2.modelos.GastoAutor;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Pago;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public class GrupoDTO {
    private long id;
    private String nombre;
    private boolean pareja;
    private Date fechaCreacion;
    private CategoriaDTO categoria;
    private List<MiembrosGrupoDTO> miembros;
    private List<DeudaUsuarioPreviewDTO> deudasUsuarios;
    private double balance;
    private List<ActividadDTO> actividades;

    public GrupoDTO() {
    }

    public GrupoDTO(Grupo grupo, Usuario usuarioAutenticado) {
        this.id = grupo.getId();
        this.nombre = grupo.getNombre();
        this.pareja = grupo.getEsPareja();
        this.fechaCreacion = grupo.getFechaCreacion();
        this.miembros = convertirUsuariosAMiembrosDTO(grupo, usuarioAutenticado);
        if (grupo.getCategoria() != null) {
            this.categoria = new CategoriaDTO(grupo.getCategoria());
        }
        this.deudasUsuarios = convertirDeudasUsuariosADTOs(grupo.getDeudas(), usuarioAutenticado);
        this.actividades = convertirActividades(grupo, usuarioAutenticado);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isPareja() {
        return pareja;
    }

    public void setPareja(boolean pareja) {
        this.pareja = pareja;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<MiembrosGrupoDTO> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<MiembrosGrupoDTO> miembros) {
        this.miembros = miembros;
    }

    public CategoriaDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDTO categoria) {
        this.categoria = categoria;
    }

    public List<DeudaUsuarioPreviewDTO> getDeudasUsuarios() {
        return deudasUsuarios;
    }

    public void setDeudasUsuarios(List<DeudaUsuarioPreviewDTO> deudasUsuarios) {
        this.deudasUsuarios = deudasUsuarios;
    }

    private List<MiembrosGrupoDTO> convertirUsuariosAMiembrosDTO(Grupo grupo, Usuario usuarioAutenticado) {
        List<MiembrosGrupoDTO> miembrosDTO = new ArrayList<>();
        List<DeudaUsuario> deudas = grupo.getDeudas();
        Map<Usuario, Double> saldos = new HashMap<>();
        balance = 0;

        for (DeudaUsuario deuda : deudas) {
            if (deuda.getDeudor().equals(usuarioAutenticado)) {
                balance -= deuda.getMonto();
                saldos.put(deuda.getAcreedor(), -deuda.getMonto());
            } else if (deuda.getAcreedor().equals(usuarioAutenticado)) {
                balance += deuda.getMonto();
                saldos.put(deuda.getDeudor(), deuda.getMonto());
            }
        }

        if (grupo.getMiembros() != null) {
            for (Usuario usuario : grupo.getMiembros()) {
                if (usuario.equals(usuarioAutenticado)) {
                    continue;
                }
                double balance = saldos.getOrDefault(usuario, 0.0);
                MiembrosGrupoDTO miembroDTO = new MiembrosGrupoDTO(usuario, balance);
                miembrosDTO.add(miembroDTO);
            }
        }
        return miembrosDTO;
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

    private List<ActividadDTO> convertirActividades(Grupo grupo, Usuario usuarioAutenticado) {
        List<ActividadDTO> actividades = new ArrayList<>();

        for (Gasto gasto : grupo.getGastos()) {
            ActividadDTO actividad = new ActividadDTO();
            actividad.setId(gasto.getId());
            actividad.setType("gasto");
            actividad.setFecha(gasto.getFecha());

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

        return actividades;
    }

    private String generarTextoGasto(Gasto gasto, Usuario usuarioAutenticado) {
        StringBuilder texto = new StringBuilder();
        List<GastoAutor> gastoAutores = gasto.getGastoAutor();
        double totalMonto = gasto.getMontoTotal();

        // Obtener la lista de nombres de los autores del gasto
        List<String> nombresAutores = gastoAutores.stream()
                .map(ga -> ga.getIntegrante().getUsername())
                .collect(Collectors.toList());

        if (nombresAutores.size() == 1 && !gastoAutores.get(0).getIntegrante().equals(usuarioAutenticado)) {
            // Caso: Solo un autor y no es el usuario autenticado
            texto.append(nombresAutores.get(0))
                    .append(" gastó $")
                    .append(totalMonto)
                    .append(" en ")
                    .append(gasto.getNombre());

        } else if (nombresAutores.size() == 1 && gastoAutores.get(0).getIntegrante().equals(usuarioAutenticado)) {
            // Caso: Solo un autor y es el usuario autenticado
            texto.append("Gastaste $")
                    .append(totalMonto)
                    .append(" en ")
                    .append(gasto.getNombre());

        } else if (nombresAutores.size() > 1 && !nombresAutores.contains(usuarioAutenticado.getUsername())) {
            // Caso: Más de un autor y ninguno es el usuario autenticado
            texto.append(String.join(", ", nombresAutores))
                    .append(" gastaron ")
                    .append(totalMonto)
                    .append(" en ")
                    .append(gasto.getNombre());

        } else if (nombresAutores.contains(usuarioAutenticado.getUsername())) {
            nombresAutores.remove(usuarioAutenticado.getUsername());
            // Caso: El usuario autenticado está entre los autores
            texto.append(String.join(", ", nombresAutores))
                    .append(" y vos gastaste ")
                    .append(totalMonto)
                    .append(" en ")
                    .append(gasto.getNombre());

        }

        return texto.toString();
    }

    private String generarTextoPago(Pago pago, Usuario usuarioAutenticado) {
        if (pago.getAutor().equals(usuarioAutenticado)) {
            return "Pagaste $" + pago.getMonto() + " a " + pago.getDestinatario().getUsername();
        } else if (pago.getDestinatario().equals(usuarioAutenticado)) {
            return pago.getAutor().getUsername() + " te pagó $" + pago.getMonto();
        } else {
            return pago.getAutor().getUsername() + " pagó $" + pago.getMonto() + " a "
                    + pago.getDestinatario().getUsername();
        }
    }

    private List<DeudaUsuarioPreviewDTO> convertirDeudasUsuariosADTOs(List<DeudaUsuario> deudaUsuarios,
            Usuario usuarioAutenticado) {
        List<DeudaUsuarioPreviewDTO> deudasDTO = new ArrayList<>();

        for (DeudaUsuario deuda : deudaUsuarios) {
            DeudaUsuarioPreviewDTO deudaDTO = new DeudaUsuarioPreviewDTO();
            deudaDTO.setId(deuda.getId());
            deudaDTO.setMonto(deuda.getMonto());
            deudaDTO.setData(generarTextoDeuda(deuda, usuarioAutenticado));
            deudaDTO.setIdAcreedor(deuda.getAcreedor().getId());

            if (deuda.getDeudor().equals(usuarioAutenticado)) {
                deudaDTO.setUsuarioDebe(true);
            } else {
                deudaDTO.setUsuarioDebe(false);
            }

            deudasDTO.add(deudaDTO);
        }

        return deudasDTO;
    }

    private String generarTextoDeuda(DeudaUsuario deuda, Usuario usuarioAutenticado) {
        if (deuda.getDeudor().equals(usuarioAutenticado)) {
            return "Le debes $" + deuda.getMonto() + " a " + deuda.getAcreedor().getUsername();
        } else if (deuda.getAcreedor().equals(usuarioAutenticado)) {
            return deuda.getDeudor().getUsername() + " te debe $" + deuda.getMonto();
        } else {
            return deuda.getDeudor().getUsername() + " debe $" + deuda.getMonto() + " a "
                    + deuda.getAcreedor().getUsername();
        }
    }

}
