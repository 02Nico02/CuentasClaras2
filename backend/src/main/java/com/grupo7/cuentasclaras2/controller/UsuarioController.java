package com.grupo7.cuentasclaras2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.grupo7.cuentasclaras2.DTO.Credentials;
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

    /**
     * Obtiene información detallada sobre un usuario mediante su identificador.
     *
     * @param id Identificador único del usuario.
     * @return ResponseEntity con el UsuarioDTO y HttpStatus correspondiente.
     */
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
    /**
     * Obtiene información detallada sobre un usuario mediante su nombre de usuario.
     *
     * @param username Nombre de usuario del usuario.
     * @return ResponseEntity con el UsuarioDTO y HttpStatus correspondiente.
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UsuarioDTO> getUserByUsername(@PathVariable String username) {
        Optional<Usuario> user = usuarioService.getByUsername(username);
        return user.map(value -> new ResponseEntity<>(new UsuarioDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // No creo que se use. eliminar
    /**
     * Obtiene información detallada sobre un usuario mediante su dirección de
     * correo electrónico.
     *
     * @param email Dirección de correo electrónico del usuario.
     * @return ResponseEntity con el UsuarioDTO y HttpStatus correspondiente.
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> getUserByEmail(@PathVariable String email) {
        Optional<Usuario> user = usuarioService.getByEmail(email);
        return user.map(value -> new ResponseEntity<>(new UsuarioDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param newUser Datos del nuevo usuario.
     * @return ResponseEntity con las credenciales (token, duración y nombre de
     *         usuario) y HttpStatus correspondiente.
     */
    @PostMapping("/register")
    public ResponseEntity<Credentials> registerUser(@RequestBody Usuario newUser) {
        Optional<Usuario> registeredUser = usuarioService.registerUser(newUser);

        if (registeredUser.isPresent()) {
            Usuario user = registeredUser.get();
            String token = tokenServices.generateToken(user.getUsername(), EXPIRATION_IN_SEC);
            return ResponseEntity.ok().body(new Credentials(token, EXPIRATION_IN_SEC, user.getUsername()));
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Actualiza la información de un usuario existente.
     *
     * @param userId     Identificador único del usuario a actualizar.
     * @param usuarioDTO Datos actualizados del usuario.
     * @return ResponseEntity con el UsuarioDTO actualizado y HttpStatus
     *         correspondiente.
     */
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

    /**
     * Autentica a un usuario utilizando las credenciales proporcionadas (nombre de
     * usuario y contraseña).
     *
     * @param credentials Credenciales del usuario (nombre de usuario y contraseña).
     * @return ResponseEntity con las credenciales (token, duración y nombre de
     *         usuario) y HttpStatus correspondiente.
     */
    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody UsernameAndPassword credentials) {
        Optional<Usuario> user = usuarioService.login(credentials.getUserName(), credentials.getPassword());
        if (user.isPresent()) {
            String token = tokenServices.generateToken(user.get().getUsername(), EXPIRATION_IN_SEC);
            return ResponseEntity.ok().body(new Credentials(token, EXPIRATION_IN_SEC, user.get().getUsername()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrecta");
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     *
     * @return ResponseEntity con un mensaje indicando que la sesión se ha cerrado y
     *         HttpStatus correspondiente.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body("Sesión cerrada");

    }

    /**
     * Envía una solicitud de amistad desde el usuario actual al usuario
     * destinatario.
     *
     * @param receiverEmail Correo electrónico del usuario destinatario.
     * @return ResponseEntity con un mensaje de estado y HttpStatus correspondiente.
     */
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

    /**
     * Obtiene las solicitudes de amistad recibidas por el usuario autenticado.
     *
     * @return ResponseEntity con la lista de solicitudes de amistad en formato DTO
     *         y HttpStatus correspondiente.
     */
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

    /**
     * Acepta una solicitud de amistad pendiente.
     *
     * @param invitationId Identificador de la invitación de amistad a aceptar.
     * @return ResponseEntity con un mensaje de estado y HttpStatus correspondiente.
     */
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

    /**
     * Rechaza una solicitud de amistad pendiente.
     *
     * @param invitationId Identificador de la invitación de amistad a rechazar.
     * @return ResponseEntity con un mensaje de estado y HttpStatus correspondiente.
     */
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

    /**
     * Envia una solicitud de grupo a otro usuario.
     *
     * @param receiverId Identificador del usuario destinatario.
     * @param groupId    Identificador del grupo al que se invita.
     * @return ResponseEntity con un mensaje de estado y HttpStatus correspondiente.
     */
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

    /**
     * Acepta una invitación de grupo pendiente.
     *
     * @param invitationId Identificador de la invitación de grupo a aceptar.
     * @return ResponseEntity con un mensaje de estado y HttpStatus correspondiente.
     */
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

    /**
     * Rechaza una invitación de grupo pendiente.
     *
     * @param invitationId Identificador de la invitación de grupo a rechazar.
     * @return ResponseEntity con un mensaje de estado y HttpStatus correspondiente.
     */
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

    /**
     * Obtiene las invitaciones de grupo pendientes para el usuario actual.
     *
     * @return ResponseEntity con la lista de InvitacionGrupoDTO y HttpStatus
     *         correspondiente.
     */
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

    /**
     * Obtiene los pagos realizados por el usuario actual.
     *
     * @return ResponseEntity con la lista de PagoDTO y HttpStatus correspondiente.
     */
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

    /**
     * Obtiene los pagos recibidos por el usuario actual.
     *
     * @return ResponseEntity con la lista de PagoDTO y HttpStatus correspondiente.
     */
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

    /**
     * Obtiene los grupos del usuario actual.
     *
     * @return ResponseEntity con la lista de GrupoDTO y HttpStatus correspondiente.
     */
    @GetMapping("/my-groups")
    public ResponseEntity<List<GrupoDTO>> getGroupsByUser() {
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

    /**
     * Obtiene los grupos pareja del usuario actual.
     *
     * @return ResponseEntity con la lista de GrupoDTO y HttpStatus correspondiente.
     */
    @GetMapping("/my-couple-groups")
    public ResponseEntity<List<GrupoDTO>> getCoupleGroupsByUser() {
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

    /**
     * Obtiene las deudas pendientes del usuario actual como deudor.
     *
     * @return ResponseEntity con la lista de DeudaUsuarioDTO y HttpStatus
     *         correspondiente.
     */
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

    /**
     * Obtiene las deudas pendientes del usuario actual como acreedor.
     *
     * @return ResponseEntity con la lista de DeudaUsuarioDTO y HttpStatus
     *         correspondiente.
     */
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
