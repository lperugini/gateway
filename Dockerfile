# Fase 1: Costruzione del JAR
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copia del file pom.xml e delle dipendenze Maven
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia del codice sorgente e build del progetto
COPY src ./src
RUN mvn clean package -DskipTests

# Fase 2: Creazione dell'immagine per l'applicazione
FROM openjdk:17
WORKDIR /app

# Copia del JAR generato nella fase precedente
COPY --from=build /app/target/*.jar app.jar

# Comando di avvio
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
