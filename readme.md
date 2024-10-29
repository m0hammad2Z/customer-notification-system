# Customer & Notification System

## 🌟 Key Features & Advanced Implementation Highlights

### 1. Event-Driven Architecture with Kafka
- **Advanced Event Processing**: Asynchronous communication between microservices using Kafka
- **Custom Event Handling**: `@Notifiable` annotation for automatic event triggering
- **Parallel Processing**: 5 concurrent threads for notification dispatch
- **Real-time Updates**: WebSocket integration for instant notification delivery

### 2. Advanced Search Capabilities
- **Dynamic Query Builder**
    - Type-safe query construction using JPA Criteria API
    - Complex search conditions
    - Automatic join handling for related entities
    - Custom result projections and dynamic sorting

### 3. Sophisticated Domain Model
- **Smart Inheritance Structure**
    - Joined table inheritance for customer types (Individual/Organization)
    - Efficient relationship management with bidirectional mappings
    - Advanced validation constraints per customer type

### 4. Performance Optimizations
- **Intelligent Caching System**
    - Strategic cache implementation for frequently accessed data
    - Transaction-aware caching
- **Lazy Loading Strategy**
    - Optimized data fetching

## 🚀 Quick Start Guide

### Prerequisites
- Java 17+
- Maven
- Docker (for Kafka & Zookeeper)

### Setup Steps

1. **Start Infrastructure**
```bash
docker-compose up -d
```

2. **Run Microservices**
```bash
# Customer Service (Port 8080)
cd customer && mvn spring-boot:run

# Notification Service (Port 8082)
cd notification && mvn spring-boot:run
```

3. **Access Points**
- Swagger UI: `http://localhost:8080/swagger-ui/`
- WebSocket Client: Open `index.html` in browser
- Health Check: `http://localhost:8080/actuator/health`

## 🏗 Architecture Overview

### Customer Microservice
```
customer/
├── aop/             # Aspect-oriented programming classes
├── config/          # Configuration classes
├── controller/      # REST API endpoints
├── dto/             # Data Transfer Objects
    ├── domain/      # Domain-specific DTOs
    ├── mapper/      # Model mapping classes
    ├── search/      # Search criteria classes
    ├── security/    # Security-related DTOs
├── service/         # Business logic layer
    ├── security/         # Service implementation classes
    ├── validation/  # Business rule validation
    ...services
├── security/        # Security related classes
└── config/          # Service configurations
├── domain/        # Entities and domain-specific enums
    ├── entity/     # JPA entities
    ├── enumeration/ # Custom enums
├── repository/    # Spring Data JPA repositories
    ├── custom/     # Custom repository methods
    ├── impl/         # Repository implementation classes using Criteria API
    ├── specification/ # JPA Specification classes
    ...repositories
├── exception/     # Custom exceptions
        GlobalExceptionHandler.java # Global exception handler
        ...exceptions
```

### Notification Microservice
```
com.digitinarytask.notification/
├── config/        # Configuration classes
├── domain/        # Notification entity and enums
├── event/         # Event listeners and handlers
├── repository/    # Notification repository
└── service/       # Notification processing logic
```

## 💡 Advanced Technical Features

### 1. Custom Annotation: @Notifiable
```java
@Notifiable(type = NotificationType.CUSTOMER_CREATED)
public Customer createCustomer(CustomerDTO dto) {
    // Method implementation
}
```
- Automatically triggers events on method execution
- Supports transaction synchronization
- Configurable notification parameters

### 2. Dynamic Search Example
```java
AccountSearchDTO search = AccountSearchDTO.builder()
            .accountNumber("123456789")
            .accountType(AccountType.CURRENT)
            .customerId(2)
            .page(0)
            .size(10)
            .sortBy("id")
            .sortDirection("asc")
            .build();

Page<AccountDTO> results = accountService.search(search);
```

### 3. WebSocket Integration
- SockJS fallback support
- Session management
- Real-time notification dispatch

### 4. Advanced Caching
```java
@Cacheable(value = "customers", key = "#id")
public Customer getCustomer(Long id) {
    // Method implementation
}
```

## 🛡 Security Features

- JWT-based authentication
- Input validation and sanitization


## 🧪 Testing

### Comprehensive Test Coverage
- Unit tests



## 📚 Additional Resources

- API Documentation: Available via Swagger UI
- Client Examples: WebSocket test client included, Open `index.html` in browser to view real-time notifications.


## 📝 License
Licensed under the MIT License.
