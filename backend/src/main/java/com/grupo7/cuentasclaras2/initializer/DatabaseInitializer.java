package com.grupo7.cuentasclaras2.initializer;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.grupo7.cuentasclaras2.modelos.Categoria;
import com.grupo7.cuentasclaras2.repositories.CategoriaRepository;

/**
 * Inicializador de la base de datos que carga categorías predeterminadas si la
 * base de datos está vacía.
 */
@Component
public class DatabaseInitializer implements ApplicationRunner {

    private final CategoriaRepository categoriaRepository;

    /**
     * Constructor del inicializador que recibe una instancia de
     * CategoriaRepository.
     *
     * @param categoriaRepository Repositorio de categorías.
     */
    public DatabaseInitializer(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Método que se ejecuta al arrancar la aplicación para inicializar la base de
     * datos.
     *
     * @param args Argumentos de la aplicación.
     */
    @Override
    public void run(ApplicationArguments args) {
        initializeCategorias();
    }

    /**
     * Inicializa las categorías predeterminadas si la base de datos está vacía.
     */
    private void initializeCategorias() {
        List<Categoria> categorias = categoriaRepository.findAll();

        if (categorias.isEmpty()) {
            Categoria salida = new Categoria("Salida", "icono_salida.png", true);
            Categoria trabajo = new Categoria("Trabajo", "icono_trabajo.png", true);
            Categoria familia = new Categoria("Familia", "icono_familia.png", true);
            Categoria amigos = new Categoria("Amigos", "icono_amigos.png", true);
            Categoria estudio = new Categoria("Estudio", "icono_estudio.png", true);
            Categoria deportes = new Categoria("Deportes", "icono_deportes.png", true);
            Categoria viaje_grupo = new Categoria("Viaje", "icono_viajes.png", true);
            Categoria comida = new Categoria("Comida", "icono_comida.png", false);
            Categoria transporte = new Categoria("Transporte", "icono_transporte.png", false);
            Categoria entretenimiento = new Categoria("Entretenimiento", "icono_entretenimiento.png", false);
            Categoria salud = new Categoria("Salud", "icono_salud.png", false);
            Categoria ropa = new Categoria("Ropa", "icono_ropa.png", false);
            Categoria educacion = new Categoria("Educación", "icono_educacion.png", false);
            Categoria tecnologia = new Categoria("Tecnología", "icono_tecnologia.png", false);
            Categoria viajes = new Categoria("Viajes", "icono_viajes.png", false);
            Categoria Servicios_publicos = new Categoria("Servicios públicos", "icono_Servicios_publicos.png", false);

            categoriaRepository.saveAll(List.of(salida, trabajo, familia, amigos, estudio, deportes, viaje_grupo,
                    comida,
                    transporte, entretenimiento, salud, ropa, educacion, tecnologia, viajes, Servicios_publicos));
        }
    }
}
