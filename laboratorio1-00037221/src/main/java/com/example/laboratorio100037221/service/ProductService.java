package com.example.laboratorio100037221.service;

import com.example.laboratorio100037221.domain.entity.Product;
import com.example.laboratorio100037221.domain.enums.Rareza;
import com.example.laboratorio100037221.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> obtenerTodos() {
        return productRepository.findAll();
    }

    public List<Product> obtenerOrdenadosPorPrecioDesc() {
        List<Product> productos = new ArrayList<>(productRepository.findAll());
        productos.sort(Comparator.comparingInt(Product::getPrecio).reversed());
        return productos;
    }

    public Product obtenerMasCaro() {
        return productRepository.findAll().stream()
                .max(Comparator.comparingInt(Product::getPrecio))
                .orElseThrow(() -> new IllegalStateException("No hay nada en el catálogo"));
    }

    public List<Product> obtenerLegendarios() {
        return productRepository.findAll().stream()
                .filter(product -> product.getRareza() == Rareza.LEGENDARIO)
                .toList();
    }

    public List<String> obtenerUbicacionesUnicas() {
        return productRepository.findAll().stream()
                .map(Product::getUbicacion)
                .distinct()
                .sorted()
                .toList();
    }
}