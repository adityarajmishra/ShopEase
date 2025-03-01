-- Insert admin user (password: admin123)
INSERT INTO users (name, email, password, role, created_at, updated_at, version)
VALUES ('Admin User', 'admin@shopease.com',
        '$2a$10$mMSn8giNzLgdJo0.GH/COeapZw1BiLXeZ1XNTbp4oD9bddA7Vz44C',
        'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- Insert demo user (password: user123)
INSERT INTO users (name, email, password, role, created_at, updated_at, version)
VALUES ('Demo User', 'user@shopease.com',
        '$2a$10$CfLv/BWu1Agg5lX58elJzeWRXcO4OCqEFUe5VM0IBtg3pXKOLm4Gy',
        'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- Insert sample products
INSERT INTO products (name, description, price, stock_quantity, category, status, created_at, updated_at, version)
VALUES
    ('Smartphone X', 'Latest smartphone with advanced features', 899.99, 50, 'Electronics', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Laptop Pro', 'Professional laptop for developers', 1299.99, 30, 'Electronics', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Wireless Headphones', 'Premium noise-cancelling headphones', 249.99, 100, 'Electronics', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Sports Shoes', 'Comfortable running shoes for athletes', 89.99, 200, 'Footwear', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Coffee Maker', 'Automatic coffee maker with timer', 59.99, 80, 'Home Appliances', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- Insert sample discount code
INSERT INTO discounts (code, percentage, start_date, expiry_date, max_usage, current_usage, created_at, updated_at, version)
VALUES ('WELCOME10', 10.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 100, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);