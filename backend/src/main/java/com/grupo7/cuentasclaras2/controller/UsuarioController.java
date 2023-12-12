package com.grupo7.cuentasclaras2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.grupo7.cuentasclaras2.DTO.DeudaUsuarioDTO;
import com.grupo7.cuentasclaras2.DTO.GrupoDTO;
import com.grupo7.cuentasclaras2.DTO.InvitacionAmistadDTO;
import com.grupo7.cuentasclaras2.DTO.InvitacionGrupoDTO;
import com.grupo7.cuentasclaras2.DTO.PagoDTO;
import com.grupo7.cuentasclaras2.DTO.UsernameAndPassword;
import com.grupo7.cuentasclaras2.DTO.UsuarioDTO;
import com.grupo7.cuentasclaras2.exception.UnauthorizedException;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.services.DeudaUsuarioService;
import com.grupo7.cuentasclaras2.services.GrupoService;
import com.grupo7.cuentasclaras2.services.InvitacionAmistadService;
import com.grupo7.cuentasclaras2.services.InvitacionService;
import com.grupo7.cuentasclaras2.services.PagoService;
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

    @Autowired
    private PagoService pagoService;

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private DeudaUsuarioService deudaUsuarioService;

    private final int EXPIRATION_IN_SEC = 7200;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Optional<Usuario> user = usuarioService.getById(id);

        if (user.isPresent()) {
            Usuario usuario = user.get();
            if (usuario.getUsername().equals(principal))
                return new ResponseEntity<>(new UsuarioDTO(usuario), HttpStatus.OK);
            throw new UnauthorizedException("No autorizado para ver este usuario");
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // No creo que se use. eliminar
    @GetMapping("/username/{username}")
    public ResponseEntity<UsuarioDTO> getUserByUsername(@PathVariable String username) {
        Optional<Usuario> user = usuarioService.getByUsername(username);
        return user.map(value -> new ResponseEntity<>(new UsuarioDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // No creo que se use. eliminar
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> getUserByEmail(@PathVariable String email) {
        Optional<Usuario> user = usuarioService.getByEmail(email);
        return user.map(value -> new ResponseEntity<>(new UsuarioDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioDTO> registerUser(@RequestBody Usuario newUser) {
        Optional<Usuario> registeredUser = usuarioService.registerUser(newUser);

        if (registeredUser.isPresent()) {
            Usuario user = registeredUser.get();
            String token = tokenServices.generateToken(user.getUsername(), EXPIRATION_IN_SEC);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            return ResponseEntity.ok().headers(headers)
                    .body(new UsuarioDTO(user));
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable long userId, @RequestBody UsuarioDTO usuarioDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> usuarioExistenteOptional = usuarioService.getById(userId);
        if (usuarioExistenteOptional.isPresent()) {
            Usuario usuarioExistente = usuarioExistenteOptional.get();
            if (usuarioExistente.getUsername().equals(principal)) {
                Usuario usuarioGuardado = usuarioService.updateUserDataFromDTO(usuarioExistente, usuarioDTO);
                return new ResponseEntity<>(new UsuarioDTO(usuarioGuardado), HttpStatus.OK);
            } else {
                throw new UnauthorizedException("No autorizado para editar este usuario");
            }
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
            @RequestParam String receiverEmail) {
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
            @RequestParam Long invitationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> userOptional = usuarioService.getByUsername((String) principal);

        if (userOptional.isPresent()) {
            Usuario usuario = userOptional.get();
            boolean operacionExitosa = invitacionAmistadService.aceptarSolicitudAmistad(usuario, invitationId);
            if (operacionExitosa) {
                return new ResponseEntity<>("Solicitud de amistad aceptada con éxito.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("La operación no pudo ser completada.", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Usuario no encontrado.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/rejectFriendRequest")
    public ResponseEntity<String> rejectFriendRequest(
            @RequestParam Long invitationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> userOptional = usuarioService.getByUsername((String) principal);

        if (userOptional.isPresent()) {
            Usuario usuario = userOptional.get();
            boolean operacionExitosa = invitacionAmistadService.rechazarSolicitudAmistad(usuario, invitationId);
            if (operacionExitosa) {
                return new ResponseEntity<>("Solicitud de amistad rechazada con éxito.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("La operación no pudo ser completada.", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Usuario no encontrado.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/sendGroupInvitation")
    public ResponseEntity<String> sendGroupInvitation(
            @RequestParam Long receiverId,
            @RequestParam Long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> userOptional = usuarioService.getByUsername((String) principal);

        if (userOptional.isPresent()) {
            Usuario usuario = userOptional.get();

            invitacionService.enviarInvitacion(usuario, receiverId, groupId);
            return new ResponseEntity<>("Solicitud de grupo enviada con éxito.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Usuario no encontrado.", HttpStatus.NOT_FOUND);

    }

    @PostMapping("/acceptGroupInvitation")
    public ResponseEntity<String> acceptGroupInvitation(
            @RequestParam Long invitationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> userOptional = usuarioService.getByUsername((String) principal);

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
            @RequestParam Long invitationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> userOptional = usuarioService.getByUsername((String) principal);

        if (userOptional.isPresent()) {
            Usuario usuario = userOptional.get();
            invitacionService.rechazarInvitacion(usuario, invitationId);
            return new ResponseEntity<>("Invitación de grupo rechazada con éxito.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Usuario no encontrado.", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/groupInvitations")
    public ResponseEntity<List<InvitacionGrupoDTO>> getGroupInvitations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> user = usuarioService.getByUsername((String) principal);

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

    @GetMapping("/myPayments")
    public ResponseEntity<List<PagoDTO>> getMyPayments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

        if (!usuarioOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<PagoDTO> myPayments = pagoService.obtenerPagosRealizadosPorUsuario(usuarioOptional.get().getId())
                .stream()
                .map(PagoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(myPayments);
    }

    @GetMapping("/receivedPayments")
    public ResponseEntity<List<PagoDTO>> getReceivedPayments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

        if (!usuarioOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<PagoDTO> receivedPayments = pagoService.obtenerPagosRecibidosPorUsuario(usuarioOptional.get().getId())
                .stream()
                .map(PagoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(receivedPayments);
    }

    @GetMapping("/my-groups")
    public ResponseEntity<List<GrupoDTO>> getGroupsByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

        if (!usuarioOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Grupo> groups = grupoService.getGroupsByUserId(usuarioOptional.get().getId());
        List<GrupoDTO> groupDTOs = groups.stream()
                .map(GrupoDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(groupDTOs);
    }

    @GetMapping("/my-couple-groups")
    public ResponseEntity<List<GrupoDTO>> getCoupleGroupsByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

        if (!usuarioOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Grupo> groups = grupoService.getGroupsWhereEsPareja(usuarioOptional.get().getId());
        List<GrupoDTO> groupDTOs = groups.stream()
                .map(GrupoDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(groupDTOs);
    }

    @GetMapping("/my-debts")
    public ResponseEntity<List<DeudaUsuarioDTO>> getMyDebts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

        if (!usuarioOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<DeudaUsuarioDTO> myDebts = deudaUsuarioService.obtenerDeudasDeDeudorID(usuarioOptional.get().getId())
                .stream()
                .map(DeudaUsuarioDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(myDebts, HttpStatus.OK);
    }

    @GetMapping("/debts-owed-to-me")
    public ResponseEntity<List<DeudaUsuarioDTO>> getDebtsOwedToMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Optional<Usuario> usuarioOptional = usuarioService.getByUsername((String) principal);

        if (!usuarioOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<DeudaUsuarioDTO> debtsOwedToMe = deudaUsuarioService
                .obtenerDeudasDeAcreedorID(usuarioOptional.get().getId())
                .stream()
                .map(DeudaUsuarioDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(debtsOwedToMe, HttpStatus.OK);
    }

}
