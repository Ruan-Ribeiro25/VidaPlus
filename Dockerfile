# --- Etapa 1: Construção (Build) ---
# Usamos uma imagem oficial do Maven com Java 21 para gerar o .jar
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia os arquivos do projeto para dentro do container
COPY . .

# Roda o comando para gerar o .jar (pulando testes para ser rápido)
RUN mvn clean package -DskipTests

# --- Etapa 2: Execução (Run) ---
# Usamos uma imagem leve do Java 21 apenas para rodar a aplicação
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Pega o .jar que foi criado na Etapa 1
COPY --from=build /app/target/*.jar app.jar

# Libera a porta 8080
EXPOSE 8080

# Comando para iniciar o aplicativo
ENTRYPOINT ["java", "-jar", "app.jar"]