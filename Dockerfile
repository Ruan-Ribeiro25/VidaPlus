# --- Etapa 1: Construção (Build) ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar o cache do Docker (baixa dependências mais rápido)
COPY pom.xml .
COPY src ./src

# Gera o arquivo .jar (pulando testes para agilidade no deploy)
RUN mvn clean package -DskipTests

# --- Etapa 2: Execução (Run) ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o jar gerado na etapa anterior
COPY --from=build /app/target/vidaplus.jar app.jar

# Expõe a porta
EXPOSE 8080

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]