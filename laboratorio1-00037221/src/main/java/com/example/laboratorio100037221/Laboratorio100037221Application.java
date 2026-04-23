package com.example.laboratorio100037221;

import com.example.laboratorio100037221.domain.entity.Product;
import com.example.laboratorio100037221.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Laboratorio100037221Application implements CommandLineRunner {

    private final ProductService productService;

    public Laboratorio100037221Application(ProductService productService) {
        this.productService = productService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Laboratorio100037221Application.class, args);
    }

    @Override
    public void run(String... args) {

        System.out.println("=== TODOS ===");
        for (Product product : productService.obtenerTodos()) {
            System.out.printf(
                    "[HYRULE-DB] Nombre: %s | Categoria: %s | Precio: %d Rupias%n",
                    product.getNombre(),
                    product.getCategoria().getNombre(),
                    product.getPrecio()
            );
        }

        System.out.println("\n=== ORDENADOS POR PRECIO DESC ===");
        for (Product product : productService.obtenerOrdenadosPorPrecioDesc()) {
            System.out.printf(
                    "[HYRULE-DB] Nombre: %s | Categoria: %s | Precio: %d Rupias%n",
                    product.getNombre(),
                    product.getCategoria().getNombre(),
                    product.getPrecio()
            );
        }

        System.out.println("\n=== PRODUCTO MAS CARO ===");
        Product masCaro = productService.obtenerMasCaro();
        System.out.printf(
                "[HYRULE-DB] Nombre: %s | Categoria: %s | Precio: %d Rupias%n",
                masCaro.getNombre(),
                masCaro.getCategoria().getNombre(),
                masCaro.getPrecio()
        );

        System.out.println("\n=== PRODUCTOS LEGENDARIOS ===");
        for (Product product : productService.obtenerLegendarios()) {
            System.out.printf(
                    "[HYRULE-DB] Nombre: %s | Categoria: %s | Rareza: %s | Precio: %d Rupias%n",
                    product.getNombre(),
                    product.getCategoria().getNombre(),
                    product.getRareza().getNombre(),
                    product.getPrecio()
            );
        }

        System.out.println("\n=== UBICACIONES UNICAS ===");
        for (String ubicacion : productService.obtenerUbicacionesUnicas()) {
            System.out.println("[HYRULE-DB] Ubicacion: " + ubicacion);
        }
    }
}