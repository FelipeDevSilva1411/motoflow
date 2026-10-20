# Estágio 1: Compilação do código Java usando Maven
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copia o arquivo de dependências para realizar o cache do Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e compila gerando o .jar executável
COPY src ./src
RUN mvn package -DskipTests

# Estágio 2: Execução leve em ambiente JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia apenas o .jar gerado no Estágio 1
COPY --from=builder /app/target/*.jar app.jar

# Render define a porta dinamicamente usando a variável de ambiente $PORT
ENV PORT=8080
EXPOSE 8080

# Execução da aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]