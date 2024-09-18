package com.grupo7.cuentasclaras2.DTO;

import java.util.ArrayList;
import java.util.List;

import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Invitacion;
import com.grupo7.cuentasclaras2.modelos.InvitacionAmistad;
import com.grupo7.cuentasclaras2.modelos.Usuario;

public class UsuarioDTO {
    private long id;
    private String username;
    private String nombres;
    private String apellido;
    private String email;
    private List<IdEmailUsuarioDTO> amigos;
    private List<Long> idsGrupos;
    private List<InvitacionAmistadDTO> invitacionesAmigosRecibidas;
    private List<InvitacionGrupoDTO> invitacionesGruposRecibidas;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.nombres = usuario.getNombres();
        this.apellido = usuario.getApellido();
        this.email = usuario.getEmail();
        this.amigos = convertirAmigosDTO(usuario.getAmigos());
        this.idsGrupos = obtenerIdsGrupos(usuario.getGrupos());
        this.invitacionesAmigosRecibidas = obtenerInvitacionesAmigosDTO(usuario.getInvitacionesAmigosRecibidas());
        this.invitacionesGruposRecibidas = obtenerInvitacionesGruposDTO(usuario.getInvitacionesGrupo());
    }

    public List<IdEmailUsuarioDTO> getAmigos() {
        return amigos;
    }

    public void setamigos(List<IdEmailUsuarioDTO> amigos) {
        this.amigos = amigos;
    }

    public List<Long> getIdsGrupos() {
        return idsGrupos;
    }

    public void setIdsGrupos(List<Long> idsGrupos) {
        this.idsGrupos = idsGrupos;
    }

    public List<InvitacionAmistadDTO> getInvitacionesAmigosRecibidas() {
        return invitacionesAmigosRecibidas;
    }

    public void setInvitacionesAmigosRecibidas(List<InvitacionAmistadDTO> invitacionesAmigosRecibidas) {
        this.invitacionesAmigosRecibidas = invitacionesAmigosRecibidas;
    }

    public List<InvitacionGrupoDTO> getInvitacionesGruposRecibidas() {
        return invitacionesGruposRecibidas;
    }

    public void setInvitacionesGruposRecibidas(List<InvitacionGrupoDTO> invitacionesGruposRecibidas) {
        this.invitacionesGruposRecibidas = invitacionesGruposRecibidas;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private List<IdEmailUsuarioDTO> convertirAmigosDTO(List<Usuario> amigos) {
        List<IdEmailUsuarioDTO> amigosDTO = new ArrayList<>();
        if (amigos != null) {
            for (Usuario amigo : amigos) {
                IdEmailUsuarioDTO amigoDTO = new IdEmailUsuarioDTO(amigo);
                amigosDTO.add(amigoDTO);
            }
        }
        return amigosDTO;
    }

    private List<Long> obtenerIdsGrupos(List<Grupo> grupos) {
        List<Long> ids = new ArrayList<>();
        if (grupos != null) {
            for (Grupo grupo : grupos) {
                ids.add(grupo.getId());
            }
        }
        return ids;
    }

    private List<InvitacionAmistadDTO> obtenerInvitacionesAmigosDTO(List<InvitacionAmistad> invitaciones) {
        List<InvitacionAmistadDTO> invitacionesDTO = new ArrayList<>();
        if (invitaciones != null) {
            for (InvitacionAmistad invitacion : invitaciones) {
                InvitacionAmistadDTO invitacionDTO = new InvitacionAmistadDTO(invitacion);
                invitacionesDTO.add(invitacionDTO);
            }
        }
        return invitacionesDTO;
    }

    private List<InvitacionGrupoDTO> obtenerInvitacionesGruposDTO(List<Invitacion> invitaciones) {
        List<InvitacionGrupoDTO> invitacionesDTO = new ArrayList<>();
        if (invitaciones != null) {
            for (Invitacion invitacion : invitaciones) {
                InvitacionGrupoDTO invitacionDTO = new InvitacionGrupoDTO(invitacion);
                invitacionesDTO.add(invitacionDTO);
            }
        }
        return invitacionesDTO;
    }
}
