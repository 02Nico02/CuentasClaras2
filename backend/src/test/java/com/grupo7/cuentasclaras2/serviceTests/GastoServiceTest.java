package com.grupo7.cuentasclaras2.serviceTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.grupo7.cuentasclaras2.DTO.CategoriaDTO;
import com.grupo7.cuentasclaras2.DTO.DivisionIndividualDTO;
import com.grupo7.cuentasclaras2.DTO.FormaDividirDTO;
import com.grupo7.cuentasclaras2.DTO.GastoAutorDTO;
import com.grupo7.cuentasclaras2.DTO.GastoDTO;
import com.grupo7.cuentasclaras2.modelos.Categoria;
import com.grupo7.cuentasclaras2.modelos.DeudaUsuario;
import com.grupo7.cuentasclaras2.modelos.FormatosDivision;
import com.grupo7.cuentasclaras2.modelos.Gasto;
import com.grupo7.cuentasclaras2.modelos.Grupo;
import com.grupo7.cuentasclaras2.modelos.Usuario;
import com.grupo7.cuentasclaras2.repositories.CategoriaRepository;
import com.grupo7.cuentasclaras2.repositories.DeudaUsuarioRepository;
import com.grupo7.cuentasclaras2.repositories.DivisionIndividualRepository;
import com.grupo7.cuentasclaras2.repositories.FormaDividirRepository;
import com.grupo7.cuentasclaras2.repositories.GastoAutorRepository;
import com.grupo7.cuentasclaras2.repositories.GastoRepository;
import com.grupo7.cuentasclaras2.repositories.GrupoRepository;
import com.grupo7.cuentasclaras2.repositories.UsuarioRepository;
import com.grupo7.cuentasclaras2.services.GastoService;

@DataJpaTest
public class GastoServiceTest {
        @Autowired
        private GastoService gastoService;

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private CategoriaRepository categoriaRepository;

        @Autowired
        private FormaDividirRepository formaDividirRepository;

        @Autowired
        private DivisionIndividualRepository divisionIndividualRepository;

        @Autowired
        private GastoRepository gastoRepository;

        @Autowired
        private GastoAutorRepository gastoAutorRepository;

        @Autowired
        private DeudaUsuarioRepository deudaUsuarioRepository;

        @Autowired
        private GrupoRepository grupoRepository;

        @Test
        public void testCrearGastos() {
                // Crear 3 usuarios
                Usuario usuario1 = new Usuario("JuanPerez", "Juan", "Perez", "juan@gmail.com", "123456");
                Usuario usuario2 = new Usuario("MariaLopez", "Maria", "Lopez", "maria@gmail.com", "abcdef");
                Usuario usuario3 = new Usuario("PedroGomez", "Pedro", "Gomez", "pedro@gmail.com", "ghijkl");

                usuarioRepository.saveAll(List.of(usuario1, usuario2, usuario3));

                // Crear un grupo
                Grupo grupo = new Grupo();
                grupo.setNombre("Grupo de Prueba");
                grupo.setEsPareja(false);

                // Categoria de grupo
                Categoria categoria = new Categoria();
                categoria.setEsGrupo(true);
                categoria.setNombre("Familiar");
                categoria.setIcono("algo");
                categoriaRepository.save(categoria);

                grupo.setCategoria(categoria);

                grupo.agregarMiembro(usuario1);
                grupo.agregarMiembro(usuario2);
                grupo.agregarMiembro(usuario3);

                grupoRepository.save(grupo);

                Categoria categoriaGasto = new Categoria();
                categoriaGasto.setEsGrupo(false);
                categoriaGasto.setNombre("Viaje");
                categoriaGasto.setIcono("algo");
                categoriaRepository.save(categoriaGasto);

                // Primer gastoAutor
                GastoAutorDTO gastoAutorDTO1 = new GastoAutorDTO();
                gastoAutorDTO1.setUserId(usuario1.getId());
                gastoAutorDTO1.setMonto(1200.0);

                // Primeros 3 divisionIndividual
                DivisionIndividualDTO divisionIndividualDTO1 = new DivisionIndividualDTO();
                DivisionIndividualDTO divisionIndividualDTO2 = new DivisionIndividualDTO();
                DivisionIndividualDTO divisionIndividualDTO3 = new DivisionIndividualDTO();
                divisionIndividualDTO1.setUserId(usuario1.getId());
                divisionIndividualDTO1.setMonto(400);
                divisionIndividualDTO2.setUserId(usuario2.getId());
                divisionIndividualDTO2.setMonto(400);
                divisionIndividualDTO3.setUserId(usuario3.getId());
                divisionIndividualDTO3.setMonto(400);

                // Primer FormaDividir
                FormaDividirDTO formaDividirDTO1 = new FormaDividirDTO();
                formaDividirDTO1.setFormaDividir(FormatosDivision.MONTO);
                formaDividirDTO1
                                .setDivisionIndividual(List.of(divisionIndividualDTO1, divisionIndividualDTO2,
                                                divisionIndividualDTO3));

                // Primer gasto
                GastoDTO gastoDTO1 = new GastoDTO();
                gastoDTO1.setGastoAutor(List.of(gastoAutorDTO1));
                gastoDTO1.setNombre("Asado");
                gastoDTO1.setFecha(new Date());
                gastoDTO1.setGrupoId(grupo.getId());
                gastoDTO1.setFormaDividir(formaDividirDTO1);
                gastoDTO1.setCategoria(new CategoriaDTO(categoriaGasto));

                // Crear gastos
                Gasto gasto1 = gastoService.newSpendingByDTO(gastoDTO1);

                // Verificar el estado de la base de datos
                assertEquals(1, grupoRepository.findAll().size());
                assertEquals(1, gastoRepository.findAll().size());
                assertEquals(1, gastoAutorRepository.findAll().size());
                assertEquals(1, formaDividirRepository.findAll().size());
                assertEquals(3, divisionIndividualRepository.findAll().size());
                assertEquals(2, deudaUsuarioRepository.findAll().size());
                // Verificar las deudas después del primer gasto
                verificarExistenciaDeuda(usuario3, usuario1, 400.0);
                verificarExistenciaDeuda(usuario2, usuario1, 400.0);

                // Segundo gastoAutor
                GastoAutorDTO gastoAutorDTO2 = new GastoAutorDTO();
                gastoAutorDTO2.setUserId(usuario2.getId());
                gastoAutorDTO2.setMonto(600.0);

                // Segundos 3 divisionIndividual
                DivisionIndividualDTO divisionIndividualDTO4 = new DivisionIndividualDTO();
                DivisionIndividualDTO divisionIndividualDTO5 = new DivisionIndividualDTO();
                DivisionIndividualDTO divisionIndividualDTO6 = new DivisionIndividualDTO();
                divisionIndividualDTO4.setUserId(usuario1.getId());
                divisionIndividualDTO4.setMonto(300);
                divisionIndividualDTO5.setUserId(usuario2.getId());
                divisionIndividualDTO5.setMonto(300);
                divisionIndividualDTO6.setUserId(usuario3.getId());
                divisionIndividualDTO6.setMonto(0);

                // Segundo FormaDividir
                FormaDividirDTO formaDividirDTO2 = new FormaDividirDTO();
                formaDividirDTO2.setFormaDividir(FormatosDivision.MONTO);
                formaDividirDTO2
                                .setDivisionIndividual(List.of(divisionIndividualDTO4, divisionIndividualDTO5,
                                                divisionIndividualDTO6));

                // Segundo gasto
                GastoDTO gastoDTO2 = new GastoDTO();
                gastoDTO2.setGastoAutor(List.of(gastoAutorDTO2));
                gastoDTO2.setNombre("Asado");
                gastoDTO2.setFecha(new Date());
                gastoDTO2.setGrupoId(grupo.getId());
                gastoDTO2.setFormaDividir(formaDividirDTO2);
                gastoDTO2.setCategoria(new CategoriaDTO(categoriaGasto));

                Gasto gasto2 = gastoService.newSpendingByDTO(gastoDTO2);

                // Verificar el estado de la base de datos
                assertEquals(1, grupoRepository.findAll().size());
                assertEquals(2, gastoRepository.findAll().size());
                assertEquals(2, gastoAutorRepository.findAll().size());
                assertEquals(2, formaDividirRepository.findAll().size());
                assertEquals(6, divisionIndividualRepository.findAll().size());
                assertEquals(2, deudaUsuarioRepository.findAll().size());

                // Verificar las deudas después del segundo gasto
                verificarExistenciaDeuda(usuario2, usuario1, 100.0);
                verificarExistenciaDeuda(usuario3, usuario1, 400.0);

                // DTO de gasto para actualizar
                GastoDTO gastoDTOActualizar = new GastoDTO(gasto1);

                gastoDTOActualizar.getGastoAutor().get(0).setUserId(usuario3.getId());

                gastoService.updateSpendingByDTO(gasto1.getId(), gastoDTOActualizar);

                // Verificar el estado de la base de datos
                assertEquals(1, grupoRepository.findAll().size());
                assertEquals(2, gastoRepository.findAll().size());
                assertEquals(2, gastoAutorRepository.findAll().size());
                assertEquals(2, formaDividirRepository.findAll().size());
                assertEquals(6, divisionIndividualRepository.findAll().size());
                assertEquals(2, deudaUsuarioRepository.findAll().size());

                // Verificar las deudas después de la modificación
                verificarExistenciaDeuda(usuario1, usuario3, 700.0);
                verificarExistenciaDeuda(usuario2, usuario3, 100.0);

                // tercer gastoAutor
                GastoAutorDTO gastoAutorDTO3 = new GastoAutorDTO();
                gastoAutorDTO3.setUserId(usuario1.getId());
                gastoAutorDTO3.setMonto(1400.0);

                // tercers 3 divisionIndividual
                DivisionIndividualDTO divisionIndividualDTO7 = new DivisionIndividualDTO();
                DivisionIndividualDTO divisionIndividualDTO8 = new DivisionIndividualDTO();
                DivisionIndividualDTO divisionIndividualDTO9 = new DivisionIndividualDTO();
                divisionIndividualDTO7.setUserId(usuario1.getId());
                divisionIndividualDTO7.setMonto(700);
                divisionIndividualDTO8.setUserId(usuario2.getId());
                divisionIndividualDTO8.setMonto(0);
                divisionIndividualDTO9.setUserId(usuario3.getId());
                divisionIndividualDTO9.setMonto(700);

                // tercer FormaDividir
                FormaDividirDTO formaDividirDTO3 = new FormaDividirDTO();
                formaDividirDTO3.setFormaDividir(FormatosDivision.MONTO);
                formaDividirDTO3
                                .setDivisionIndividual(List.of(divisionIndividualDTO7, divisionIndividualDTO8,
                                                divisionIndividualDTO9));

                // tercer gasto
                GastoDTO gastoDTO3 = new GastoDTO();
                gastoDTO3.setGastoAutor(List.of(gastoAutorDTO3));
                gastoDTO3.setNombre("Asado");
                gastoDTO3.setFecha(new Date());
                gastoDTO3.setGrupoId(grupo.getId());
                gastoDTO3.setFormaDividir(formaDividirDTO3);
                gastoDTO3.setCategoria(new CategoriaDTO(categoriaGasto));

                gastoService.newSpendingByDTO(gastoDTO3);

                // Verificar el estado de la base de datos
                assertEquals(1, grupoRepository.findAll().size());
                assertEquals(3, gastoRepository.findAll().size());
                assertEquals(3, gastoAutorRepository.findAll().size());
                assertEquals(3, formaDividirRepository.findAll().size());
                assertEquals(9, divisionIndividualRepository.findAll().size());
                assertEquals(1, deudaUsuarioRepository.findAll().size());

                // Verificar las deudas después del tercer gasto
                verificarExistenciaDeuda(usuario2, usuario3, 100.0);
                // verificarExistenciaDeuda(usuario3, usuario1, 800.0);
                // verificarExistenciaDeuda(usuario2, usuario3, 800.0);

                // DTO de gasto para actualizar
                GastoDTO gastoDTO2Actualizar = new GastoDTO(gasto2);

                gastoDTO2Actualizar.getGastoAutor().get(0).setMonto(700.0);
                for (DivisionIndividualDTO divInd : gastoDTO2Actualizar.getFormaDividir().getDivisionIndividual()) {
                        if (divInd.getUserId() == usuario3.getId()) {
                                divInd.setMonto(100);
                                break;
                        }
                }

                gastoService.updateSpendingByDTO(gasto2.getId(), gastoDTO2Actualizar);

                // Verificar el estado de la base de datos
                assertEquals(1, grupoRepository.findAll().size());
                assertEquals(3, gastoRepository.findAll().size());
                assertEquals(3, gastoAutorRepository.findAll().size());
                assertEquals(3, formaDividirRepository.findAll().size());
                assertEquals(9, divisionIndividualRepository.findAll().size());
                assertEquals(0, deudaUsuarioRepository.findAll().size());

        }

        private void verificarExistenciaDeuda(Usuario deudor, Usuario acreedor, double monto) {
                List<DeudaUsuario> deudas = deudaUsuarioRepository
                                .findByDeudorAndAcreedorAndMonto(deudor, acreedor, monto);
                assertEquals(1, deudas.size(), String.format(
                                "No se encontró la deuda entre el deudor %s, el acreedor %s y el monto %f",
                                deudor.getUsername(), acreedor.getUsername(), monto));
        }
}
