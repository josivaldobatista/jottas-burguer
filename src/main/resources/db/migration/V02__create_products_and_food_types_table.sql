-- V1__create_products_and_food_types_table.sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    ingredients TEXT NOT NULL, -- Novo campo
    description TEXT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255), -- Novo campo (pode ser nulo)
    food_type_id BIGINT NOT NULL -- Novo campo
);

CREATE TABLE food_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO food_types (name, description, image_url)
VALUES
    ('Burgers', 'Deliciosos hambúrgueres artesanais', 'https://example.com/images/burguer.jpg'),
    ('Lanches', 'Deliciosos lanches e Hot-dogs e outros', 'https://example.com/images/lanches.jpg'),
    ('Sobremesas', 'Doces e sobremesas para fechar com chave de ouro', 'https://example.com/images/sobremesas.jpg'),
    ('Bebidas', 'Refrigerantes, sucos e cervejas', 'https://example.com/images/bebidas.jpg'),
    ('Acompanhamentos', 'Porções para acompanhar seu lanche', 'https://example.com/images/acompanhamentos.jpg'),
    ('Porções', 'Porções grandes para compartilhar', 'https://example.com/images/porcoes.jpg'),
    ('Combos', 'Combos completos com lanche, bebida e acompanhamento', 'https://example.com/images/combos.jpg');