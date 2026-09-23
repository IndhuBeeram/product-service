package com.ecom.product_service.ProductService;

import com.ecom.product_service.dto.ProductRequest;
import com.ecom.product_service.dto.ProductResponse;
import com.ecom.product_service.dto.SearchSuggestion;
import com.ecom.product_service.entity.Category;
import com.ecom.product_service.entity.Product;
import com.ecom.product_service.entity.ProductImage;
import com.ecom.product_service.exception.ProductNotFoundException;
import com.ecom.product_service.repository.CategoryRepository;
import com.ecom.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setBrand(request.getBrand());
        product.setType(request.getType());
        product.setActive(true);
        product.setCategory(category);

        // Add product images
        addImages(product, request.getImageUrls());

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    // GET PRODUCT BY ID
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        return mapToResponse(product);
    }

    // GET ALL PRODUCTS
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {

        return productRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // UPDATE PRODUCT
    @Override
    @Transactional
    public ProductResponse updateProduct(
            Long id,
            ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setBrand(request.getBrand());
        product.setType(request.getType());
        product.setCategory(category);

        /*
         * Remove old images.
         *
         * orphanRemoval = true in Product entity
         * will remove them from product_images table.
         */
        product.getImages().clear();

        /*
         * Add the new images.
         */
        addImages(product, request.getImageUrls());

        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    // DELETE PRODUCT
    @Override
    @Transactional
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        /*
         * Soft delete.
         *
         * Product remains in database,
         * but active becomes false.
         */
        product.setActive(false);

        productRepository.save(product);
    }
    // GET PRODUCTS BY CATEGORY
@Override
@Transactional(readOnly = true)
public List<ProductResponse> getProductsByCategory(Long categoryId) {

    return productRepository
            .findByCategoryIdAndActiveTrue(categoryId)
            .stream()
            .map(this::mapToResponse)
            .toList();
}


// GET PRODUCTS BY CATEGORY AND TYPE
@Override
@Transactional(readOnly = true)
public List<ProductResponse> getProductsByCategoryAndType(
        Long categoryId,
        String type) {

    return productRepository
            .findByCategoryIdAndTypeAndActiveTrue(
                    categoryId,
                    type
            )
            .stream()
            .map(this::mapToResponse)
            .toList();
}

    // ADD PRODUCT IMAGES
    private void addImages(
            Product product,
            List<String> imageUrls) {

        for (int i = 0; i < imageUrls.size(); i++) {

            ProductImage image = new ProductImage();

            image.setImageUrl(imageUrls.get(i));

            /*
             * First image becomes the primary image.
             */
            image.setDisplayOrder(i + 1);
            image.setPrimaryImage(i == 0);

            /*
             * This sets the Product relationship
             * from both sides.
             */
            product.addImage(image);
        }
    }
    @Override
@Transactional
public List<ProductResponse> createProductsBulk(List<ProductRequest> requests) {

    List<Product> products = requests.stream()
            .map(request -> {

                Category category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found: " + request.getCategoryId()
                                ));

                Product product = new Product();

                product.setName(request.getName());
                product.setDescription(request.getDescription());
                product.setPrice(request.getPrice());
                product.setBrand(request.getBrand());
                product.setType(request.getType());
                product.setActive(true);
                product.setCategory(category);

                addImages(product, request.getImageUrls());

                return product;
            })
            .toList();

    List<Product> savedProducts = productRepository.saveAll(products);

    return savedProducts.stream()
            .map(this::mapToResponse)
            .toList();
}

    // ENTITY → RESPONSE DTO
    private ProductResponse mapToResponse(Product product) {

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setBrand(product.getBrand());
        response.setType(product.getType());
        response.setActive(product.getActive());

        response.setCategoryId(
                product.getCategory().getId()
        );

        response.setCategoryName(
                product.getCategory().getName()
        );

        /*
         * Convert ProductImage entities
         * into image URL list.
         */
        List<String> imageUrls = product.getImages()
                .stream()
                .map(ProductImage::getImageUrl)
                .toList();

        response.setImageUrls(imageUrls);

        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        return response;
    }
    @Override
        @Transactional(readOnly = true)
        public List<ProductResponse> searchProducts(String query) {

        return productRepository
                .searchProducts(query.trim())
                .stream()
                .map(this::mapToResponse)
                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<SearchSuggestion> getSearchSuggestions(String query) {

        if (query == null || query.trim().length() < 2) {
                return List.of();
        }

        String q = query.trim().toLowerCase();

        List<SearchSuggestion> suggestions = new ArrayList<>();

        /*
        * SHIRT / T-SHIRT SEARCH
        */
        if (q.contains("shi")) {

                suggestions.add(
                        new SearchSuggestion(
                                "CATEGORY",
                                "Men Shirts"
                        )
                );

                suggestions.add(
                        new SearchSuggestion(
                                "CATEGORY",
                                "Women Shirts"
                        )
                );

                suggestions.add(
                        new SearchSuggestion(
                                "CATEGORY",
                                "Men T-Shirts"
                        )
                );

                suggestions.add(
                        new SearchSuggestion(
                                "CATEGORY",
                                "Women T-Shirts"
                        )
                );
        }

        /*
        * SHOE SEARCH
        */
        if (q.contains("shoe")) {

                suggestions.add(
                        new SearchSuggestion(
                                "CATEGORY",
                                "Men Shoes"
                        )
                );

                suggestions.add(
                        new SearchSuggestion(
                                "CATEGORY",
                                "Women Shoes"
                        )
                );
        }

        /*
        * BRAND SEARCH
        */
        List<String> brands =
                productRepository.findMatchingBrands(query);

        for (String brand : brands) {

                boolean alreadyExists =
                        suggestions.stream()
                                .anyMatch(s ->
                                        s.getText()
                                                .equalsIgnoreCase(brand)
                                );

                if (!alreadyExists) {

                suggestions.add(
                        new SearchSuggestion(
                                "BRAND",
                                brand
                        )
                );
                }
        }

        /*
        * Limit suggestions
        */
        return suggestions.stream()
                .limit(8)
                .toList();
        }
}