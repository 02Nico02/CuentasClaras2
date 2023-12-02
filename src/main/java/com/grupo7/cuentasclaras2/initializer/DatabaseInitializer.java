package com.grupo7.cuentasclaras2.initializer;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.grupo7.cuentasclaras2.modelos.Categoria;
import com.grupo7.cuentasclaras2.repositories.CategoriaRepository;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    private final CategoriaRepository categoriaRepository;

    public DatabaseInitializer(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        initializeCategorias();
    }

    private void initializeCategorias() {
        List<Categoria> categorias = categoriaRepository.findAll();

        if (categorias.isEmpty()) {
            Categoria casa = new Categoria("Casa", "icono_casa.png", true);
            Categoria trabajo = new Categoria("Trabajo", "icono_trabajo.png", true);
            Categoria familia = new Categoria("Familia", "icono_familia.png", true);
            Categoria comida = new Categoria("Comida", "icono_comida.png", false);
            Categoria transporte = new Categoria("Transporte", "icono_transporte.png", false);
            Categoria entretenimiento = new Categoria("Entretenimiento", "icono_entretenimiento.png", false);

            categoriaRepository.saveAll(List.of(casa, trabajo, familia, comida, transporte, entretenimiento));
        }
    }
}
