-- Cria a tabela users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    hashed_password VARCHAR(255) NOT NULL
);

-- Cria a tabela user_roles
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE (user_id, role)
);

-- Adiciona o usuário admin
INSERT INTO users (email, hashed_password)
VALUES ('admin@jottasburguer.com', '$2a$10$lhbW9qrnJgUOFhRZuyBbc.odwbzdrlw.cnQ8QESrHu5bzVlZjYO2e'); -- Senha: "senha123" criptografada

-- Adiciona a role ADMIN para o usuário admin
INSERT INTO user_roles (user_id, role)
VALUES (
    (SELECT id FROM users WHERE email = 'admin@jottasburguer.com'),
    'ADMIN'
);