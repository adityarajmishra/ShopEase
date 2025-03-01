package com.shopease.controller;

import com.shopease.dto.request.ProductRequest;
import com.shopease.dto.response.ApiResponse;
import com.shopease.dto.response.PagedResponse;
import com.shopease.dto.response.ProductResponse;
import com.shopease.model.Product;
import com.shopease.service.ProductService;
import com.shopease.util.AppConstants;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for product operations.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Gets all products with pagination.
     *
     * @param page The page number
     * @param size The page size
     * @return ResponseEntity with paged products
     */
    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        PagedResponse<ProductResponse> response = productService.getAllProducts(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets a product by ID.
     *
     * @param id The product ID
     * @return ResponseEntity with the product
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ProductResponse.fromEntity(product));
    }

    /**
     * Creates a new product (admin only).
     *
     * @param productRequest The product data
     * @return ResponseEntity with the created product
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        Product product = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.fromEntity(product));
    }

    /**
     * Updates a product (admin only).
     *
     * @param id The product ID
     * @param productRequest The updated product data
     * @return ResponseEntity with the updated product
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest productRequest) {

        Product product = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(ProductResponse.fromEntity(product));
    }

    /**
     * Deletes a product (admin only).
     *
     * @param id The product ID
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse(true, "Product deleted successfully"));
    }

    /**
     * Gets products by category with pagination.
     *
     * @param category The category to filter by
     * @param page The page number
     * @param size The page size
     * @return ResponseEntity with paged products
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<PagedResponse<ProductResponse>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        PagedResponse<ProductResponse> response = productService.getProductsByCategory(category, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Searches for products by name with pagination.
     *
     * @param query The search query
     * @param page The page number
     * @param size The page size
     * @return ResponseEntity with paged products
     */
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ProductResponse>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        PagedResponse<ProductResponse> response = productService.searchProducts(query, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets products with low stock (admin only).
     *
     * @param threshold The stock threshold
     * @return ResponseEntity with products with low stock
     */
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductResponse>> getProductsWithLowStock(
            @RequestParam(defaultValue = "5") int threshold) {

        List<Product> products = productService.getProductsWithLowStock(threshold);
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}