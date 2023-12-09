package com.grupo7.cuentasclaras2.serviceTests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.grupo7.cuentasclaras2.DTO.CategoriaDTO;
import com.grupo7.cuentasclaras2.DTO.GrupoDTO;
import com.grupo7.cuentasclaras2.DTO.IdEmailUsuarioDTO;
import com.grupo7.cuentasclaras2.exception.GroupException;
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

        Usuario usuario1 = new Usuario("usuario1", "Nombre", "Apellido", "usuario1@example.com", "password");
        Usuario usuario2 = new Usuario("usuario2", "Nombre", "Apellido", "usuario2@example.com", "password");
        Usuario usuario3 = new Usuario("usuario3", "Nombre", "Apellido", "usuario3@example.com", "password");
        usuarioRepository.saveAll(List.of(usuario1, usuario2, usuario3));

        // ahora deberia hascer grupo.setMiembros y enviarla una lista con los usuarios
        grupo.setMiembros(Arrays.asList(usuario1, usuario2));
        grupoRepository.save(grupo);

        GroupException thrownException = assertThrows(GroupException.class, () -> {
            grupoService.addMemberToGroup(grupo.getId(), usuario3.getId());
        });

        assertEquals("No se puede agregar miembros a una pareja", thrownException.getMessage());
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

    @Test
    void testNewGroupByDTO() {
        GrupoDTO grupoDTO = new GrupoDTO();
        grupoDTO.setNombre("Nuevo Grupo");

        Categoria categoria = new Categoria();
        categoria.setEsGrupo(true);
        categoria.setIcono("icono_categoria.ico");
        categoria.setNombre("Familia");
        categoriaRepository.save(categoria);

        grupoDTO.setCategoria(new CategoriaDTO(categoria));

        Usuario usuario1 = new Usuario("usuario1", "Nombre", "Apellido", "usuario1@example.com", "password");
        Usuario usuario2 = new Usuario("usuario2", "Nombre", "Apellido", "usuario2@example.com", "password");
        List<Usuario> usuarios = usuarioRepository.saveAll(Arrays.asList(usuario1, usuario2));

        IdEmailUsuarioDTO idEmailUsuarioDTO1 = new IdEmailUsuarioDTO();
        idEmailUsuarioDTO1.setUsername(usuarios.get(0).getUsername());
        idEmailUsuarioDTO1.setId(usuarios.get(0).getId());

        List<IdEmailUsuarioDTO> miembrosDTO = Arrays.asList(idEmailUsuarioDTO1);
        grupoDTO.setMiembros(miembrosDTO);

        Optional<Grupo> grupoGuardado = grupoService.newGroupByDTO(grupoDTO);

        assertTrue(grupoGuardado.isPresent());
    }

    @Test
    void testNewCoupleGroupByDTO() {
        GrupoDTO coupleGroupDTO = new GrupoDTO();

        Usuario usuario1 = new Usuario("usuario1", "Nombre", "Apellido", "usuario1@example.com", "password");
        Usuario usuario2 = new Usuario("usuario2", "Nombre", "Apellido", "usuario2@example.com", "password");
        List<Usuario> usuarios = usuarioRepository.saveAll(Arrays.asList(usuario1, usuario2));

        IdEmailUsuarioDTO idEmailUsuarioDTO1 = new IdEmailUsuarioDTO();
        idEmailUsuarioDTO1.setUsername(usuarios.get(0).getUsername());
        idEmailUsuarioDTO1.setId(usuarios.get(0).getId());
        IdEmailUsuarioDTO idEmailUsuarioDTO2 = new IdEmailUsuarioDTO();
        idEmailUsuarioDTO2.setUsername(usuarios.get(1).getUsername());
        idEmailUsuarioDTO2.setId(usuarios.get(1).getId());

        List<IdEmailUsuarioDTO> miembrosDTO = Arrays.asList(idEmailUsuarioDTO1, idEmailUsuarioDTO2);
        coupleGroupDTO.setMiembros(miembrosDTO);

        Optional<Grupo> coupleGroupGuardado = grupoService.newCoupleGroupByDTO(coupleGroupDTO);

        assertTrue(coupleGroupGuardado.isPresent());
        assertTrue(coupleGroupGuardado.get().getEsPareja());
    }

    @Test
    void testNewCoupleGroupByDTOWithInsufficientMembers() {
        GrupoDTO coupleGroupDTO = new GrupoDTO();

        Usuario usuario1 = new Usuario("usuario1", "Nombre", "Apellido", "usuario1@example.com", "password");
        usuarioRepository.save(usuario1);

        IdEmailUsuarioDTO idEmailUsuarioDTO1 = new IdEmailUsuarioDTO();
        idEmailUsuarioDTO1.setUsername(usuario1.getUsername());
        idEmailUsuarioDTO1.setId(usuario1.getId());

        List<IdEmailUsuarioDTO> miembrosDTO = Collections.singletonList(idEmailUsuarioDTO1);
        coupleGroupDTO.setMiembros(miembrosDTO);

        assertThrows(GroupException.class, () -> grupoService.newCoupleGroupByDTO(coupleGroupDTO),
                "Se esperaba una excepción GroupException");
    }

}
