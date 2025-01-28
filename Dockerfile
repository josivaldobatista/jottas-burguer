# Estágio de build: usa Maven e OpenJDK 21 para compilar o projeto
FROM maven:3.9.7-openjdk-21-slim AS build

# Copia o código fonte e o arquivo pom.xml para o diretório de trabalho
COPY src /app/src
COPY pom.xml /app

# Define o diretório de trabalho
WORKDIR /app

# Compila o projeto e gera o arquivo JAR
RUN mvn clean install -DskipTests

# Estágio de execução: usa a imagem slim do OpenJDK 21
FROM openjdk:21-jdk-slim

# Copia o arquivo JAR gerado no estágio de build
COPY --from=build /app/target/jottasburguer-0.0.1-SNAPSHOT.jar /app/app.jar

# Define o diretório de trabalho
WORKDIR /app

# Expõe a porta 8080 (porta padrão do Spring Boot)
EXPOSE 8080

# Comando para rodar a aplicação
CMD ["java", "-jar", "app.jar"]