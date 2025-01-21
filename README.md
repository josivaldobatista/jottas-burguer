# Jottas Burguer 🍔

Bem-vindo ao repositório da **Jottas Burguer**, uma aplicação moderna desenvolvida em **Spring Boot** com **Kotlin** para gerenciar pedidos, cardápio e clientes de uma hamburgueria. Este projeto utiliza as melhores práticas de desenvolvimento de APIs, como arquitetura em camadas, DTOs, validações, migrações de banco de dados com **Flyway** e documentação automática com **SpringDoc OpenAPI**.

---

## **Tecnologias Utilizadas**

- **Linguagem**: Kotlin
- **Framework**: Spring Boot
- **Banco de Dados**: PostgreSQL
- **Migrações**: Flyway
- **Containerização**: Docker
- **Gerenciamento de Dependências**: Maven
- **Testes**: JUnit 5 (para testes unitários e de integração)
- **Documentação da API**: SpringDoc OpenAPI (Swagger UI)
- **Logging**: Logback (integrado com Spring Boot)
- **Validações**: Bean Validation (Hibernate Validator)

---

## **Estrutura do Projeto**

A aplicação está organizada em camadas, seguindo boas práticas de arquitetura de software. Aqui está a estrutura de diretórios:

```plaintext
src/main/kotlin/com/jfb/jottasburguer
├── config
│   ├── AppConfig.kt
│   └── SecurityConfig.kt (opcional, se você adicionar Spring Security)
├── controller
├── service
├── repository
├── model
│   ├── entity
│   ├── dto
│   └── exception
│       ├── ErrorResponse.kt
│       └── GlobalExceptionHandler.kt
├── migration (se você adicionou Flyway ou Liquibase)
│   └── V1__Create_Tables.sql
└── JottasBurguerApplication.kt
```


---

## **Como Executar o Projeto**

### **Pré-requisitos**

- **Java 21**
- **Maven**
- **Docker** (opcional, para rodar o PostgreSQL em um contêiner)
- **PostgreSQL** (se não estiver usando Docker)

### **Passos para Execução**

1. **Clone o repositório**:
   ```bash
   git clone https://github.com/seu-usuario/jottas-burguer.git
   cd jottas-burguer

Se estiver usando Docker, inicie o contêiner do PostgreSQL:

bash
Copy
docker-compose up -d

### **Acesse a API**
A API estará disponível em http://localhost:8080.

Acesse a documentação da API (Swagger UI) em: http://localhost:8080/swagger-ui.html