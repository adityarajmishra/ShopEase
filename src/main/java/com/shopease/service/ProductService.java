package com.shopease.service;

import com.shopease.dto.request.ProductRequest;
import com.shopease.dto.response.PagedResponse;
import com.shopease.dto.response.ProductResponse;
import com.shopease.model.Product;

import java.util.List;

/**
 * Service interface for product-related operations.
 */
public interface ProductService {

    /**
     * Creates a new product.
     *
     * @param productRequest The product data
     * @return The created product
     */
    Product createProduct(ProductRequest productRequest);

    /**
     * Updates an existing product.
     *
     * @param id The product ID
     * @param productRequest The updated product data
     * @return The updated product
     */
    Product updateProduct(Long id, ProductRequest productRequest);

    /**
     * Deletes a product.
     *
     * @param id The product ID
     */
    void deleteProduct(Long id);

    /**
     * Gets a product by ID.
     *
     * @param id The product ID
     * @return The product
     */
    Product getProductById(Long id);

    /**
     * Gets all products with pagination.
     *
     * @param page The page number
     * @param size The page size
     * @return A paged response of products
     */
    PagedResponse<ProductResponse> getAllProducts(int page, int size);

    /**
     * Gets products by category with pagination.
     *
     * @param category The category to filter by
     * @param page The page number
     * @param size The page size
     * @return A paged response of products
     */
    PagedResponse<ProductResponse> getProductsByCategory(String category, int page, int size);

    /**
     * Searches for products by name with pagination.
     *
     * @param searchTerm The search term
     * @param page The page number
     * @param size The page size
     * @return A paged response of products
     */
    PagedResponse<ProductResponse> searchProducts(String searchTerm, int page, int size);

    /**
     * Gets products with low stock.
     *
     * @param threshold The stock threshold
     * @return A list of products with stock below the threshold
     */
    List<Product> getProductsWithLowStock(int threshold);
}