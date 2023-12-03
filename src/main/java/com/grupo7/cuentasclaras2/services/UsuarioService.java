package com.grupo7.cuentasclaras2.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo7.cuentasclaras2.DTO.UsuarioDTO;
import com.grupo7.cuentasclaras2.exception.UserException;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<Usuario> getById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> getByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Optional<Usuario> getByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> getByFriendId(Long friendId) {
        return usuarioRepository.findByAmigos_Id(friendId);
    }

    public List<Usuario> searchByNameOrLastName(String name, String lastName) {
        return usuarioRepository.findByNombresIgnoreCaseContainingOrApellidoIgnoreCaseContaining(name, lastName);
    }

    public List<Usuario> getByInvitationGroupId(Long invitationId) {
        return usuarioRepository.findByInvitacionesGrupo_Destinatario_Id(invitationId);
    }

    public List<Usuario> getByFriendInvitationId(Long friendInvitationId) {
        return usuarioRepository.findByInvitacionesAmigosRecibidas_Id(friendInvitationId);
    }

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

    public Optional<Usuario> registerUser(Usuario newUser) {
        return usuarioRepository.findByUsername(newUser.getUsername())
                .or(() -> usuarioRepository.findByEmail(newUser.getEmail()))
                .map(existingUser -> {
                    return Optional.<Usuario>empty();
                })
                .orElseGet(() -> {
                    newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
                    return Optional.of(usuarioRepository.save(newUser));
                });
    }

    public Optional<Usuario> findByUsernameOrEmail(String usernameOrEmail) {
        return usuarioRepository.findByUsername(usernameOrEmail)
                .or(() -> usuarioRepository.findByEmail(usernameOrEmail));
    }

    public Usuario updateUser(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

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

}
