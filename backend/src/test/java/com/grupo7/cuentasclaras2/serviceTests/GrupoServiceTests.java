package com.grupo7.cuentasclaras2.serviceTests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.grupo7.cuentasclaras2.DTO.CategoriaDTO;
import com.grupo7.cuentasclaras2.DTO.GrupoDTO;
import com.grupo7.cuentasclaras2.DTO.MiembrosGrupoDTO;
import com.grupo7.cuentasclaras2.exception.GroupException;
import com.grupo7.cuentasclaras2.modelos.Categoria;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.CategoriaRepository;
import com.grupo7.cuentasclaras2.repositories.GrupoRepository;
import com.grupo7.cuentasclaras2.repositories.UsuarioRepository;
import com.grupo7.cuentasclaras2.services.GrupoService;
import com.grupo7.cuentasclaras2.services.UsuarioService;

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

    @Autowired
    private UsuarioService usuarioService;

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

        MiembrosGrupoDTO miembroGrupoDTO1 = new MiembrosGrupoDTO();
        miembroGrupoDTO1.setUserName(usuarios.get(0).getUsername());
        miembroGrupoDTO1.setIdUsuario(usuarios.get(0).getId());

        List<MiembrosGrupoDTO> miembrosDTO = Arrays.asList(miembroGrupoDTO1);
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

        MiembrosGrupoDTO miembrosGrupoDTO1 = new MiembrosGrupoDTO();
        miembrosGrupoDTO1.setUserName(usuarios.get(0).getUsername());
        miembrosGrupoDTO1.setIdUsuario(usuarios.get(0).getId());
        MiembrosGrupoDTO miembrosGrupoDTO2 = new MiembrosGrupoDTO();
        miembrosGrupoDTO2.setUserName(usuarios.get(1).getUsername());
        miembrosGrupoDTO2.setIdUsuario(usuarios.get(1).getId());

        List<MiembrosGrupoDTO> miembrosDTO = Arrays.asList(miembrosGrupoDTO1, miembrosGrupoDTO2);
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

        MiembrosGrupoDTO miembrosGrupoDTO1 = new MiembrosGrupoDTO();
        miembrosGrupoDTO1.setUserName(usuario1.getUsername());
        miembrosGrupoDTO1.setIdUsuario(usuario1.getId());

        List<MiembrosGrupoDTO> miembrosDTO = Collections.singletonList(miembrosGrupoDTO1);
        coupleGroupDTO.setMiembros(miembrosDTO);

        assertThrows(GroupException.class, () -> grupoService.newCoupleGroupByDTO(coupleGroupDTO),
                "Se esperaba una excepción GroupException");
    }

    @Test
    public void testFindUsersNotInGroupAndNotFriends() {
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

        // Crear y guardar un usuario
        Usuario usuario1 = new Usuario("usuario1", "Nombre", "Apellido", "usuario1@example.com", "password");
        usuario1 = usuarioRepository.save(usuario1);

        // Agregar el usuario al grupo
        grupoService.addMemberToGroup(grupo.getId(), usuario1.getId());
        grupoRepository.save(grupo);

        String usernameQuery = "io";

        // Lista de usuarios esperados
        List<Usuario> usuariosEsperados = new ArrayList<>();
        Usuario usuario2 = new Usuario("usuario2", "Nombre", "Apellido", "usuario2@example.com", "password");
        usuariosEsperados.add(usuario2);
        usuario2 = usuarioRepository.save(usuario2);

        Usuario usuario3 = new Usuario("usuario3", "Nombre", "Apellido", "usuario3@example.com", "password");
        usuario3.agregarAmigo(usuario1);
        usuario3 = usuarioRepository.save(usuario3);
        usuario1 = usuarioRepository.save(usuario1);

        // Ejecutar el método a probar
        List<Usuario> resultado = usuarioService.findUsersNotInGroupAndNotFriends(usuario1, grupo.getId(),
                usernameQuery);

        // Verificar el resultado
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("usuario2", resultado.get(0).getUsername());

        List<Usuario> resultado2 = usuarioService.findFriendsNotInGroupByQuery(usuario1, grupo.getId(),
                usernameQuery);

        // Verificar el resultado
        assertNotNull(resultado2);
        assertEquals(1, resultado2.size());
        assertEquals("usuario3", resultado2.get(0).getUsername());
    }
}
