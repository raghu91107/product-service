package com.ecom.service;

import com.ecom.dto.ProductRequest;
import com.ecom.dto.ProductResponse;
import com.ecom.entity.Product;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    List<ProductResponse> getProductsByCategory(String category);
    List<ProductResponse> searchProducts(String name);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}
