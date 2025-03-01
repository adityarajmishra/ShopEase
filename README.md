# ShopEase E-Commerce Backend

A robust and scalable e-commerce backend system built with Spring Boot, following advanced Java concepts, OOP principles, and security best practices.

## Features

- **User Management & Authentication**
    - Secure user registration and login
    - JWT-based authentication
    - Role-based access control (USER, ADMIN)

- **Product Management**
    - CRUD operations for products
    - Stock tracking
    - Search and filter products

- **Shopping Cart**
    - Add/remove/update items
    - Cart persistence between sessions
    - Automatic cart expiry

- **Order Processing**
    - Checkout process
    - Order status management
    - Order history tracking

- **Discount System**
    - Discount code creation
    - Validation rules (expiry, usage limits)
    - Apply discounts at checkout

- **Payment Processing**
    - Payment simulation
    - Order confirmation
    - Payment status tracking

## Technologies Used

- **Spring Boot** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data access layer
- **PostgreSQL** - Database
- **Flyway** - Database migrations
- **JWT** - Stateless authentication
- **JUnit & Mockito** - Testing framework
- **Swagger/OpenAPI** - API documentation

## Architecture

The application follows a layered architecture:

- **Controller Layer** - REST API endpoints
- **Service Layer** - Business logic
- **Repository Layer** - Data access
- **Model Layer** - Domain model

Advanced concepts implemented:
- **Object-Oriented Design** - Inheritance, encapsulation, polymorphism
- **Design Patterns** - Factory, State, Strategy
- **Event-Driven Communication** - Decoupled components
- **Transaction Management** - ACID properties
- **Optimistic Locking** - Concurrent access control


## Project Structure

```
com.shopease/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── shopease/
│   │   │           ├── ShopEaseApplication.java
│   │   │           ├── config/
│   │   │           │   ├── SecurityConfig.java
│   │   │           │   ├── JpaConfig.java
│   │   │           │   ├── WebConfig.java
│   │   │           │   └── SwaggerConfig.java
│   │   │           ├── controller/
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── ProductController.java
│   │   │           │   ├── CartController.java
│   │   │           │   ├── OrderController.java
│   │   │           │   └── DiscountController.java
│   │   │           ├── dto/
│   │   │           │   ├── request/
│   │   │           │   │   ├── LoginRequest.java
│   │   │           │   │   ├── RegisterRequest.java
│   │   │           │   │   ├── ProductRequest.java
│   │   │           │   │   ├── CartItemRequest.java
│   │   │           │   │   └── DiscountRequest.java
│   │   │           │   └── response/
│   │   │           │       ├── JwtAuthResponse.java
│   │   │           │       ├── ApiResponse.java
│   │   │           │       ├── ProductResponse.java
│   │   │           │       ├── CartResponse.java
│   │   │           │       └── OrderResponse.java
│   │   │           ├── exception/
│   │   │           │   ├── ResourceNotFoundException.java
│   │   │           │   ├── ApiError.java
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   └── ... (other exception classes)
│   │   │           ├── model/
│   │   │           │   ├── BaseEntity.java
│   │   │           │   ├── User.java
│   │   │           │   ├── Product.java
│   │   │           │   ├── Cart.java
│   │   │           │   ├── CartItem.java
│   │   │           │   ├── Order.java
│   │   │           │   ├── OrderItem.java
│   │   │           │   ├── Discount.java
│   │   │           │   └── PaymentRecord.java
│   │   │           ├── repository/
│   │   │           │   ├── UserRepository.java
│   │   │           │   ├── ProductRepository.java
│   │   │           │   ├── CartRepository.java
│   │   │           │   ├── CartItemRepository.java
│   │   │           │   ├── OrderRepository.java
│   │   │           │   ├── OrderItemRepository.java
│   │   │           │   ├── DiscountRepository.java
│   │   │           │   └── PaymentRepository.java
│   │   │           ├── security/
│   │   │           │   ├── JwtTokenProvider.java
│   │   │           │   ├── JwtAuthenticationFilter.java
│   │   │           │   ├── UserPrincipal.java
│   │   │           │   └── CurrentUser.java
│   │   │           ├── service/
│   │   │           │   ├── UserService.java
│   │   │           │   ├── ProductService.java
│   │   │           │   ├── CartService.java
│   │   │           │   ├── OrderService.java
│   │   │           │   ├── DiscountService.java
│   │   │           │   ├── PaymentService.java
│   │   │           │   └── impl/
│   │   │           │       ├── UserServiceImpl.java
│   │   │           │       ├── ProductServiceImpl.java
│   │   │           │       ├── CartServiceImpl.java
│   │   │           │       ├── OrderServiceImpl.java
│   │   │           │       ├── DiscountServiceImpl.java
│   │   │           │       └── PaymentServiceImpl.java
│   │   │           ├── util/
│   │   │           │   ├── AppConstants.java
│   │   │           │   └── ModelMapper.java
│   │   │           └── event/
│   │   │               ├── CartExpiredEvent.java
│   │   │               ├── OrderCreatedEvent.java
│   │   │               ├── OrderCompletedEvent.java
│   │   │               └── EventListeners.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── db/
│   │           └── migration/
│   │               ├── V1__Initial_Schema.sql
│   │               └── V2__Initial_Data.sql
│   └── test/
│       └── java/
│           └── com/
│               └── shopease/
│                   ├── controller/
│                   ├── service/
│                   └── repository/
└── build.gradle
```

## API Endpoints

### Authentication
- `POST /auth/register` - Register a new user
- `POST /auth/login` - Log in and get JWT token

### Products
- `GET /products` - Get all products
- `GET /products/{id}` - Get a product by ID
- `POST /products` - Create a product (admin)
- `PUT /products/{id}` - Update a product (admin)
- `DELETE /products/{id}` - Delete a product (admin)

### Cart
- `GET /cart` - Get user's cart
- `POST /cart` - Add item to cart
- `PUT /cart/items/{itemId}` - Update cart item quantity
- `DELETE /cart/items/{itemId}` - Remove item from cart

### Orders
- `POST /orders` - Create an order from cart
- `GET /orders` - Get user's orders
- `GET /orders/{orderId}` - Get an order by ID
- `POST /orders/{orderId}/payment` - Process payment for an order
- `PUT /orders/{orderId}/cancel` - Cancel an order

### Discounts
- `POST /discounts` - Create a discount code (admin)
- `GET /discounts` - Get all discount codes (admin)
- `GET /discounts/active` - Get active discount codes
- `GET /discounts/validate/{code}` - Validate a discount code

## Setup and Installation

### Prerequisites
- JDK 17+
- Maven or Gradle
- PostgreSQL

### Configuration
Update `application.properties` with your database connection details:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shopease
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Running the Application

```bash
# Clone the repository
git clone https://github.com/yourusername/shopease.git
cd shopease

# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The application will be accessible at http://localhost:8080/api

Swagger documentation will be available at http://localhost:8080/api/swagger-ui

## Testing

```bash
# Run tests
./mvnw test

# Generate test coverage report
./mvnw verify
```

## API Security

All endpoints except for:
- Authentication endpoints
- Public product listings
- API documentation

Require JWT authentication with the token provided in the `Authorization` header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.