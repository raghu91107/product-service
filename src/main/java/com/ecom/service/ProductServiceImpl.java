package com.ecom.service;

import com.ecom.dto.ProductRequest;
import com.ecom.dto.ProductResponse;
import com.ecom.entity.Product;
import com.ecom.exception.ResourceNotFoundException;
import com.ecom.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        //In real applications, tracing libraries often populate these automatically
        String traceId= UUID.randomUUID().toString();
        MDC.put("traceId",traceId);
        //MDC.put("orderId",request.)
        try {
            log.info("Creating product with name: {}", request.getName());

        Product product = new Product();
        copyRequestToEntity(request, product);

            Product savedProduct = productRepository.save(product);
            log.info("Product created successfully. productId={}",
                    savedProduct.getId());
            return mapToResponse(savedProduct);
        }catch (RuntimeException ex) {
            log.error("Failed to create product. productName={}",
                    request.getName(), ex);
            throw ex;
        } finally {
            MDC.remove("traceId");
        }


    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = getProductOrThrow(id);
        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository
                .findByCategoryIgnoreCaseAndActiveTrue(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String name) {
        return productRepository
                .findByNameContainingIgnoreCaseAndActiveTrue(name)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProductResponse updateProduct(
            Long id, ProductRequest request) {

        Product existingProduct = getProductOrThrow(id);

        copyRequestToEntity(request, existingProduct);

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductOrThrow(id);

        // Soft delete: keep the record but hide it from normal listings.
        product.setActive(false);
        productRepository.save(product);
    }

    private Product getProductOrThrow(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id));

        if (!product.isActive()) {
            throw new ResourceNotFoundException(
                    "Product not found with id: " + id);
        }

        return product;
    }

    private void copyRequestToEntity(
            ProductRequest request, Product product) {

        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(request.getCategory().trim());
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setCategory(product.getCategory());
        response.setActive(product.isActive());
        response.setCreatedAt(product.getCreatedAt());

        return response;
    }
}
