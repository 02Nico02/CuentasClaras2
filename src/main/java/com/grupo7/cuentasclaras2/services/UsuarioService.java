package com.grupo7.cuentasclaras2.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (usuarioRepository.findByUsername(newUser.getUsername()).isPresent() ||
                usuarioRepository.findByEmail(newUser.getEmail()).isPresent()) {
            return Optional.empty();
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return Optional.of(usuarioRepository.save(newUser));
    }

    public Optional<Usuario> findByUsernameOrEmail(String usernameOrEmail) {
        return usuarioRepository.findByUsername(usernameOrEmail)
                .or(() -> usuarioRepository.findByEmail(usernameOrEmail));
    }

}
