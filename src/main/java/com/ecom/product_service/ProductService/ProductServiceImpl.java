package com.ecom.product_service.ProductService;

import com.ecom.product_service.dto.ProductRequest;
import com.ecom.product_service.dto.ProductResponse;
import com.ecom.product_service.entity.Category;
import com.ecom.product_service.entity.Product;
import com.ecom.product_service.exception.ProductNotFoundException;
import com.ecom.product_service.repository.CategoryRepository;
import com.ecom.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // CREATE PRODUCT
    @Override
    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setBrand(request.getBrand());
        product.setImageUrl(request.getImageUrl());
        product.setActive(true);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    // GET PRODUCT BY ID
    @Override
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id: " + id)
                );

        return mapToResponse(product);
    }

    // GET ALL PRODUCTS
    @Override
    public List<ProductResponse> getAllProducts() {

        return productRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // UPDATE PRODUCT
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id: " + id)
                );

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setBrand(request.getBrand());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    // DELETE PRODUCT
    @Override
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id: " + id)
                );

        product.setActive(false);
        productRepository.save(product);
    }

    // ENTITY → RESPONSE DTO
    private ProductResponse mapToResponse(Product product) {

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setBrand(product.getBrand());
        response.setImageUrl(product.getImageUrl());
        response.setActive(product.getActive());

        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());

        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        return response;
    }
}