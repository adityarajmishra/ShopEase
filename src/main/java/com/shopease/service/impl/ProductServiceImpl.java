package com.shopease.service.impl;

import com.shopease.dto.request.ProductRequest;
import com.shopease.dto.response.PagedResponse;
import com.shopease.dto.response.ProductResponse;
import com.shopease.exception.InvalidProductDataException;
import com.shopease.exception.ProductNameAlreadyExistsException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.model.Product;
import com.shopease.repository.ProductRepository;
import com.shopease.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the ProductService interface.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Product createProduct(ProductRequest productRequest) {
        validateProductData(productRequest, null);

        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setCategory(productRequest.getCategory());
        product.setStatus(Product.ProductStatus.ACTIVE);

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        validateProductData(productRequest, product);

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setCategory(productRequest.getCategory());

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> products = productRepository.findAll(pageable);

        return new PagedResponse<>(
                products.getContent().stream().map(ProductResponse::fromEntity).collect(Collectors.toList()),
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> products = productRepository.findByCategory(category, pageable);

        return new PagedResponse<>(
                products.getContent().stream().map(ProductResponse::fromEntity).collect(Collectors.toList()),
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> searchProducts(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> products = productRepository.findByNameContainingIgnoreCase(searchTerm, pageable);

        return new PagedResponse<>(
                products.getContent().stream().map(ProductResponse::fromEntity).collect(Collectors.toList()),
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsWithLowStock(int threshold) {
        return productRepository.findProductsWithLowStock(threshold);
    }

    /**
     * Validates product data when creating or updating a product.
     *
     * @param productRequest The product data to validate
     * @param existingProduct The existing product (null for new products)
     */
    private void validateProductData(ProductRequest productRequest, Product existingProduct) {
        // Validate name uniqueness
        if (existingProduct == null || !existingProduct.getName().equals(productRequest.getName())) {
            if (productRepository.existsByName(productRequest.getName())) {
                throw new ProductNameAlreadyExistsException("Product name already exists: " + productRequest.getName());
            }
        }

        // Validate price
        if (productRequest.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidProductDataException("Price must be a positive number");
        }

        // Validate stock quantity
        if (productRequest.getStockQuantity() < 0) {
            throw new InvalidProductDataException("Stock quantity cannot be negative");
        }
    }
}