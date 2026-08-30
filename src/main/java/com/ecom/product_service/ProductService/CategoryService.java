package com.ecom.product_service.ProductService;

import com.ecom.product_service.dto.CategoryRequest;
import com.ecom.product_service.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    List<CategoryResponse> getAllCategories();
}