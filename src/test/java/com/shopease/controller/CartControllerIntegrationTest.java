package com.shopease.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopease.dto.request.AddToCartRequest;
import com.shopease.dto.request.UpdateCartItemRequest;
import com.shopease.model.Cart;
import com.shopease.model.CartItem;
import com.shopease.model.Product;
import com.shopease.model.User;
import com.shopease.repository.CartItemRepository;
import com.shopease.repository.CartRepository;
import com.shopease.repository.ProductRepository;
import com.shopease.repository.UserRepository;
import com.shopease.security.JwtTokenProvider;
import com.shopease.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Product testProduct;
    private String jwtToken;

    @BeforeEach
    public void setup() {
        // Create test user
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("testcart@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRole(User.Role.USER);
        testUser = userRepository.save(testUser);

        // Create test product
        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStockQuantity(10);
        testProduct.setCategory("Electronics");
        testProduct.setStatus(Product.ProductStatus.ACTIVE);
        testProduct = productRepository.save(testProduct);

        // Generate JWT token for authentication
        // Note: In a real test, you would use TestRestTemplate and perform a proper login
        // This is a simplified approach for demonstration
        jwtToken = "Bearer " + jwtTokenProvider.generateToken(testUser.getId());
    }

    @Test
    public void testGetUserCart_EmptyCart() throws Exception {
        mockMvc.perform(get("/cart")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(testUser.getId()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.totalAmount").value(0))
                .andExpect(jsonPath("$.itemCount").value(0));
    }

    @Test
    public void testAddItemToCart() throws Exception {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(testProduct.getId());
        request.setQuantity(2);

        mockMvc.perform(post("/cart")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(testUser.getId()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId").value(testProduct.getId()))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.totalAmount").value(199.98)) // 99.99 * 2
                .andExpect(jsonPath("$.itemCount").value(1));
    }

    @Test
    public void testUpdateCartItemQuantity() throws Exception {
        // First add an item to the cart
        Cart cart = new Cart();
        cart.setUser(testUser);
        cart.setLastAccessed(LocalDateTime.now());
        cart = cartRepository.save(cart);

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);
        cartItem.setAddedAt(LocalDateTime.now());
        cartItem = cartItemRepository.save(cartItem);

        // Now update the quantity
        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setQuantity(5);

        mockMvc.perform(put("/cart/items/" + cartItem.getId())
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].quantity").value(5))
                .andExpect(jsonPath("$.totalAmount").value(499.95)); // 99.99 * 5
    }

    @Test
    public void testRemoveItemFromCart() throws Exception {
        // First add an item to the cart
        Cart cart = new Cart();
        cart.setUser(testUser);
        cart.setLastAccessed(LocalDateTime.now());
        cart = cartRepository.save(cart);

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);
        cartItem.setAddedAt(LocalDateTime.now());
        cartItem = cartItemRepository.save(cartItem);

        // Now remove the item
        mockMvc.perform(delete("/cart/items/" + cartItem.getId())
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.totalAmount").value(0))
                .andExpect(jsonPath("$.itemCount").value(0));
    }
}