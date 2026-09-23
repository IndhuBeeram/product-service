package com.ecom.product_service.repository;

import com.ecom.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByActiveTrue();

    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

    List<Product> findByTypeAndActiveTrue(String type);

    List<Product> findByCategoryIdAndTypeAndActiveTrue(
            Long categoryId,
            String type
    );

    @Query("""
            SELECT p
            FROM Product p
            JOIN p.category c
            WHERE p.active = true
              AND (
                    LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
                    OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))
                    OR LOWER(p.type) LIKE LOWER(CONCAT('%', :query, '%'))
                    OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))
              )
            """)
    List<Product> searchProducts(@Param("query") String query);

    @Query("""
        SELECT DISTINCT p.brand
        FROM Product p
        WHERE p.active = true
          AND LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY p.brand
        """)
List<String> findBrandSuggestions(@Param("query") String query);

@Query("""
        SELECT DISTINCT p.name
        FROM Product p
        WHERE p.active = true
          AND LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY p.name
        """)
List<String> findProductSuggestions(@Param("query") String query);

@Query("""
        SELECT DISTINCT c.name
        FROM Product p
        JOIN p.category c
        WHERE p.active = true
          AND LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY c.name
        """)
List<String> findCategorySuggestions(@Param("query") String query);

@Query("""
    SELECT DISTINCT p.brand
    FROM Product p
    WHERE p.active = true
    AND LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))
    ORDER BY p.brand
""")
List<String> findMatchingBrands(@Param("query") String query);


@Query("""
    SELECT DISTINCT p.type
    FROM Product p
    WHERE p.active = true
    AND LOWER(p.type) LIKE LOWER(CONCAT('%', :query, '%'))
    ORDER BY p.type
""")
List<String> findMatchingTypes(@Param("query") String query);

}