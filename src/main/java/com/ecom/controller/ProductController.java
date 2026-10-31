package com.ecom.controller;

import com.ecom.dto.ProductRequest;
import com.ecom.dto.ProductResponse;
import com.ecom.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Slf4j
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request){
        log.info("product order request {} ",request);
        ProductResponse response=productService.createProduct(request);

        return ResponseEntity.created(URI.create("/api/v1/products/"+response.getId())).body(response);
    }
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(@RequestParam(required = false) String category,
                                                             @RequestParam(required = false) String search){
        List<ProductResponse> products;
        if(category!=null && !category.isBlank()){
            products= productService.getProductsByCategory(category);
        }
        else if(search!=null && !search.isBlank()){
            products=productService.searchProducts(search);
        }
        else {
            products=productService.getAllProducts();
        }
        return ResponseEntity.ok(products);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request){
        return ResponseEntity.ok(productService.updateProduct(id,request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
