package com.grupo7.cuentasclaras2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo7.cuentasclaras2.DTO.UsuarioDTO;
import com.grupo7.cuentasclaras2.exception.UserAlreadyExistsException;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.services.InvitacionAmistadService;
import com.grupo7.cuentasclaras2.services.InvitacionService;
import com.grupo7.cuentasclaras2.services.UsuarioService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private InvitacionAmistadService invitacionAmistadService;

    @Autowired
    private InvitacionService invitacionService;

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUserById(@PathVariable Long id) {
        Optional<Usuario> user = usuarioService.getById(id);
        return user.map(value -> new ResponseEntity<>(new UsuarioDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UsuarioDTO> getUserByUsername(@PathVariable String username) {
        Optional<Usuario> user = usuarioService.getByUsername(username);
        return user.map(value -> new ResponseEntity<>(new UsuarioDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> getUserByEmail(@PathVariable String email) {
        Optional<Usuario> user = usuarioService.getByEmail(email);
        return user.map(value -> new ResponseEntity<>(new UsuarioDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioDTO> registerUser(@RequestBody Usuario newUser) {
        Optional<Usuario> registeredUser = usuarioService.registerUser(newUser);

        return registeredUser.map(value -> new ResponseEntity<>(new UsuarioDTO(value), HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@PathVariable long userId, @RequestBody UsuarioDTO usuarioDTO) {
        try {
            Optional<Usuario> usuarioExistenteOptional = usuarioService.getById(userId);
            if (usuarioExistenteOptional.isPresent()) {
                Usuario usuarioExistente = usuarioExistenteOptional.get();
                Usuario usuarioGuardado = usuarioService.updateUserDataFromDTO(usuarioExistente, usuarioDTO);
                return new ResponseEntity<>(new UsuarioDTO(usuarioGuardado), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (UserAlreadyExistsException e) {
            e.getMessage();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody Map<String, String> credentials) {
        String usernameOrEmail = credentials.get("usernameOrEmail");
        String password = credentials.get("password");

        Optional<Usuario> user = usuarioService.login(usernameOrEmail, password);

        return user.map(value -> new ResponseEntity<>(new UsuarioDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @PostMapping("/sendFriendRequest")
    public ResponseEntity<String> sendFriendRequest(
            @RequestParam String senderEmail,
            @RequestParam String receiverEmail) {
        Optional<Usuario> senderOptional = invitacionAmistadService.sendFriendRequest(senderEmail, receiverEmail);

        if (senderOptional.isPresent()) {
            return new ResponseEntity<>("Solicitud de amistad enviada con éxito.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No se pudo enviar la solicitud de amistad.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/hasFriendRequests/{email}")
    public ResponseEntity<Boolean> hasFriendRequests(@PathVariable String email) {
        Optional<Usuario> user = usuarioService.getByEmail(email);

        if (user.isPresent()) {
            Usuario usuario = user.get();

            boolean hasFriendRequests = usuario.getInvitacionesAmigosRecibidas() != null
                    && !usuario.getInvitacionesAmigosRecibidas().isEmpty();

            return new ResponseEntity<>(hasFriendRequests, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/acceptFriendRequest")
    public ResponseEntity<String> acceptFriendRequest(
            @RequestParam String userEmail,
            @RequestParam Long invitationId) {
        Optional<Usuario> userOptional = usuarioService.getByEmail(userEmail);

        if (userOptional.isPresent()) {
            Usuario usuario = userOptional.get();
            invitacionAmistadService.aceptarSolicitudAmistad(usuario, invitationId);
            return new ResponseEntity<>("Solicitud de amistad aceptada con éxito.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Usuario no encontrado.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/rejectFriendRequest")
    public ResponseEntity<String> rejectFriendRequest(
            @RequestParam String userEmail,
            @RequestParam Long invitationId) {
        Optional<Usuario> userOptional = usuarioService.getByEmail(userEmail);

        if (userOptional.isPresent()) {
            Usuario usuario = userOptional.get();
            invitacionAmistadService.rechazarSolicitudAmistad(usuario, invitationId);
            return new ResponseEntity<>("Solicitud de amistad rechazada con éxito.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Usuario no encontrado.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/sendGroupInvitation")
    public ResponseEntity<String> sendGroupInvitation(
            @RequestParam String senderEmail,
            @RequestParam String receiverEmail,
            @RequestParam Long groupId) {
        Optional<Usuario> senderOptional = usuarioService.getByEmail(senderEmail);
        Optional<Usuario> receiverOptional = usuarioService.getByEmail(receiverEmail);

        if (senderOptional.isPresent() && receiverOptional.isPresent()) {
            Usuario sender = senderOptional.get();
            Usuario receiver = receiverOptional.get();

            boolean success = invitacionService.enviarInvitacion(sender.getId(), receiver.getId(), groupId);

            if (success) {
                return new ResponseEntity<>("Solicitud de grupo enviada con éxito.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No se pudo enviar la solicitud de grupo.", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Usuarios no encontrados.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/acceptGroupInvitation")
    public ResponseEntity<String> acceptGroupInvitation(
            @RequestParam String userEmail,
            @RequestParam Long invitationId) {
        Optional<Usuario> userOptional = usuarioService.getByEmail(userEmail);

        if (userOptional.isPresent()) {
            Usuario usuario = userOptional.get();
            invitacionService.aceptarInvitacion(usuario, invitationId);
            return new ResponseEntity<>("Invitación de grupo aceptada con éxito.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Usuario no encontrado.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/rejectGroupInvitation")
    public ResponseEntity<String> rejectGroupInvitation(
            @RequestParam String userEmail,
            @RequestParam Long invitationId) {
        Optional<Usuario> userOptional = usuarioService.getByEmail(userEmail);

        if (userOptional.isPresent()) {
            Usuario usuario = userOptional.get();
            invitacionService.rechazarInvitacion(usuario, invitationId);
            return new ResponseEntity<>("Invitación de grupo rechazada con éxito.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Usuario no encontrado.", HttpStatus.NOT_FOUND);
        }
    }

}
