package com.example.laboratorio100037221.common;

import com.example.laboratorio100037221.domain.entity.Product;
import com.example.laboratorio100037221.domain.enums.Categoria;
import com.example.laboratorio100037221.domain.enums.Efecto;
import com.example.laboratorio100037221.domain.enums.Rareza;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductList {

    public List<Product> getCatalogo() {
        return List.of(
                Product.builder()
                        .nombre("\u00C1mbar Rojo")
                        .categoria(Categoria.MINERAL)
                        .efecto(Efecto.DEFENSA)
                        .precio(30)
                        .ubicacion("Volc\u00E1n de Eldin")
                        .rareza(Rareza.LEGENDARIO)
                        .build(),
                Product.builder()
                        .nombre("Ala de Keese")
                        .categoria(Categoria.PARTE_DE_MONSTRUO)
                        .efecto(Efecto.ATAQUE)
                        .precio(15)
                        .ubicacion("Llanura de Hyrule")
                        .rareza(Rareza.COMUN)
                        .build(),
                Product.builder()
                        .nombre("Pimienta Ardiente")
                        .categoria(Categoria.PLANTA)
                        .efecto(Efecto.ESTAMINA)
                        .precio(10)
                        .ubicacion("Cordillera de Hebra")
                        .rareza(Rareza.COMUN)
                        .build()
        );
    }
}
