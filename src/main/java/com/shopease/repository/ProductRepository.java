package com.shopease.repository;

import com.shopease.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds all products in a specific category.
     *
     * @param category The category to filter by
     * @return A list of products in the specified category
     */
    List<Product> findByCategory(String category);

    /**
     * Finds all products in a specific category with pagination.
     *
     * @param category The category to filter by
     * @param pageable Pagination information
     * @return A page of products in the specified category
     */
    Page<Product> findByCategory(String category, Pageable pageable);

    /**
     * Checks if a product with the given name exists.
     *
     * @param name The name to check
     * @return true if a product with the name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Searches for products by name containing the search term.
     *
     * @param searchTerm The search term to look for in product names
     * @param pageable Pagination information
     * @return A page of products with names containing the search term
     */
    Page<Product> findByNameContainingIgnoreCase(String searchTerm, Pageable pageable);

    /**
     * Finds products with low stock (below a specified threshold).
     *
     * @param threshold The stock threshold
     * @return A list of products with stock below the threshold
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= :threshold")
    List<Product> findProductsWithLowStock(@Param("threshold") int threshold);
}