# Estagio 1: Build (Maven com Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN MAVEN_OPTS="-Xmx512m -XX:MaxMetaspaceSize=256m" mvn clean package -DskipTests

# Estagio 2: Runtime (Amazon Corretto JDK 21 - Leve sobre Alpine)
FROM amazoncorretto:21-alpine
WORKDIR /app

# Copia o arquivo compilado (.jar)
COPY --from=build /app/target/*.jar app.jar

# Move a pasta wallet do build para a raiz de execução
COPY --from=build /app/src/main/resources/wallet /app/wallet

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]