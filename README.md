# Aggregator Microservice Application

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?logo=springboot)
![Gradle](https://img.shields.io/badge/Gradle-7%2B-blue?logo=gradle)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen)
![Architecture](https://img.shields.io/badge/Architecture-Microservices-blueviolet)

## Overview
A Spring Boot-based microservices application consisting of an **Aggregator service** that asynchronously polls multiple **Producer services** to retrieve user transaction data. The Aggregator service caches responses and provides aggregated transaction data to clients through a unified API.
This project was completed as part of the [Hyperskill](https://hyperskill.org/projects/424) educational project.

## Architecture

### Service Components:
1. **Aggregator Service** (`com.example.aggregator`)
   - Main service that clients interact with
   - Asynchronously calls producer services
   - Caches transaction data
   - Provides aggregated response

2. **Producer 1 Service** (`com.example.producer1`)
   - Mock transaction service with simulated failures
   - Returns transactions for a given account ID

3. **Producer 2 Service** (`com.example.producer2`)
   - Second mock transaction service
   - Similar functionality to Producer 1

### Technology Stack:
- **Spring Boot** - Application framework
- **Spring Web** - REST API implementation
- **Database** - Embedded SQLite (`.db` files)
- **Gradle** - Build tool and dependency management

## Project Structure

```
FileServerCLI/
├── src/main/java/com/example/
│   ├── aggregator/
│   │   ├── AggregatorApplication.java      # Aggregator main class
│   │   ├── AggregatorController.java       # REST controller for aggregator
│   │   ├── AggregatorService.java          # Business logic for aggregation
│   │   ├── ExternalApiService.java         # HTTP client for calling producers
│   │   ├── Transaction.java                # Aggregator transaction model
│   │   ├── TransactionDao.java             # Data access for transactions
│   │   └── TransactionService.java         # Transaction management service
│   ├── producer1/
│   │   ├── Producer1Application.java       # Producer 1 main class
│   │   ├── Producer1Controller.java        # REST controller for producer 1
│   │   ├── Producer1Repository.java        # Repository for producer 1 data
│   │   └── Transaction1.java               # Producer 1 transaction model
│   └── producer2/
│       ├── Producer2Application.java       # Producer 2 main class
│       ├── Producer2Controller.java        # REST controller for producer 2
│       ├── Producer2Repository.java        # Repository for producer 2 data
│       └── Transaction2.java               # Producer 2 transaction model
├── src/main/resources/
│   ├── application-aggregator.properties   # Aggregator configuration
│   ├── application-producer1.properties    # Producer 1 configuration
│   ├── application-producer2.properties    # Producer 2 configuration
│   ├── producer1.db                        # Producer 1 SQLite database
│   └── producer2.db                        # Producer 2 SQLite database
├── build.gradle                            # Gradle build configuration
├── settings.gradle                         # Gradle settings
└── ... other configuration files
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Gradle 7+

### Building the Application

```bash
# Build all services
./gradlew build

# Build specific service
./gradlew :aggregator:build
./gradlew :producer1:build
./gradlew :producer2:build
```

### Running the Services

You need to run each service in a separate terminal:

#### Terminal 1 - Producer 1:
```bash
./gradlew :producer1:bootRun
# Or using the Spring Boot plugin
java -jar build/libs/producer1-0.0.1-SNAPSHOT.jar
```

#### Terminal 2 - Producer 2:
```bash
./gradlew :producer2:bootRun
# Or using the Spring Boot plugin
java -jar build/libs/producer2-0.0.1-SNAPSHOT.jar
```

#### Terminal 3 - Aggregator:
```bash
./gradlew :aggregator:bootRun
# Or using the Spring Boot plugin
java -jar build/libs/aggregator-0.0.1-SNAPSHOT.jar
```

### Default Ports:
- **Aggregator**: `8080` (configurable in `application-aggregator.properties`)
- **Producer 1**: `8088` (configurable in `application-producer1.properties`)
- **Producer 2**: `8089` (configurable in `application-producer2.properties`)

## API Documentation

### Producer Service APIs

#### Health Check:
```http
GET http://localhost:8088/ping
```
**Response:** `Pong from server #`

#### Get Transactions:
```http
GET http://localhost:8088/transactions?account={accountId}
```
**Behavior:**
- Randomly returns one of three responses:
  1. `503 SERVICE_UNAVAILABLE`
  2. `429 TOO_MANY_REQUESTS`
  3. `200 OK` with transaction list

**Example Response:**
```json
[
{
    "id":"31969aef-ffbe-413a-8a94-bc920556a0d4",
    "serverId":"server-01",
    "account":"02248",
    "amount":"5120",
    "timestamp":"2023-12-24T00:02:31"
  }
]
```

### Aggregator Service API

#### Aggregate Transactions:
```http
GET http://localhost:8080/aggregate?account={accountId}
```
**Functionality:**
- Asynchronously calls all producer services
- Handles failed responses gracefully
- Caches successful responses
- Returns aggregated list of all transactions

**Example Response:**
```json
[
  {
    "id":"31969aef-ffbe-413a-8a94-bc920556a0d4",
    "serverId":"server-01",
    "account":"02248",
    "amount":"5120",
    "timestamp":"2023-12-24T00:02:31"
  },
  {
    "id":"dcc57df0-d815-497f-be1d-b3fb419b9bee",
    "serverId":"server-02",
    "account":"02248",
    "amount":"2",
    "timestamp":"2023-12-21T01:23:31"
  }
]
```
## Key Features

### 1. **Asynchronous Polling**
- Aggregator calls producer services asynchronously
- Non-blocking I/O for better performance
- Parallel processing of multiple producers

### 2. **Resilience Patterns**
- **Circuit Breaker**: Prevents cascading failures
- **Retry Logic**: Automatic retry for failed requests
- **Fallback**: Returns cached data when producers fail
- **Timeout**: Configurable timeout for external calls

### 3. **Caching Strategy**
- In-memory caching of transaction data
- Time-based eviction (TTL)
- Per-user caching for personalized responses
- Cache invalidation on update operations

### 4. **Error Handling**
- Graceful degradation when producers fail
- Partial responses with available data
- Detailed error logging
- Health check endpoints

## Design Patterns Used

1. **Aggregator Pattern**: Combines data from multiple sources
2. **Circuit Breaker Pattern**: Prevents system overload
3. **Repository Pattern**: Data access abstraction
4. **Service Layer Pattern**: Business logic separation
5. **DTO Pattern**: Data transfer between services
