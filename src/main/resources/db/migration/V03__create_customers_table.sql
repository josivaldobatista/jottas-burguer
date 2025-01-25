-- V03__create_customers_table.sql
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    address TEXT NOT NULL
);

-- Inserir um cliente de exemplo
INSERT INTO customers (name, email, phone, address)
VALUES (
    'João Silva',
    'joao.silva@example.com',
    '(11) 99999-9999',
    'Rua das Flores, 123 - São Paulo, SP'
);