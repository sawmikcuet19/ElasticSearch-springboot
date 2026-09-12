# Elasticsearch + Spring Boot

A complete CRUD and advanced search application built with **Spring Boot 4.1.1** and **Elasticsearch 9.4.5**, featuring JWT authentication, full-text search, fuzzy matching, wildcard queries, geospatial queries, aggregations, and more.

![Java](https://img.shields.io/badge/Java-25-orange?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-green?style=flat-square&logo=springboot&logoColor=white)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-9.4.5-yellow?style=flat-square&logo=elasticsearch&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Authentication Flow](#authentication-flow)
- [API Endpoints](#api-endpoints)
- [Elasticsearch Features](#elasticsearch-features)
- [Entity Relationship](#entity-relationship)
- [Configuration](#configuration)
- [License](#license)

---

## Architecture Overview

```mermaid
flowchart TD
    subgraph CLIENT["🌐 Client"]
        A[HTTP Requests]
    end

    subgraph SECURITY["🔒 Security Layer"]
        B[JWT Authentication Filter]
        C[Spring Security Filter Chain]
    end

    subgraph CONTROLLER["📡 Controller Layer"]
        D[AuthController]
        E[ProductController]
        F[ArticleController]
        G[LogController]
        H[LocationController]
        I[UserController]
    end

    subgraph SERVICE["⚙️ Service Layer"]
        J[AuthService]
        K[ProductService]
        L[ProductSearchService]
        M[ProductIndexService]
        N[ArticleService]
        O[ArticleSearchService]
        P[LogService]
        Q[LogSearchService]
        R[LocationService]
        S[LocationSearchService]
    end

    subgraph REPOSITORY["📦 Repository Layer"]
        T[ProductRepository]
        U[ArticleRepository]
        V[LogEntryRepository]
        W[LocationRepository]
        X[UserRepository]
    end

    subgraph ELASTICSEARCH["🔍 Elasticsearch"]
        Y[(ES Cluster 9.4.5)]
    end

    A --> B --> C
    C --> D & E & F & G & H & I
    D --> J
    E --> K & L & M
    F --> N & O
    G --> P & Q
    H --> R & S
    K & L & M --> T
    N & O --> U
    P & Q --> V
    R & S --> W
    J --> X
    T & U & V & W & X --> Y

    style CLIENT fill:#e3f2fd,stroke:#1565c0,stroke-width:2px,color:#000
    style SECURITY fill:#fce4ec,stroke:#c62828,stroke-width:2px,color:#000
    style CONTROLLER fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
    style SERVICE fill:#fff3e0,stroke:#e65100,stroke-width:2px,color:#000
    style REPOSITORY fill:#f3e5f5,stroke:#6a1b9a,stroke-width:2px,color:#000
    style ELASTICSEARCH fill:#fffde7,stroke:#f9a825,stroke-width:2px,color:#000
```

---

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 25 | Programming language |
| Spring Boot | 4.1.1 | Application framework |
| Spring Data Elasticsearch | 6.1.1 | ES data access |
| Elasticsearch Java Client | 9.4.5 | Native ES queries |
| Spring Security | 6.x | Authentication & authorization |
| JJWT | 0.12.6 | JWT token generation |
| Lombok | Latest | Boilerplate reduction |
| Elasticsearch | 9.4.5 | Search engine |
| Docker | Latest | Container runtime |

---

## Prerequisites

- **Java 25+** (JDK)
- **Maven 3.9+**
- **Docker & Docker Compose** (for Elasticsearch)

---

## Getting Started

### 1. Start Elasticsearch

```bash
docker-compose up -d
```

This starts a single-node Elasticsearch 9.4.5 instance on port `9200`.

### 2. Build the Project

```bash
./mvnw clean install
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The app starts on **http://localhost:8080**.

### 4. Quick Test

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","email":"admin@test.com","password":"password123","role":"ADMIN"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

---

## Project Structure

```
src/main/java/com/sawmik/elastic_search/
├── config/                          # Configuration classes
│   ├── AppConfig.java               # CORS & JWT config properties
│   ├── GlobalExceptionHandler.java  # Centralized exception handling
│   └── SecurityConfig.java          # Spring Security configuration
│
├── controller/                      # REST API controllers
│   ├── AuthController.java          # Register & Login
│   ├── UserController.java          # User management
│   ├── product/
│   │   ├── ProductController.java       # CRUD operations
│   │   ├── ProductSearchController.java # Search & queries
│   │   └── ProductAdminController.java  # Index management
│   ├── article/
│   │   ├── ArticleController.java       # CRUD operations
│   │   └── ArticleSearchController.java # Search & queries
│   ├── log/
│   │   ├── LogController.java           # CRUD operations
│   │   └── LogSearchController.java     # Search & aggregations
│   └── location/
│       ├── LocationController.java      # CRUD operations
│       └── LocationSearchController.java# Geo queries
│
├── dto/                             # Data Transfer Objects
│   ├── auth/    (AuthResponse, LoginRequest, RegisterRequest)
│   ├── user/    (UserResponse)
│   ├── product/ (ProductRequest, ReviewRequest)
│   └── location/(LocationRequest, Coordinates)
│
├── entity/                          # Elasticsearch document entities
│   ├── Product.java
│   ├── Article.java
│   ├── LogEntry.java
│   ├── Location.java
│   ├── User.java
│   └── product/Review.java
│
├── repository/                      # Spring Data Elasticsearch repositories
│   ├── ProductRepository.java
│   ├── ArticleRepository.java
│   ├── LogEntryRepository.java
│   ├── LocationRepository.java
│   └── UserRepository.java
│
├── security/                        # Security components
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtTokenProvider.java
│
├── service/                         # Business logic
│   ├── AuthService.java
│   ├── product/ (ProductService, ProductSearchService, ProductIndexService)
│   ├── article/ (ArticleService, ArticleSearchService)
│   ├── log/     (LogService, LogSearchService)
│   └── location/(LocationService, LocationSearchService)
│
└── ElasticSearchApplication.java    # Main entry point
```

---

## Authentication Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant AC as AuthController
    participant AS as AuthService
    participant UR as UserRepository
    participant PE as PasswordEncoder
    participant AM as AuthenticationManager
    participant JP as JwtTokenProvider
    participant ES as Elasticsearch

    rect rgb(232, 245, 233)
    Note over C,ES: Registration Flow
    C->>AC: POST /api/auth/register
    AC->>AS: register(RegisterRequest)
    AS->>UR: existsByUsername()
    UR-->>AS: false
    AS->>UR: existsByEmail()
    UR-->>AS: false
    AS->>PE: encode(password)
    PE-->>AS: hashed_password
    AS->>ES: save(User)
    AS->>AM: authenticate(credentials)
    AM-->>AS: Authentication
    AS->>JP: generateToken(auth)
    JP-->>AS: JWT token
    AS-->>AC: AuthResponse(token)
    AC-->>C: 200 OK + JWT
    end

    rect rgb(227, 242, 253)
    Note over C,ES: Login Flow
    C->>AC: POST /api/auth/login
    AC->>AS: login(LoginRequest)
    AS->>AM: authenticate(username, password)
    AM-->>AS: Authentication
    AS->>JP: generateToken(auth)
    JP-->>AS: JWT token
    AS->>UR: findByUsername()
    UR-->>AS: User
    AS-->>AC: AuthResponse(token)
    AC-->>C: 200 OK + JWT
    end

    rect rgb(252, 228, 236)
    Note over C,ES: Authenticated Request
    C->>C: Add Authorization: Bearer <token>
    C->>AC: GET /api/products
    AC->>AC: JwtAuthenticationFilter validates token
    AC->>AC: Sets SecurityContext
    AC-->>C: 200 OK + Data
    end
```

---

## API Endpoints

### Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/auth/register` | Register new user | Public |
| `POST` | `/api/auth/login` | Login, get JWT | Public |

### Users

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/users/me` | Current user info | Any |
| `GET` | `/api/users` | List all users | ADMIN |
| `DELETE` | `/api/users/{id}` | Delete user | ADMIN |

### Products

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/products` | Create product | ADMIN/MOD |
| `GET` | `/api/products` | List all products | Any |
| `GET` | `/api/products/{id}` | Get product by ID | Any |
| `PUT` | `/api/products/{id}` | Update product | ADMIN/MOD |
| `DELETE` | `/api/products/{id}` | Delete product | ADMIN |
| `GET` | `/api/products/count` | Count products | Any |
| `POST` | `/api/products/bulk` | Bulk create | ADMIN |
| `DELETE` | `/api/products/bulk` | Bulk delete | ADMIN |

### Product Search

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/products/search?q=` | Full-text search |
| `GET` | `/api/products/search/name/{name}` | Search by name |
| `GET` | `/api/products/search/category/{category}` | Search by category |
| `GET` | `/api/products/search/price?min=&max=` | Price range |
| `GET` | `/api/products/search/fuzzy/{name}` | Fuzzy search |
| `GET` | `/api/products/search/wildcard/{pattern}` | Wildcard search |
| `GET` | `/api/products/search/highlight/{query}` | Search with highlight |
| `GET` | `/api/products/search/bool?name=&minPrice=&maxPrice=` | Bool query |
| `GET` | `/api/products/search/aggregations` | Aggregations |
| `GET` | `/api/products/search/terms-aggregation` | Terms aggregation |
| `GET` | `/api/products/search/category/{cat}/page?page=&size=` | Paginated |
| `GET` | `/api/products/search/sort?category=&sortBy=&direction=` | Sorted |
| `GET` | `/api/products/search/nearby?lat=&lon=&distance=` | Geo nearby |
| `POST` | `/api/products/search/tags` | Search by tags |

### Product Admin

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/products/admin/index/{name}` | Create index | ADMIN |
| `GET` | `/api/products/admin/index/{name}/exists` | Check index | ADMIN |
| `DELETE` | `/api/products/admin/index/{name}` | Delete index | ADMIN |
| `POST` | `/api/products/admin/index/{name}/refresh` | Refresh index | ADMIN |

### Articles

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/articles` | Create article | ADMIN/MOD |
| `GET` | `/api/articles` | List all | Any |
| `GET` | `/api/articles/{id}` | Get by ID | Any |
| `PUT` | `/api/articles/{id}` | Update | ADMIN/MOD |
| `DELETE` | `/api/articles/{id}` | Delete | ADMIN |
| `GET` | `/api/articles/count` | Count | Any |
| `GET` | `/api/articles/author/{author}` | By author | Any |
| `POST` | `/api/articles/bulk` | Bulk create | ADMIN |

### Article Search

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/articles/search/fulltext/{query}` | Full-text search |
| `GET` | `/api/articles/search/title/{query}` | Search by title |
| `GET` | `/api/articles/search/content/{query}` | Search by content |
| `GET` | `/api/articles/search/highlight/{query}` | Highlight search |
| `GET` | `/api/articles/search/paged?q=&page=&size=` | Paginated search |
| `GET` | `/api/articles/search/bool/must?title=&author=` | Bool must |
| `GET` | `/api/articles/search/bool/must-not/{author}` | Bool must-not |
| `GET` | `/api/articles/search/bool/filter` | Terms filter |

### Logs

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/logs` | Create log | ADMIN/MOD |
| `GET` | `/api/logs` | List all | Any |
| `GET` | `/api/logs/{id}` | Get by ID | Any |
| `DELETE` | `/api/logs/{id}` | Delete | ADMIN |
| `GET` | `/api/logs/level/{level}` | By level | Any |
| `GET` | `/api/logs/service/{service}` | By service | Any |
| `GET` | `/api/logs/errors` | Error responses | Any |
| `GET` | `/api/logs/slow?minDurationMs=` | Slow requests | Any |
| `GET` | `/api/logs/search/{query}` | Search message | Any |
| `GET` | `/api/logs/search/phrase/{phrase}` | Exact phrase | Any |
| `POST` | `/api/logs/bulk` | Bulk create | ADMIN |

### Log Search

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/logs/search/bool?level=&service=&minResponseCode=` | Bool query |
| `GET` | `/api/logs/aggregations` | Aggregations |
| `GET` | `/api/logs/aggregations/histogram` | Date histogram |

### Locations

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/locations` | Create location | ADMIN/MOD |
| `GET` | `/api/locations` | List all | Any |
| `GET` | `/api/locations/{id}` | Get by ID | Any |
| `PUT` | `/api/locations/{id}` | Update | ADMIN/MOD |
| `DELETE` | `/api/locations/{id}` | Delete | ADMIN |
| `GET` | `/api/locations/country/{country}` | By country | Any |
| `GET` | `/api/locations/city/{city}` | By city | Any |
| `GET` | `/api/locations/type/{type}` | By type | Any |
| `GET` | `/api/locations/nearby?lat=&lon=&distance=` | Nearby | Any |
| `GET` | `/api/locations/bounding-box?topLat=&topLon=&bottomLat=&bottomLon=` | Bounding box | Any |

### Location Search

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/locations/search/geo-distance?lat=&lon=&distance=` | Geo distance |
| `GET` | `/api/locations/search/geo-bounding-box?topLat=&topLon=&bottomLat=&bottomLon=` | Geo bounding box |

---

## Elasticsearch Features

### Query Types Demonstrated

```mermaid
flowchart LR
    subgraph SEARCH["Search Query Types"]
        A[Full-Text Search]
        B[Fuzzy Search]
        C[Wildcard Search]
        D[Bool Query]
        E[Highlight Search]
        F[Aggregations]
        G[Geo Queries]
        H[Nested Queries]
        I[Pagination & Sort]
    end

    subgraph PRODUCTS["Product Domain"]
        A1["match / multi_match"]
        B1["match + fuzziness: AUTO"]
        C1["wildcard on keyword"]
        D1["must + filter + range"]
        E1["highlight tags"]
        F1["avg, max, min, sum, terms"]
        G1["geo_distance, geo_bounding_box"]
        H1["nested reviews"]
        I1["PageRequest + Sort"]
    end

    A --> A1
    B --> B1
    C --> C1
    D --> D1
    E --> E1
    F --> F1
    G --> G1
    H --> H1
    I --> I1

    style SEARCH fill:#e8eaf6,stroke:#283593,stroke-width:2px,color:#000
    style PRODUCTS fill:#e0f2f1,stroke:#00695c,stroke-width:2px,color:#000
```

### Search Flow

```mermaid
flowchart TD
    START([Client Request]) --> VALIDATE{Valid JWT?}
    VALIDATE -->|No| REJECT[401/403]
    VALIDATE -->|Yes| ROUTE{Endpoint}

    ROUTE -->|/search/fuzzy| FUZZY["match query + fuzziness:AUTO"]
    ROUTE -->|/search/wildcard| WILDCARD["wildcard on keyword field"]
    ROUTE -->|/search?fulltext| FULLTEXT["multi_match on name + description"]
    ROUTE -->|/search/bool| BOOL["bool: must + filter + range"]
    ROUTE -->|/search/highlight| HIGHLIGHT["multi_match + highlight params"]
    ROUTE -->|/search/aggregations| AGG["agg: avg, max, min, sum, terms"]
    ROUTE -->|/search/geo*| GEO["geo_distance / geo_bounding_box"]

    FUZZY --> EXECUTE[Execute NativeQuery]
    WILDCARD --> EXECUTE
    FULLTEXT --> EXECUTE
    BOOL --> EXECUTE
    HIGHLIGHT --> EXECUTE
    AGG --> EXECUTE
    GEO --> EXECUTE

    EXECUTE --> MAPPER["Map SearchHits → Response"]
    MAPPER --> RETURN([200 OK + Results])

    REJECT --> RETURN_ERR([Error Response])

    style START fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px,color:#000
    style REJECT fill:#ffcdd2,stroke:#c62828,stroke-width:2px,color:#000
    style RETURN fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px,color:#000
    style RETURN_ERR fill:#ffcdd2,stroke:#c62828,stroke-width:2px,color:#000
    style EXECUTE fill:#bbdefb,stroke:#1565c0,stroke-width:2px,color:#000
```

---

## Entity Relationship

```mermaid
erDiagram
    PRODUCT {
        string id PK
        string name
        string description
        string category
        list tags
        double price
        integer stockQuantity
        boolean available
        geo_point location
        date createdAt
        date updatedAt
    }

    REVIEW {
        string reviewer
        string comment
        integer rating
    }

    ARTICLE {
        string id PK
        string title
        string content
        string author
        list categories
        list tags
        integer viewCount
        boolean published
        string status
        date publishedAt
        date createdAt
        date updatedAt
    }

    LOG_ENTRY {
        string id PK
        string level
        string service
        string message
        integer responseCode
        long durationMs
        string host
        date timestamp
    }

    LOCATION {
        string id PK
        string name
        string address
        string type
        geo_point coordinates
        string country
        string city
        date createdAt
    }

    USER {
        string id PK
        string username
        string email
        string password
        string role
        boolean enabled
        date createdAt
    }

    PRODUCT ||--o{ REVIEW : "has"
```

---

## Configuration

### application.yaml

```yaml
spring:
  application:
    name: elastic-search
  elasticsearch:
    uris: http://localhost:9200

server:
  port: 8080

app:
  cors:
    allowed-origins: http://localhost:3000
  jwt:
    secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
    expiration-ms: 86400000

management:
  endpoints:
    web:
      exposure:
        include: health,info,beans
```

### Docker Compose

The `docker-compose.yml` runs Elasticsearch 9.4.5 with:
- Single-node discovery
- Security disabled (for development)
- 512MB heap
- Persistent data volume
- Health check on port 9200

---

## Key Implementation Details

### Multi-field Mapping

Product `name` field uses a keyword sub-field for exact matching and wildcard queries:

```java
@MultiField(
    mainField = @Field(type = FieldType.Text, analyzer = "standard"),
    otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword)
)
private String name;
```

### Fuzzy Search

Uses `match` query with `fuzziness: AUTO` instead of raw `fuzzy` query (which doesn't analyze input):

```json
{ "match": { "name": { "query": "Lapto", "fuzziness": "AUTO" } } }
```

### GeoPoint DTO Pattern

GeoPoint can't be deserialized from JSON directly, so a `LocationRequest` DTO with nested `Coordinates` class handles the conversion:

```java
// Client sends: {"coordinates": {"lat": 23.81, "lon": 90.41}}
// Converted to: new GeoPoint(lat, lon) in controller
```

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
