package com.ecom.product_service.ProductService;

import com.ecom.product_service.dto.ProductRequest;
import com.ecom.product_service.dto.ProductResponse;
import com.ecom.product_service.dto.SearchSuggestion;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    List<ProductResponse> createProductsBulk(List<ProductRequest> requests);

    ProductResponse getProductById(Long id);

    List<ProductResponse> getAllProducts();

    List<ProductResponse> getProductsByCategory(Long categoryId);

    List<ProductResponse> getProductsByCategoryAndType(Long categoryId, String type);

    List<ProductResponse> searchProducts(String query);

    List<SearchSuggestion> getSearchSuggestions(String query);

    ProductResponse updateProduct(Long id, ProductRequest request);


    void deleteProduct(Long id);
}