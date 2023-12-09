package com.grupo7.cuentasclaras2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.grupo7.cuentasclaras2.DTO.InvitacionAmistadDTO;
import com.grupo7.cuentasclaras2.DTO.InvitacionGrupoDTO;
import com.grupo7.cuentasclaras2.DTO.UsernameAndPassword;
import com.grupo7.cuentasclaras2.DTO.UsuarioDTO;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.services.InvitacionAmistadService;
import com.grupo7.cuentasclaras2.services.InvitacionService;
import com.grupo7.cuentasclaras2.services.TokenServices;
import com.grupo7.cuentasclaras2.services.UsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private InvitacionAmistadService invitacionAmistadService;

    @Autowired
    private InvitacionService invitacionService;

    @Autowired
    private TokenServices tokenServices;

    private final int EXPIRATION_IN_SEC = 7200;

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
        Optional<Usuario> usuarioExistenteOptional = usuarioService.getById(userId);
        if (usuarioExistenteOptional.isPresent()) {
            Usuario usuarioExistente = usuarioExistenteOptional.get();
            Usuario usuarioGuardado = usuarioService.updateUserDataFromDTO(usuarioExistente, usuarioDTO);
            return new ResponseEntity<>(new UsuarioDTO(usuarioGuardado), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody UsernameAndPassword credentials) {
        Optional<Usuario> user = usuarioService.login(credentials.getUserName(), credentials.getPassword());
        if (user.isPresent()) {
            String token = tokenServices.generateToken(user.get().getUsername(), EXPIRATION_IN_SEC);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            return ResponseEntity.ok().headers(headers)
                    .body(new UsuarioDTO(user.get()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrecta");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body("Sesión cerrada");

    }

    @PostMapping("/sendFriendRequest")
    public ResponseEntity<String> sendFriendRequest(
            @RequestParam String receiverEmail,
            @RequestAttribute("username") String userName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> senderOptional = usuarioService.getByUsername((String) principal);

        if (senderOptional.isPresent()) {
            Usuario sender = senderOptional.get();
            Optional<Usuario> receiverOptional = usuarioService.getByEmail(receiverEmail);

            if (receiverOptional.isPresent()) {
                Usuario receiver = receiverOptional.get();
                invitacionAmistadService.sendFriendRequest(sender, receiver);
                return new ResponseEntity<>("Solicitud de amistad enviada con éxito.",
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Usuario destinatario no encontrado.",
                        HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("Usuario remitente no encontrado.",
                    HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/friendRequests")
    public ResponseEntity<List<InvitacionAmistadDTO>> getFriendRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> user = usuarioService.getByUsername((String) principal);

        if (user.isPresent()) {
            Usuario usuario = user.get();

            List<InvitacionAmistadDTO> friendRequestsDTO = usuario.getInvitacionesAmigosRecibidas()
                    .stream()
                    .map(InvitacionAmistadDTO::new)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(friendRequestsDTO, HttpStatus.OK);
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
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam Long groupId) {

        invitacionService.enviarInvitacion(senderId, receiverId, groupId);
        return new ResponseEntity<>("Solicitud de grupo enviada con éxito.", HttpStatus.OK);

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

    @GetMapping("/groupInvitations/{userId}")
    public ResponseEntity<List<InvitacionGrupoDTO>> getGroupInvitations(@PathVariable Long userId) {
        Optional<Usuario> user = usuarioService.getById(userId);

        if (user.isPresent()) {
            Usuario usuario = user.get();

            List<InvitacionGrupoDTO> groupInvitationsDTO = usuario.getInvitacionesGrupo()
                    .stream()
                    .map(InvitacionGrupoDTO::new)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(groupInvitationsDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
