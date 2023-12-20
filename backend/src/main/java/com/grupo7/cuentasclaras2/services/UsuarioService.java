package com.grupo7.cuentasclaras2.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo7.cuentasclaras2.DTO.UsuarioDTO;
import com.grupo7.cuentasclaras2.exception.UserAlreadyExistsException;
import com.grupo7.cuentasclaras2.exception.UserException;
import com.grupo7.cuentasclaras2.modelos.DeudaUsuario;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Obtener un usuario por su ID.
     * 
     * @param id El ID del usuario.
     * @return Un Optional que contiene al usuario, o vacío si no se encuentra.
     */
    public Optional<Usuario> getById(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Obtener un usuario por su nombre de usuario.
     * 
     * @param username El userName del usuario.
     * @return Un Optional que contiene al usuario, o vacío si no se encuentra.
     */
    public Optional<Usuario> getByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Obtener un usuario por su correo electrónico.
     * 
     * @param email El correo electrónico del usuario.
     * @return Un Optional que contiene al usuario, o vacío si no se encuentra.
     */
    public Optional<Usuario> getByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Obtener una lista de usuarios por el ID de un amigo.
     * 
     * @param friendId El ID del amigo.
     * @return Una lista de usuarios que son amigos del usuario con ID especificado.
     */
    public List<Usuario> getByFriendId(Long friendId) {
        return usuarioRepository.findByAmigos_Id(friendId);
    }

    /**
     * Buscar usuarios por su nombre o apellido.
     * 
     * @param name     El nombre a buscar.
     * @param lastName El apellido a buscar.
     * @return Una lista de usuarios que coinciden con los criterios de búsqueda.
     */
    public List<Usuario> searchByNameOrLastName(String name, String lastName) {
        return usuarioRepository.findByNombresIgnoreCaseContainingOrApellidoIgnoreCaseContaining(name, lastName);
    }

    /**
     * Iniciar sesión de un usuario con el nombre de usuario o correo electrónico y
     * la contraseña proporcionados.
     * 
     * @param usernameOrEmail El nombre de usuario o correo electrónico del usuario.
     * @param password        La contraseña del usuario.
     * @return Un Optional que contiene al usuario si el inicio de sesión es
     *         exitoso, o vacío de lo contrario.
     */
    public Optional<Usuario> login(String usernameOrEmail, String password) {
        Optional<Usuario> optionalUser = findByUsernameOrEmail(usernameOrEmail);

        if (optionalUser.isPresent()) {
            Usuario user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    /**
     * Registrar un nuevo usuario en el sistema.
     * 
     * @param newUser El nuevo usuario a registrar.
     * @return Un Optional que contiene al nuevo usuario registrado si no existe un
     *         usuario con el mismo nombre de usuario o correo electrónico, o vacío
     *         de lo contrario.
     */
    public Usuario registerUser(Usuario newUser) {
        if (usuarioRepository.findByUsername(newUser.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("El nombre de usuario ya existe.", "username");
        }

        if (usuarioRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("El correo electrónico ya está en uso.", "email");
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return usuarioRepository.save(newUser);
    }

    /**
     * Buscar un usuario por su nombre de usuario o correo electrónico.
     * 
     * @param usernameOrEmail El nombre de usuario o correo electrónico del usuario
     *                        a buscar.
     * @return Un Optional que contiene al usuario si se encuentra, o vacío de lo
     *         contrario.
     */
    public Optional<Usuario> findByUsernameOrEmail(String usernameOrEmail) {
        return usuarioRepository.findByUsername(usernameOrEmail)
                .or(() -> usuarioRepository.findByEmail(usernameOrEmail));
    }

    /**
     * Actualizar la información de un usuario.
     * 
     * @param usuario El usuario con la información actualizada.
     * @return El usuario actualizado.
     */
    public Usuario updateUser(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Actualizar la información de un usuario a partir de un objeto DTO.
     * 
     * @param usuarioExistente El usuario existente que se actualizará.
     * @param usuarioDTO       El DTO con la nueva información del usuario.
     * @return El usuario actualizado.
     * @throws UserException Si el nuevo nombre de usuario o correo electrónico ya
     *                       está en uso.
     */
    public Usuario updateUserDataFromDTO(Usuario usuarioExistente, UsuarioDTO usuarioDTO) {
        String nuevoUsername = usuarioDTO.getUsername();
        if (!nuevoUsername.equals(usuarioExistente.getUsername())
                && usuarioRepository.existsByUsername(nuevoUsername)) {
            throw new UserException("El nombre de usuario ya está en uso.");
        }

        String nuevoEmail = usuarioDTO.getEmail();
        if (!nuevoEmail.equals(usuarioExistente.getEmail()) && usuarioRepository.existsByEmail(nuevoEmail)) {
            throw new UserException("La dirección de correo electrónico ya está en uso.");
        }

        usuarioExistente.setUsername(nuevoUsername);
        usuarioExistente.setNombres(usuarioDTO.getNombres());
        usuarioExistente.setApellido(usuarioDTO.getApellido());
        usuarioExistente.setEmail(nuevoEmail);

        return usuarioRepository.save(usuarioExistente);
    }

    public double calcularSaldoDisponibleEnGrupo(Usuario usuario, Grupo grupo) {
        List<DeudaUsuario> deudas = grupo.getDeudas();

        double saldoDisponible = 0.0;

        for (DeudaUsuario deuda : deudas) {
            if (deuda.getDeudor().equals(usuario)) {
                saldoDisponible -= deuda.getMonto();
            } else if (deuda.getAcreedor().equals(usuario)) {
                saldoDisponible += deuda.getMonto();
            }
        }

        return saldoDisponible;
    }

}
