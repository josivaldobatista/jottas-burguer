-- Cria a tabela users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    hashed_password VARCHAR(255) NOT NULL
);

-- Cria a tabela user_roles
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    roles VARCHAR(50) NOT NULL, -- Coluna renomeada para "roles"
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- Adiciona o usuário admin
INSERT INTO users (email, hashed_password)
VALUES ('admin@jottasburguer.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a'); -- Senha: "admin" criptografada

-- Adiciona a role ADMIN para o usuário admin
INSERT INTO user_roles (user_id, roles)
VALUES (
    (SELECT id FROM users WHERE email = 'admin@jottasburguer.com'), -- Obtém o ID do usuário admin
    'ADMIN'
);