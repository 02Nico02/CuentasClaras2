package com.grupo7.cuentasclaras2.serviceTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Invitacion;
import com.grupo7.cuentasclaras2.modelos.InvitacionAmistad;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.GrupoRepository;
import com.grupo7.cuentasclaras2.repositories.InvitacionAmistadRepository;
import com.grupo7.cuentasclaras2.repositories.InvitacionRepository;
import com.grupo7.cuentasclaras2.repositories.UsuarioRepository;
import com.grupo7.cuentasclaras2.services.InvitacionAmistadService;
import com.grupo7.cuentasclaras2.services.InvitacionService;
import com.grupo7.cuentasclaras2.services.UsuarioService;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UsuarioServiceTests {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private InvitacionAmistadService invitacionAmistadService;

    @Autowired
    private InvitacionAmistadRepository invitacionAmistadRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private InvitacionService invitacionService;

    @Autowired
    private InvitacionRepository invitacionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testGetById() {
        // Arrange
        Usuario usuario = new Usuario("JuanPerez", "Juan", "Perez", "juan@gmail.com", "123456");
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Act
        Optional<Usuario> usuarioRecuperado = usuarioService.getById(usuarioGuardado.getId());

        // Assert
        assertTrue(usuarioRecuperado.isPresent());
        assertEquals(usuario.getNombres(), usuarioRecuperado.get().getNombres());
    }

    @Test
    void testGetByUsername() {
        // Arrange
        String username = "JuanPerez";
        Usuario usuario = new Usuario(username, "Juan", "Perez", "juan@gmail.com", "123456");
        usuarioRepository.save(usuario);

        // Act
        Optional<Usuario> usuarioRecuperado = usuarioService.getByUsername(username);

        // Assert
        assertTrue(usuarioRecuperado.isPresent());
        assertEquals(username, usuarioRecuperado.get().getUsername());
    }

    @Test
    void testGetByEmail() {
        // Arrange
        String email = "juan@gmail.com";
        Usuario usuario = new Usuario("JuanPerez", "Juan", "Perez", email, "123456");
        usuarioRepository.save(usuario);

        // Act
        Optional<Usuario> usuarioRecuperado = usuarioService.getByEmail(email);

        // Assert
        assertTrue(usuarioRecuperado.isPresent());
        assertEquals(email, usuarioRecuperado.get().getEmail());
    }

    @Test
    void testSaveUser() {
        // Arrange
        String nombre = "Juan";
        String apellido = "Perez";
        String username = "JuanPerez";
        String email = "juan@gmail.com";
        String password = "123456";

        // Act
        Usuario usuario1 = new Usuario(username, nombre, apellido, email, password);
        Usuario usuarioGuardado = usuarioRepository.save(usuario1);

        Usuario usuarioRecuperado = usuarioRepository.findById(usuarioGuardado.getId()).orElse(null);

        // Assert
        assertNotNull(usuarioRecuperado);
        assertEquals(nombre, usuarioRecuperado.getNombres());
        assertEquals(apellido, usuarioRecuperado.getApellido());
        assertEquals(email, usuarioRecuperado.getEmail());
        assertEquals(username, usuarioRecuperado.getUsername());
        assertEquals(password, usuarioRecuperado.getPassword());
        assertThat(usuarioRecuperado.getId()).isGreaterThan(0);

    }

    @Test
    void testSearchByNameOrLastName() {
        // Arrange
        Usuario usuario1 = new Usuario("JuanPerez", "Juan", "Perez", "juan@gmail.com", "123456");
        Usuario usuario2 = new Usuario("MariaLopez", "Maria", "Lopez", "maria@gmail.com", "abcdef");
        Usuario usuario3 = new Usuario("PedroGomez", "Pedro", "Gomez", "pedro@gmail.com", "ghijkl");
        usuarioRepository.saveAll(List.of(usuario1, usuario2, usuario3));

        // Act
        List<Usuario> resultados = usuarioService.searchByNameOrLastName("Juan", "Lopez");

        // Assert
        assertEquals(2, resultados.size());
    }

    @Test
    void testLogin_ValidCredentials() {
        // Arrange
        String usernameOrEmail = "JuanPerez";
        String password = "123456";
        Usuario usuario = new Usuario(usernameOrEmail, "Juan", "Perez", "juan@gmail.com",
                passwordEncoder.encode(password));
        usuarioRepository.save(usuario);

        // Act
        Optional<Usuario> usuarioLogueado = usuarioService.login(usernameOrEmail, password);

        // Assert
        assertTrue(usuarioLogueado.isPresent());
        assertEquals(usernameOrEmail, usuarioLogueado.get().getUsername());
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Arrange
        String usernameOrEmail = "JuanPerez";
        String password = "123456";
        Usuario usuario = new Usuario(usernameOrEmail, "Juan", "Perez", "juan@gmail.com",
                passwordEncoder.encode(password));
        usuarioRepository.save(usuario);

        // Act
        Optional<Usuario> usuarioLogueado = usuarioService.login(usernameOrEmail, "contraseñaIncorrecta");

        // Assert
        assertTrue(usuarioLogueado.isEmpty());
    }

    @Test
    void testRegisterUser_SuccessfulRegistration() {
        // Arrange
        String username = "NuevoUsuario";
        String email = "nuevo@gmail.com";
        Usuario nuevoUsuario = new Usuario(username, "Nuevo", "Usuario", email, "abcdef");

        // Act
        Optional<Usuario> usuarioRegistrado = usuarioService.registerUser(nuevoUsuario);

        // Assert
        assertTrue(usuarioRegistrado.isPresent());
        assertEquals(username, usuarioRegistrado.get().getUsername());
        assertEquals(email, usuarioRegistrado.get().getEmail());
    }

    @Test
    void testRegisterUser_DuplicateUsername() {
        // Arrange
        String existingUsername = "UsuarioExistente";
        String email = "nuevo@gmail.com";
        Usuario usuarioExistente = new Usuario(existingUsername, "Usuario", "Existente", email, "abcdef");
        usuarioRepository.save(usuarioExistente);

        Usuario nuevoUsuario = new Usuario(existingUsername, "Nuevo", "Usuario", "nuevo@gmail.com", "123456");

        // Act
        Optional<Usuario> usuarioRegistrado = usuarioService.registerUser(nuevoUsuario);

        // Assert
        assertTrue(usuarioRegistrado.isEmpty());
    }

    @Test
    void testRegisterUser_DuplicateEmail() {
        // Arrange
        String username = "NuevoUsuario";
        String existingEmail = "existente@gmail.com";
        Usuario usuarioExistente = new Usuario("UsuarioExistente", "Usuario", "Existente", existingEmail, "abcdef");
        usuarioRepository.save(usuarioExistente);

        Usuario nuevoUsuario = new Usuario(username, "Nuevo", "Usuario", existingEmail, "123456");

        // Act
        Optional<Usuario> usuarioRegistrado = usuarioService.registerUser(nuevoUsuario);

        // Assert
        assertTrue(usuarioRegistrado.isEmpty());
    }

    @Test
    void testEnviarInvitacionAmigo() {
        // Arrange
        Usuario remitente = new Usuario("remitente", "Juan", "Perez", "juan@gmail.com", "password");
        Usuario destinatario = new Usuario("destinatario", "Carlos", "Gomez", "carlos@gmail.com", "password");
        usuarioRepository.save(remitente);
        usuarioRepository.save(destinatario);

        // Act
        Optional<Usuario> result = invitacionAmistadService.sendFriendRequest(remitente.getEmail(),
                destinatario.getEmail());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(remitente.getEmail(), result.get().getEmail());

        // Act
        Optional<InvitacionAmistad> invitacionBuscada = invitacionAmistadRepository
                .findByRemitenteAndReceptor(remitente, destinatario);

        // Assert
        assertTrue(invitacionBuscada.isPresent());
    }

    @Test
    void testAceptarSolicitudAmistad() {
        // Arrange
        Usuario remitente = new Usuario("remitente", "Juan", "Perez", "juan@gmail.com", "password");
        Usuario destinatario = new Usuario("destinatario", "Carlos", "Gomez", "carlos@gmail.com", "password");
        usuarioRepository.save(remitente);
        usuarioRepository.save(destinatario);

        invitacionAmistadService.sendFriendRequest(remitente.getEmail(), destinatario.getEmail());

        // Act
        invitacionAmistadService.aceptarSolicitudAmistad(destinatario, remitente.getId());

        // Assert
        assertTrue(destinatario.getAmigos().contains(remitente));
        assertFalse(invitacionAmistadRepository.findByRemitenteAndReceptor(remitente, destinatario).isPresent());
    }

    @Test
    void testRechazarSolicitudAmistad() {
        // Arrange
        Usuario remitente = new Usuario("remitente", "Juan", "Perez", "juan@gmail.com", "password");
        Usuario destinatario = new Usuario("destinatario", "Carlos", "Gomez", "carlos@gmail.com", "password");
        usuarioRepository.save(remitente);
        usuarioRepository.save(destinatario);

        invitacionAmistadService.sendFriendRequest(remitente.getEmail(), destinatario.getEmail());

        Optional<InvitacionAmistad> invitacion = invitacionAmistadRepository.findByRemitenteAndReceptor(remitente,
                destinatario);

        // Act
        invitacionAmistadService.rechazarSolicitudAmistad(destinatario, invitacion.get().getId());

        // Assert
        assertFalse(destinatario.getAmigos().contains(remitente));
        assertFalse(invitacionAmistadRepository.findByRemitenteAndReceptor(remitente, destinatario).isPresent());
    }

    @Test
    void testEnviarSolicitudAUsuarioExistente() {
        // Arrange
        Usuario remitente = new Usuario("remitente", "Juan", "Perez", "juan@gmail.com", "password");
        Usuario destinatario = new Usuario("destinatario", "Carlos", "Gomez", "carlos@gmail.com", "password");
        usuarioRepository.save(remitente);
        usuarioRepository.save(destinatario);

        remitente.agregarAmigo(destinatario);
        usuarioRepository.save(remitente);

        // Act
        Optional<Usuario> result = invitacionAmistadService.sendFriendRequest(remitente.getEmail(),
                destinatario.getEmail());

        // Assert
        assertTrue(result.isEmpty());
        assertFalse(invitacionAmistadRepository.findByRemitenteAndReceptor(remitente, destinatario).isPresent());
    }

    @Test
    void testEnviarSolicitudAUnoMismo() {
        // Arrange
        Usuario usuario = new Usuario("remitente", "Juan", "Perez", "juan@gmail.com", "password");
        usuarioRepository.save(usuario);

        // Act
        Optional<Usuario> result = invitacionAmistadService.sendFriendRequest(usuario.getEmail(), usuario.getEmail());

        // Assert
        System.out.println("result: " + result);
        assertTrue(result.isEmpty());
        assertFalse(invitacionAmistadRepository.findByRemitenteAndReceptor(usuario, usuario).isPresent());
    }

    @Test
    void testAceptarSolicitudInexistente() {
        // Arrange
        Usuario usuario = new Usuario("remitente", "Juan", "Perez", "juan@gmail.com", "password");
        usuarioRepository.save(usuario);

        // Act & Assert
        assertDoesNotThrow(() -> invitacionAmistadService.aceptarSolicitudAmistad(usuario, 999L));
    }

    @Test
    void testRechazarSolicitudInexistente() {
        // Arrange
        Usuario usuario = new Usuario("remitente", "Juan", "Perez", "juan@gmail.com", "password");
        usuarioRepository.save(usuario);

        // Act & Assert
        assertDoesNotThrow(() -> invitacionAmistadService.rechazarSolicitudAmistad(usuario, 999L));
    }

    @Test
    void testEnviarInvitacionGrupoExitosa() {
        // Arrange
        Usuario remitente = new Usuario("remitente", "Juan", "Perez", "juan@gmail.com", "password");
        Usuario destinatario = new Usuario("destinatario", "Carlos", "Gomez", "carlos@gmail.com", "password");
        Grupo grupo = new Grupo();

        usuarioRepository.save(remitente);
        usuarioRepository.save(destinatario);
        grupoRepository.save(grupo);
        grupo.agregarMiembro(remitente);

        // Act
        boolean result = invitacionService.enviarInvitacion(remitente.getId(), destinatario.getId(), grupo.getId());

        // Assert
        assertTrue(result);
        assertEquals(1, invitacionRepository.count());
    }

    @Test
    void testEnviarInvitacionGrupoFallaPorGrupoNoMiembro() {
        // Arrange
        Usuario remitente = new Usuario("remitente", "Juan", "Perez", "juan@gmail.com", "password");
        Usuario destinatario = new Usuario("destinatario", "Carlos", "Gomez", "carlos@gmail.com", "password");
        Grupo grupo = new Grupo();

        usuarioRepository.save(remitente);
        usuarioRepository.save(destinatario);
        grupoRepository.save(grupo);

        // Act
        boolean result = invitacionService.enviarInvitacion(remitente.getId(), destinatario.getId(), grupo.getId());

        // Assert
        assertFalse(result);
        assertEquals(0, invitacionRepository.count());
    }

    @Test
    void testAceptarInvitacionGrupo() {
        // Arrange
        Usuario destinatario = new Usuario("destinatario", "Carlos", "Gomez", "carlos@gmail.com", "password");
        Grupo grupo = new Grupo();
        Invitacion invitacion = new Invitacion();

        usuarioRepository.save(destinatario);
        grupoRepository.save(grupo);
        invitacion.setDestinatario(destinatario);
        invitacion.setGrupo(grupo);
        invitacionRepository.save(invitacion);

        // Act
        invitacionService.aceptarInvitacion(destinatario, invitacion.getId());

        // Assert
        assertTrue(grupo.getMiembros().contains(destinatario));
        assertEquals(0, invitacionRepository.count());
    }

    @Test
    void testRechazarInvitacionGrupo() {
        // Arrange
        Usuario destinatario = new Usuario("destinatario", "Carlos", "Gomez", "carlos@gmail.com", "password");
        Invitacion invitacion = new Invitacion();
        Grupo grupo = new Grupo();

        usuarioRepository.save(destinatario);
        grupoRepository.save(grupo);
        invitacion.setDestinatario(destinatario);
        invitacion.setGrupo(grupo);
        invitacionRepository.save(invitacion);
        assertEquals(1, invitacionRepository.count());

        // Act
        invitacionService.rechazarInvitacion(destinatario, invitacion.getId());

        // Assert
        assertFalse(grupo.getMiembros().contains(destinatario));
        assertEquals(0, invitacionRepository.count());
    }

    @Test
    void testAceptarInvitacionGrupoInexistente() {
        // Arrange
        Usuario destinatario = new Usuario("destinatario", "Carlos", "Gomez", "carlos@gmail.com", "password");
        Invitacion invitacion = new Invitacion();

        usuarioRepository.save(destinatario);
        invitacion.setDestinatario(destinatario);
        invitacionRepository.save(invitacion);

        // Act & Assert
        assertDoesNotThrow(() -> invitacionService.aceptarInvitacion(destinatario, 999L));
    }

    @Test
    void testRechazarInvitacionGrupoInexistente() {
        // Arrange
        Usuario destinatario = new Usuario("destinatario", "Carlos", "Gomez", "carlos@gmail.com", "password");
        Invitacion invitacion = new Invitacion();

        usuarioRepository.save(destinatario);
        invitacion.setDestinatario(destinatario);
        invitacionRepository.save(invitacion);

        // Act & Assert
        assertDoesNotThrow(() -> invitacionService.rechazarInvitacion(destinatario, 999L));
    }

    @Test
    void testEnviarInvitacionGrupoFallaPorDestinatarioInexistente() {
        // Arrange
        Usuario remitente = new Usuario("remitente", "Juan", "Perez", "juan@gmail.com", "password");
        Grupo grupo = new Grupo();

        usuarioRepository.save(remitente);
        grupoRepository.save(grupo);
        grupo.agregarMiembro(remitente);

        // Act
        boolean result = invitacionService.enviarInvitacion(remitente.getId(), 999L, grupo.getId());

        // Assert
        assertFalse(result);
        assertEquals(0, invitacionRepository.count());
    }

    @Test
    void testEnviarInvitacionGrupoFallaPorGrupoInexistente() {
        // Arrange
        Usuario remitente = new Usuario("remitente", "Juan", "Perez", "juan@gmail.com", "password");
        Usuario destinatario = new Usuario("destinatario", "Carlos", "Gomez", "carlos@gmail.com", "password");

        usuarioRepository.save(remitente);
        usuarioRepository.save(destinatario);

        // Act
        boolean result = invitacionService.enviarInvitacion(remitente.getId(), destinatario.getId(), 999L);

        // Assert
        assertFalse(result);
        assertEquals(0, invitacionRepository.count());
    }

    @Test
    void testAceptarInvitacionGrupoUsuarioDiferente() {
        // Arrange
        Usuario destinatario = new Usuario("destinatario", "Carlos", "Gomez", "carlos@gmail.com", "password");
        Usuario otroUsuario = new Usuario("otro", "Otro", "Usuario", "otro@gmail.com", "password");
        Invitacion invitacion = new Invitacion();
        Grupo grupo = new Grupo();

        usuarioRepository.save(destinatario);
        usuarioRepository.save(otroUsuario);
        grupoRepository.save(grupo);
        invitacion.setDestinatario(destinatario);
        invitacion.setGrupo(grupo);
        invitacionRepository.save(invitacion);

        // Act
        invitacionService.aceptarInvitacion(otroUsuario, invitacion.getId());

        // Assert
        assertFalse(grupo.getMiembros().contains(otroUsuario));
    }

}
