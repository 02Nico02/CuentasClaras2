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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 250)
    private String password;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "usuarios_grupos", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "grupo_id"))
    private List<Grupo> grupos;

    @OneToMany(mappedBy = "receptor")
    @JsonIgnore
    private List<InvitacionAmistad> invitacionesAmigosRecibidas;

    @OneToMany(mappedBy = "remitente")
    @JsonIgnore
    private List<InvitacionAmistad> invitacionesAmigosEnviadas;

    @OneToMany(mappedBy = "destinatario")
    private List<Invitacion> invitacionesGrupo;

    @ManyToMany
    @JoinTable(name = "amigos", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "amigo_id"))
    @JsonIgnore
    private List<Usuario> amigos;

    @CreationTimestamp
    private Date fechaCreacion;

    @UpdateTimestamp
    private Date fechaActualizacion;

    public Usuario() {

    }

    public Usuario(String username, String nombres, String apellido, String email, String password) {
        this.username = username;
        this.nombres = nombres;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
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

    public List<Grupo> getGrupos() {
        if (grupos == null) {
            grupos = new ArrayList<>();
        }
        return grupos;
    }

    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }

    public void unirseAGrupo(Grupo grupo) {
        if (grupos == null) {
            grupos = new ArrayList<>();
        }
        if (!grupos.contains(grupo)) {
            grupos.add(grupo);
            grupo.agregarMiembro(this);
        }
    }

    public List<InvitacionAmistad> getInvitacionesAmigosEnviadas() {
        if (invitacionesAmigosEnviadas == null) {
            invitacionesAmigosEnviadas = new ArrayList<>();
        }
        invitacionesAmigosEnviadas.size();
        return invitacionesAmigosEnviadas;
    }

    public void setInvitacionesAmigosEnviadas(List<InvitacionAmistad> invitacionesAmigos) {
        this.invitacionesAmigosEnviadas = invitacionesAmigos;
    }

    public List<Invitacion> getInvitacionesGrupo() {
        if (invitacionesGrupo == null) {
            invitacionesGrupo = new ArrayList<>();
        }
        return invitacionesGrupo;
    }

    public void setInvitacionesGrupo(List<Invitacion> invitacionesGrupo) {
        this.invitacionesGrupo = invitacionesGrupo;
    }

    public List<Usuario> getAmigos() {
        if (amigos == null) {
            amigos = new ArrayList<>();
        }
        return amigos;
    }

    public void setAmigos(List<Usuario> amigos) {
        this.amigos = amigos;
    }

    public void agregarAmigo(Usuario amigo) {
        if (amigos == null) {
            amigos = new ArrayList<>();
        }
        if (!amigos.contains(amigo)) {
            amigos.add(amigo);
            amigo.agregarAmigo(this);
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public List<InvitacionAmistad> getInvitacionesAmigosRecibidas() {
        if (invitacionesAmigosRecibidas == null) {
            invitacionesAmigosRecibidas = new ArrayList<>();
        }
        invitacionesAmigosRecibidas.size();
        return invitacionesAmigosRecibidas;
    }

    public void setInvitacionesAmigosRecibidas(List<InvitacionAmistad> invitacionesAmigosRecibidas) {
        this.invitacionesAmigosRecibidas = invitacionesAmigosRecibidas;
    }

    public void agregarInvitacionAmistadEnviada(InvitacionAmistad invitacion) {
        if (invitacionesAmigosEnviadas == null) {
            invitacionesAmigosEnviadas = new ArrayList<>();
        }
        if (!invitacionesAmigosEnviadas.contains(invitacion)) {
            invitacionesAmigosEnviadas.add(invitacion);
        }
    }

    public void agregarInvitacionAmistadRecibida(InvitacionAmistad invitacion) {
        if (invitacionesAmigosRecibidas == null) {
            invitacionesAmigosRecibidas = new ArrayList<>();
        }
        if (!invitacionesAmigosRecibidas.contains(invitacion)) {
            invitacionesAmigosRecibidas.add(invitacion);
        }
    }

    public void aceptarInvitacionAmistad(InvitacionAmistad invitacion) {
        if (invitacionesAmigosRecibidas != null &&
                invitacionesAmigosRecibidas.contains(invitacion)) {
            invitacionesAmigosRecibidas.remove(invitacion);
            Usuario emisor = invitacion.getRemitente();
            agregarAmigo(emisor);
        }
    }

    public void rechazarInvitacionAmistad(InvitacionAmistad invitacion) {
        if (invitacionesAmigosRecibidas != null) {
            invitacionesAmigosRecibidas.remove(invitacion);
        }
    }

    public void eliminarAmigo(Usuario amigo) {
        if (amigos != null) {
            amigos.remove(amigo);
            amigo.getAmigos().remove(this);
        }
    }

    public static Usuario builder() {
        return null;
    }

    public void addGroupInvitationReceived(Invitacion invitacion) {
        if (invitacionesGrupo == null) {
            invitacionesGrupo = new ArrayList<>();
        }
        if (!invitacionesGrupo.contains(invitacion)) {
            invitacionesGrupo.add(invitacion);
        }
    }

}