package com.ecom.product_service.controller;

import com.ecom.product_service.ProductService.ProductService;
import com.ecom.product_service.dto.ProductRequest;
import com.ecom.product_service.dto.ProductResponse;
import com.ecom.product_service.dto.SearchSuggestion;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE PRODUCT
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        ProductResponse response =
                productService.createProduct(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }


    // CREATE PRODUCTS IN BULK
   @PostMapping("/bulk")
    public ResponseEntity<List<ProductResponse>> createProductsBulk(
            @Valid @RequestBody List<ProductRequest> requests) {

        List<ProductResponse> responses =
                productService.createProductsBulk(requests);

        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }
        @GetMapping("/search")
        public ResponseEntity<List<ProductResponse>> searchProducts(
                @RequestParam String q) {

        List<ProductResponse> response =
                productService.searchProducts(q);

        return ResponseEntity.ok(response);
        }
        @GetMapping("/search/suggestions")
        public ResponseEntity<List<SearchSuggestion>> getSearchSuggestions(
                @RequestParam String q) {

        return ResponseEntity.ok(
                productService.getSearchSuggestions(q)
        );
        }

    // GET PRODUCT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id) {

        ProductResponse response =
                productService.getProductById(id);

        return ResponseEntity.ok(response);
    }


    // GET PRODUCTS
    //
    // Examples:
    //
    // GET /products
    // GET /products?categoryId=1
    // GET /products?categoryId=1&type=SHIRT
    //
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String type) {

        List<ProductResponse> response;

        // Category + Type
        if (categoryId != null && type != null) {

            response =
                    productService
                            .getProductsByCategoryAndType(
                                    categoryId,
                                    type
                            );

        }

        // Category only
        else if (categoryId != null) {

            response =
                    productService
                            .getProductsByCategory(
                                    categoryId
                            );

        }

        // No filters
        else {

            response =
                    productService.getAllProducts();
        }

        return ResponseEntity.ok(response);
    }


    // UPDATE PRODUCT
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        ProductResponse response =
                productService.updateProduct(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }


    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
    
}