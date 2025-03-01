package com.shopease.service;

import com.shopease.dto.request.ProductRequest;
import com.shopease.dto.response.PagedResponse;
import com.shopease.dto.response.ProductResponse;
import com.shopease.exception.InvalidProductDataException;
import com.shopease.exception.ProductNameAlreadyExistsException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.model.Product;
import com.shopease.repository.ProductRepository;
import com.shopease.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest validProductRequest;
    private Product existingProduct;

    @BeforeEach
    public void setup() {
        validProductRequest = new ProductRequest();
        validProductRequest.setName("Test Product");
        validProductRequest.setDescription("Test Description");
        validProductRequest.setPrice(new BigDecimal("99.99"));
        validProductRequest.setStockQuantity(10);
        validProductRequest.setCategory("Electronics");

        existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Existing Product");
        existingProduct.setDescription("Existing Description");
        existingProduct.setPrice(new BigDecimal("199.99"));
        existingProduct.setStockQuantity(20);
        existingProduct.setCategory("Electronics");
        existingProduct.setStatus(Product.ProductStatus.ACTIVE);
    }

    @Test
    public void testCreateProduct_Success() {
        // Arrange
        when(productRepository.existsByName(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(1L);
            return savedProduct;
        });

        // Act
        Product result = productService.createProduct(validProductRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(validProductRequest.getName(), result.getName());
        assertEquals(validProductRequest.getDescription(), result.getDescription());
        assertEquals(0, validProductRequest.getPrice().compareTo(result.getPrice()));
        assertEquals(validProductRequest.getStockQuantity(), result.getStockQuantity());
        assertEquals(validProductRequest.getCategory(), result.getCategory());
        assertEquals(Product.ProductStatus.ACTIVE, result.getStatus());

        verify(productRepository).existsByName(validProductRequest.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void testCreateProduct_NameAlreadyExists() {
        // Arrange
        when(productRepository.existsByName(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(ProductNameAlreadyExistsException.class, () -> {
            productService.createProduct(validProductRequest);
        });

        verify(productRepository).existsByName(validProductRequest.getName());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void testCreateProduct_NegativePrice() {
        // Arrange
        ProductRequest invalidRequest = new ProductRequest();
        invalidRequest.setName("Test Product");
        invalidRequest.setDescription("Test Description");
        invalidRequest.setPrice(new BigDecimal("-10.00"));
        invalidRequest.setStockQuantity(10);
        invalidRequest.setCategory("Electronics");

        // Act & Assert
        assertThrows(InvalidProductDataException.class, () -> {
            productService.createProduct(invalidRequest);
        });

        verify(productRepository, never()).existsByName(anyString());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void testCreateProduct_NegativeStock() {
        // Arrange
        ProductRequest invalidRequest = new ProductRequest();
        invalidRequest.setName("Test Product");
        invalidRequest.setDescription("Test Description");
        invalidRequest.setPrice(new BigDecimal("99.99"));
        invalidRequest.setStockQuantity(-5);
        invalidRequest.setCategory("Electronics");

        // Act & Assert
        assertThrows(InvalidProductDataException.class, () -> {
            productService.createProduct(invalidRequest);
        });

        verify(productRepository, never()).existsByName(anyString());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void testUpdateProduct_Success() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByName(validProductRequest.getName())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        // Act
        Product result = productService.updateProduct(productId, validProductRequest);

        // Assert
        assertNotNull(result);
        assertEquals(existingProduct.getId(), result.getId());
        assertEquals(validProductRequest.getName(), result.getName());
        assertEquals(validProductRequest.getDescription(), result.getDescription());
        assertEquals(0, validProductRequest.getPrice().compareTo(result.getPrice()));
        assertEquals(validProductRequest.getStockQuantity(), result.getStockQuantity());
        assertEquals(validProductRequest.getCategory(), result.getCategory());

        verify(productRepository).findById(productId);
        verify(productRepository).existsByName(validProductRequest.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void testUpdateProduct_ProductNotFound() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(productId, validProductRequest);
        });

        verify(productRepository).findById(productId);
        verify(productRepository, never()).existsByName(anyString());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void testDeleteProduct_Success() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        doNothing().when(productRepository).delete(any(Product.class));

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(productRepository).findById(productId);
        verify(productRepository).delete(existingProduct);
    }

    @Test
    public void testDeleteProduct_ProductNotFound() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(productId);
        });

        verify(productRepository).findById(productId);
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    public void testGetProductById_Success() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // Act
        Product result = productService.getProductById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(existingProduct, result);
        verify(productRepository).findById(productId);
    }

    @Test
    public void testGetProductById_ProductNotFound() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(productId);
        });
        verify(productRepository).findById(productId);
    }

    @Test
    public void testGetAllProducts_Success() {
        // Arrange
        int page = 0;
        int size = 10;
        List<Product> products = new ArrayList<>();
        products.add(existingProduct);

        Page<Product> productPage = new PageImpl<>(products);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        // Act
        PagedResponse<ProductResponse> result = productService.getAllProducts(page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals(products.size(), result.getTotalElements());

        ProductResponse productResponse = result.getContent().get(0);
        assertEquals(existingProduct.getId(), productResponse.getId());
        assertEquals(existingProduct.getName(), productResponse.getName());

        verify(productRepository).findAll(any(Pageable.class));
    }
}