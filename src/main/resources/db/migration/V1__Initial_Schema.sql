-- Base tables

-- Users Table
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,
                       version BIGINT NOT NULL DEFAULT 0
);

-- Products Table
CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL UNIQUE,
                          description TEXT NOT NULL,
                          price DECIMAL(10, 2) NOT NULL CHECK (price > 0),
                          stock_quantity INTEGER NOT NULL CHECK (stock_quantity >= 0),
                          category VARCHAR(100) NOT NULL,
                          status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL,
                          version BIGINT NOT NULL DEFAULT 0
);

-- Carts Table
CREATE TABLE carts (
                       id BIGSERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                       last_accessed TIMESTAMP NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,
                       version BIGINT NOT NULL DEFAULT 0
);

-- Cart Items Table
CREATE TABLE cart_items (
                            id BIGSERIAL PRIMARY KEY,
                            cart_id BIGINT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
                            product_id BIGINT NOT NULL REFERENCES products(id),
                            quantity INTEGER NOT NULL CHECK (quantity > 0),
                            added_at TIMESTAMP NOT NULL,
                            created_at TIMESTAMP NOT NULL,
                            updated_at TIMESTAMP NOT NULL,
                            version BIGINT NOT NULL DEFAULT 0,
                            UNIQUE (cart_id, product_id)
);

-- Discounts Table
CREATE TABLE discounts (
                           id BIGSERIAL PRIMARY KEY,
                           code VARCHAR(50) NOT NULL UNIQUE,
                           percentage DECIMAL(5, 2) NOT NULL CHECK (percentage >= 0 AND percentage <= 100),
                           start_date TIMESTAMP NOT NULL,
                           expiry_date TIMESTAMP NOT NULL,
                           max_usage INTEGER NOT NULL,
                           current_usage INTEGER NOT NULL DEFAULT 0,
                           created_at TIMESTAMP NOT NULL,
                           updated_at TIMESTAMP NOT NULL,
                           version BIGINT NOT NULL DEFAULT 0,
                           CHECK (start_date < expiry_date)
);

-- Orders Table
CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL REFERENCES users(id),
                        total_price DECIMAL(10, 2) NOT NULL,
                        discount_amount DECIMAL(10, 2),
                        final_price DECIMAL(10, 2) NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        discount_id BIGINT REFERENCES discounts(id),
                        order_date TIMESTAMP NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL,
                        version BIGINT NOT NULL DEFAULT 0
);

-- Order Items Table
CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                             product_id BIGINT NOT NULL REFERENCES products(id),
                             quantity INTEGER NOT NULL CHECK (quantity > 0),
                             price DECIMAL(10, 2) NOT NULL,
                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP NOT NULL,
                             version BIGINT NOT NULL DEFAULT 0
);

-- Payment Records Table
CREATE TABLE payment_records (
                                 id BIGSERIAL PRIMARY KEY,
                                 order_id BIGINT NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
                                 amount DECIMAL(10, 2) NOT NULL,
                                 status VARCHAR(20) NOT NULL,
                                 payment_date TIMESTAMP NOT NULL,
                                 transaction_reference VARCHAR(100),
                                 created_at TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP NOT NULL,
                                 version BIGINT NOT NULL DEFAULT 0
);

-- Indexes
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_date ON orders(order_date);
CREATE INDEX idx_discounts_code ON discounts(code);
CREATE INDEX idx_discounts_expiry_date ON discounts(expiry_date);