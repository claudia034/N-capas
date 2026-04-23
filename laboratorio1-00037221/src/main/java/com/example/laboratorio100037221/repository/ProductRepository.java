package com.example.laboratorio100037221.repository;

import com.example.laboratorio100037221.common.ProductList;
import com.example.laboratorio100037221.domain.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepository {

    private final ProductList productList;

    public ProductRepository(ProductList productList) {
        this.productList = productList;
    }

    public List<Product> findAll() {
        return productList.getCatalogo();
    }
}
