# ─── Etapa 1: Compilar con Maven ───────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiar pom.xml primero para aprovechar la cache de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copiar codigo fuente y compilar empaquetando el JAR
COPY src ./src
RUN mvn clean package -DskipTests -q

# ─── Etapa 2: Imagen final ligera con JRE 21 Alpine ───────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar solo el JAR resultante de la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8080

EXPOSE 8080

# Arrancar la aplicacion Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
