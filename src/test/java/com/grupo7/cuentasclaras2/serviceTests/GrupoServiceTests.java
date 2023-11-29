package com.grupo7.cuentasclaras2.serviceTests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.grupo7.cuentasclaras2.modelos.Categoria;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.CategoriaRepository;
import com.grupo7.cuentasclaras2.repositories.GrupoRepository;
import com.grupo7.cuentasclaras2.repositories.UsuarioRepository;
import com.grupo7.cuentasclaras2.services.GrupoService;

@DataJpaTest
public class GrupoServiceTests {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void testAddMemberToGroup() {
        Grupo grupo = new Grupo();
        grupo.setNombre("Grupo de Prueba");
        grupo.setEsPareja(false);
        Categoria categoria = new Categoria();
        categoria.setEsGrupo(true);
        categoria.setNombre("Familiar");
        categoria.setIcono("algo");
        categoriaRepository.save(categoria);
        grupo.setCategoria(categoria);
        grupoRepository.save(grupo);

        Usuario usuario = new Usuario("usuario1", "Nombre", "Apellido", "usuario1@example.com", "password");
        usuarioRepository.save(usuario);

        boolean success = grupoService.addMemberToGroup(grupo.getId(), usuario.getId());

        assertTrue(success);

        Grupo grupoActualizado = grupoRepository.findById(grupo.getId()).orElse(null);

        assertNotNull(grupoActualizado);
        assertTrue(grupoActualizado.getMiembros().contains(usuario));
    }

    @Test
    void testAddMemberToGroupWithInvalidGroupId() {
        // Intentar agregar un miembro a un grupo con un ID no válido
        boolean success = grupoService.addMemberToGroup(-1L, 1L);

        assertFalse(success);
    }

    @Test
    void testAddMemberToGroupWithInvalidUserId() {
        // Intentar agregar un miembro a un grupo con un ID de usuario no válido
        Grupo grupo = new Grupo();
        grupo.setNombre("Grupo de Prueba");
        grupoRepository.save(grupo);

        boolean success = grupoService.addMemberToGroup(grupo.getId(), -1L);

        assertFalse(success);
    }

    @Test
    void testAddMemberToNonexistentGroup() {
        // Intentar agregar un miembro a un grupo que no existe
        Usuario usuario = new Usuario("usuario1", "Nombre", "Apellido", "usuario1@example.com", "password");
        usuarioRepository.save(usuario);

        boolean success = grupoService.addMemberToGroup(-1L, usuario.getId());

        assertFalse(success);
    }

    @Test
    void testRemoveMemberFromGroup() {
        Grupo grupo = new Grupo();
        grupo.setNombre("Grupo de Prueba");
        grupo.setEsPareja(false);
        Categoria categoria = new Categoria();
        categoria.setEsGrupo(true);
        categoria.setNombre("Familiar");
        categoria.setIcono("algo");
        categoriaRepository.save(categoria);
        grupo.setCategoria(categoria);
        grupoRepository.save(grupo);

        Usuario usuario = new Usuario("usuario1", "Nombre", "Apellido", "usuario1@example.com", "password");
        usuarioRepository.save(usuario);

        grupoService.addMemberToGroup(grupo.getId(), usuario.getId());

        boolean success = grupoService.removeMemberFromGroup(grupo.getId(), usuario.getId());

        assertTrue(success);

        Grupo grupoActualizado = grupoRepository.findById(grupo.getId()).orElse(null);

        assertNotNull(grupoActualizado);
        assertFalse(grupoActualizado.getMiembros().contains(usuario));
    }

    @Test
    void testRemoveMemberFromNonexistentGroup() {
        // Intentar eliminar un miembro de un grupo que no existe
        Usuario usuario = new Usuario("usuario1", "Nombre", "Apellido", "usuario1@example.com", "password");
        usuarioRepository.save(usuario);

        boolean success = grupoService.removeMemberFromGroup(-1L, usuario.getId());

        assertFalse(success);
    }

    @Test
    void testRemoveNonexistentMemberFromGroup() {
        Grupo grupo = new Grupo();
        grupo.setNombre("Grupo de Prueba");
        grupo.setEsPareja(false);
        Categoria categoria = new Categoria();
        categoria.setEsGrupo(true);
        categoria.setNombre("Familiar");
        categoria.setIcono("algo");
        categoriaRepository.save(categoria);
        grupo.setCategoria(categoria);
        grupoRepository.save(grupo);

        boolean success = grupoService.removeMemberFromGroup(grupo.getId(), 1L);

        assertFalse(success);
    }

    @Test
    void testRemoveMemberFromEmptyGroup() {
        Grupo grupo = new Grupo();
        grupo.setNombre("Grupo de Prueba");
        grupoRepository.save(grupo);

        Usuario usuario = new Usuario("usuario1", "Nombre", "Apellido", "usuario1@example.com", "password");
        usuarioRepository.save(usuario);

        boolean success = grupoService.removeMemberFromGroup(grupo.getId(), usuario.getId());

        assertFalse(success);
    }

    @Test
    void testAddMemberToGroupAlreadyInGroup() {
        Grupo grupo = new Grupo();
        grupo.setNombre("Grupo de Prueba");
        grupoRepository.save(grupo);

        Usuario usuario = new Usuario("usuario1", "Nombre", "Apellido", "usuario1@example.com", "password");
        usuarioRepository.save(usuario);

        grupoService.addMemberToGroup(grupo.getId(), usuario.getId());

        boolean success = grupoService.addMemberToGroup(grupo.getId(), usuario.getId());

        assertFalse(success);
    }

    @Test
    void testAddThirdMemberToCoupleGroup() {
        Grupo grupo = new Grupo();
        grupo.setNombre("Grupo de Prueba");
        grupo.setEsPareja(true);
        grupoRepository.save(grupo);

        Usuario usuario1 = new Usuario("usuario1", "Nombre", "Apellido", "usuario1@example.com", "password");
        Usuario usuario2 = new Usuario("usuario2", "Nombre", "Apellido", "usuario2@example.com", "password");
        Usuario usuario3 = new Usuario("usuario3", "Nombre", "Apellido", "usuario3@example.com", "password");
        usuarioRepository.saveAll(List.of(usuario1, usuario2, usuario3));

        grupoService.addMemberToGroup(grupo.getId(), usuario1.getId());
        grupoService.addMemberToGroup(grupo.getId(), usuario2.getId());

        boolean success = grupoService.addMemberToGroup(grupo.getId(), usuario3.getId());

        assertFalse(success);
    }

    @Test
    void testRemoveNonMemberFromGroup() {
        Grupo grupo = new Grupo();
        grupo.setNombre("Grupo de Prueba");
        grupoRepository.save(grupo);

        Usuario usuario = new Usuario("usuario1", "Nombre", "Apellido", "usuario1@example.com", "password");
        usuarioRepository.save(usuario);

        boolean success = grupoService.removeMemberFromGroup(grupo.getId(), usuario.getId());

        assertFalse(success);
    }

    @Test
    void testRemoveMemberFromDissolvedGroup() {
        Grupo grupo = new Grupo();
        grupo.setNombre("Grupo de Prueba");
        grupoRepository.save(grupo);

        Usuario usuario = new Usuario("usuario1", "Nombre", "Apellido", "usuario1@example.com", "password");
        usuarioRepository.save(usuario);

        grupoService.addMemberToGroup(grupo.getId(), usuario.getId());

        grupoRepository.delete(grupo);

        boolean success = grupoService.removeMemberFromGroup(grupo.getId(), usuario.getId());

        assertFalse(success);
    }

}
