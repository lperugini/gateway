# API Gateway - PeriziaFacile

## Introduzione

Questo API Gateway gestisce l'instradamento delle richieste ai vari microservizi di PeriziaFacile, tra cui

- **orderservice** per la gestione degli ordini, disponibile al seguente [repository]:(https://github.com/lperugini/orderservice).
- **userservice** per la gestione dei prodotti.
- **itemservice** per la gestione dei prodotti.
- **paymentservice** per la gestione dei pagamenti.

Questa versione rappresenta una demo accademica. Per questo, fatta eccezione per **orderservice**, i servizi descriti sono disponibili come mock all'interno di questo gateway.

## Tecnologie Utilizzate

- **Spring Cloud Gateway** per il routing delle richieste.
- **Spring Boot** per l'infrastruttura del progetto.
- **WebClient** per la comunicazione tra servizi.

## Avvio del Gateway

### 1. Requisiti

- **JDK 17** o superiore
- **Maven 3+**
- **Docker e Docker Compose** (per l'esecuzione in container)

### **2. Configurazione**

Le configurazioni sono definite nel file `application.yml` situato in `src/main/resources`:

```yaml
server:
  port: 8080

spring:
  application:
    name: pfgateway
  rabbitmq:
    host: localhost
    port: 5672
    username: user
    password: password
    template:
      retry:
        enabled: true
        max-attempts: 3
        initial-interval: 1000
        multiplier: 2.0
        max-interval: 5000

jwt:
  expiration: 3600000
  secret: ROWS0BIAS0COIL0WENT0IONS0COED0OWNS0SEND0MARS0LYRA0BAWL0DAY0DEER0IS0BID0OVER0SCAT0WISE0DEAD0HO0LOY0STIR0DADE0ALAN
```

### **3. Costruzione del progetto**

Compila il progetto ed esegui i test con:

```sh
mvn clean package
```

### **4. Esecuzione**

#### **4.1 - Avvio manuale:**

```sh
java -jar target/pfgateway.jar
```

#### **4.2 - Avvio con Docker**

Per un avvio come Docker container è valido il seguente `Dockerfile`:

```dockerfile
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
```

Usa il seguente `docker-compose.yml`:

```yaml
version: "3.8"
services:
  pfgateway:
    build:
      context: .
    container_name: pfgateway
    ports:
      - "8080:8080"
    environment:
      RABBITMQ_HOST: rabbitmq
      RABBITMQ_PORT: 5672
      RABBITMQ_USER: user
      RABBITMQ_PASS: password
```

Avvia tutto con:

```sh
docker-compose up --build
```

## Rotte Disponibili

### **Gestione Utenti**

- **Registrazione:**

  ```http
  POST /auth/register
  ```

  Body JSON:

  ```json
  {
    "username": "utente1",
    "password": "password123"
  }
  ```

- **Login:**
  ```http
  POST /users/login
  ```
  Body JSON:
  ```json
  {
    "username": "utente1",
    "password": "password123"
  }
  ```

### **Gestione Ordini**

- **Creazione ordine:**

  ```http
  POST /orders
  ```

  Header richiesto:

  ```http
  Authorization: Bearer <token>
  ```

  Body JSON:

  ```json
  {
    "userId": "123",
    "items": [{ "id": "456", "quantity": 2 }]
  }
  ```

- **Recupero ordine per ID:**

  ```http
  GET /orders/{orderId}
  ```

  Header richiesto:

  ```http
  Authorization: Bearer <token>
  ```

- **Inserimento nuovo ordine:**

  ```http
  POST /orders/
  ```

  Header richiesto:

  ```http
  Authorization: Bearer <token>
  ```

  Body JSON:

  ```json
    {
        "user": 1,
        "description": "...",
        "price": 9.99,
        "item": 1
    }
  ```

- **Modifica di un ordine esistente:**

  ```http
  PUT /orders/{orderId}
  ```

  Header richiesto:

  ```http
  Authorization: Bearer <token>
  ```

  Body JSON:

  ```json
    {
        "id": 1,
        "user": 1,
        "description": "...",
        "price": 9.99,
        "item": 1
    }
  ```

- **Rimozione di un ordine esistente:**
  ```http
  DELETE /orders/{orderId}
  ```
  Header richiesto:
  ```http
  Authorization: Bearer <token>
  ```

### **Gestione Prodotti**

- **Lista prodotti:**

  ```http
  GET /items
  ```

- **Dettaglio prodotto:**

  ```http
  GET /items/{itemId}
  ```

- **Modifica di un prodotto esistente:**

  ```http
  PUT /items/{orderId}
  ```

  Header richiesto:

  ```http
  Authorization: Bearer <token>
  ```

  Body JSON:

  ```json
    {
        "price": 150.0,
        "name": "...",
        "description": "...",
    }
  ```

- **Rimozione di un prodotto esistente:**

  ```http
  DELETE /items/{orderId}
  ```

  Header richiesto:

  ```http
  Authorization: Bearer <token>
  ```

---
**Autore:** _Leonardo Perugini - leonardo.perugini2@studio.unibo.it_
