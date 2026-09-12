# Elasticsearch + Spring Boot

A complete CRUD and advanced search application built with **Spring Boot 4.1.1** and **Elasticsearch 9.4.5**, featuring JWT authentication, full-text search, fuzzy matching, wildcard queries, geospatial queries, aggregations, and more.

![Java](https://img.shields.io/badge/Java-25-orange?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-green?style=flat-square&logo=springboot&logoColor=white)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-9.4.5-yellow?style=flat-square&logo=elasticsearch&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
  - [Step 1: Client Sends Request](#step-1-client-sends-request)
  - [Step 2: Security Layer Validates](#step-2-security-layer-validates)
  - [Step 3: Controller Routes](#step-3-controller-routes)
  - [Step 4: Service Processes](#step-4-service-processes)
  - [Step 5: Repository Queries Elasticsearch](#step-5-repository-queries-elasticsearch)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Authentication Flow](#authentication-flow)
  - [Step 1: Register](#step-1-register)
  - [Step 2: Login](#step-2-login)
  - [Step 3: Use JWT Token](#step-3-use-jwt-token)
- [API Endpoints](#api-endpoints)
- [Elasticsearch Features](#elasticsearch-features)
- [Entity Relationship](#entity-relationship)
- [Configuration](#configuration)
- [License](#license)

---

## Architecture Overview

### Step 1: Client Sends Request

Every request enters through the client and hits the Spring Boot application on port `8080`.

```mermaid
flowchart LR
    CLIENT["🌐 Client\n(Browser / Postman / curl)"]
    APP["Spring Boot App\nlocalhost:8080"]
    ES["Elasticsearch\nlocalhost:9200"]

    CLIENT -->|"HTTP Request"| APP
    APP -->|"Query / Index"| ES

    style CLIENT fill:#e3f2fd,stroke:#1565c0,stroke-width:2px,color:#000
    style APP fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
    style ES fill:#fffde7,stroke:#f9a825,stroke-width:2px,color:#000
```

---

### Step 2: Security Layer Validates

Before reaching any controller, the request passes through Spring Security. The JWT filter checks if a valid token is attached.

```mermaid
flowchart TD
    REQ([Incoming Request]) --> JWT{"Has JWT Token?"}

    JWT -->|"No + Public Endpoint\n(/api/auth/**)"| PERMIT["Access Granted"]
    JWT -->|"No + Protected Endpoint"| DENY["401 Unauthorized"]
    JWT -->|"Yes"| VALIDATE{"Token Valid?"}

    VALIDATE -->|"Yes"| ROLES{"Has Required Role?"}
    VALIDATE -->|"No / Expired"| DENY2["403 Forbidden"]

    ROLES -->|"Yes"| CONTINUE["Pass to Controller"]
    ROLES -->|"No"| DENY3["403 Forbidden"]

    PERMIT --> CONTINUE

    style REQ fill:#e3f2fd,stroke:#1565c0,stroke-width:2px,color:#000
    style DENY fill:#ffcdd2,stroke:#c62828,stroke-width:2px,color:#000
    style DENY2 fill:#ffcdd2,stroke:#c62828,stroke-width:2px,color:#000
    style DENY3 fill:#ffcdd2,stroke:#c62828,stroke-width:2px,color:#000
    style CONTINUE fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px,color:#000
    style PERMIT fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px,color:#000
```

---

### Step 3: Controller Routes

The request is routed to the appropriate controller based on the URL path. Each domain (product, article, log, location) has its own controllers split by responsibility.

```mermaid
flowchart TD
    FILTER["Security Filter passes request"] --> ROUTE{"URL Path?"}

    ROUTE -->|"/api/auth/*"| AUTH_CTRL["AuthController\n(Register / Login)"]
    ROUTE -->|"/api/users/*"| USER_CTRL["UserController"]
    ROUTE -->|"/api/products"| PROD_CTRL["ProductController\n(CRUD)"]
    ROUTE -->|"/api/products/search/*"| PROD_SEARCH["ProductSearchController\n(Queries)"]
    ROUTE -->|"/api/products/admin/*"| PROD_ADMIN["ProductAdminController\n(Index Mgmt)"]
    ROUTE -->|"/api/articles"| ART_CTRL["ArticleController\n(CRUD)"]
    ROUTE -->|"/api/articles/search/*"| ART_SEARCH["ArticleSearchController"]
    ROUTE -->|"/api/logs"| LOG_CTRL["LogController\n(CRUD)"]
    ROUTE -->|"/api/logs/search/*"| LOG_SEARCH["LogSearchController"]
    ROUTE -->|"/api/locations"| LOC_CTRL["LocationController\n(CRUD)"]
    ROUTE -->|"/api/locations/search/*"| LOC_SEARCH["LocationSearchController\n(Geo Queries)"]

    style FILTER fill:#e3f2fd,stroke:#1565c0,stroke-width:2px,color:#000
    style AUTH_CTRL fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
    style USER_CTRL fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
    style PROD_CTRL fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
    style PROD_SEARCH fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
    style PROD_ADMIN fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
    style ART_CTRL fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
    style ART_SEARCH fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
    style LOG_CTRL fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
    style LOG_SEARCH fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
    style LOC_CTRL fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
    style LOC_SEARCH fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
```

---

### Step 4: Service Processes

Controllers delegate to services. Each domain has a **CRUD service** and a separate **Search service** for advanced queries.

```mermaid
flowchart TD
    CTRL["Controller"] --> DECIDE{"Operation Type?"}

    DECIDE -->|"Create / Update /\nDelete / Find All"| CRUD["CRUD Service\n(ProductService, etc.)"]
    DECIDE -->|"Advanced Search /\nAggregations / Geo"| SEARCH["Search Service\n(ProductSearchService, etc.)"]
    DECIDE -->|"Index Management\n(create/delete index)"| INDEX["Index Service\n(ProductIndexService)"]

    CRUD --> REPO["Repository\n(Spring Data ES)"]
    SEARCH --> OPS["ElasticsearchOperations\n(NativeQuery)"]
    INDEX --> OPS

    REPO -->|"Derived Queries\n(findByName, etc.)"| ES[(Elasticsearch)]
    OPS -->|"Native Queries\n(bool, highlight, agg)"| ES

    style CTRL fill:#fff3e0,stroke:#e65100,stroke-width:2px,color:#000
    style CRUD fill:#f3e5f5,stroke:#6a1b9a,stroke-width:2px,color:#000
    style SEARCH fill:#f3e5f5,stroke:#6a1b9a,stroke-width:2px,color:#000
    style INDEX fill:#f3e5f5,stroke:#6a1b9a,stroke-width:2px,color:#000
    style REPO fill:#e0f2f1,stroke:#00695c,stroke-width:2px,color:#000
    style OPS fill:#e0f2f1,stroke:#00695c,stroke-width:2px,color:#000
    style ES fill:#fffde7,stroke:#f9a825,stroke-width:2px,color:#000
```

---

### Step 5: Repository Queries Elasticsearch

Two ways to query Elasticsearch:

```mermaid
flowchart LR
    subgraph METHOD1["Method 1: Spring Data Derived Queries"]
        A1["findByName('Laptop')"]
        A2["findByCategory('Electronics')"]
        A3["findByPriceBetween(100, 500)"]
        A1 & A2 & A3 -->|"Auto-generated"| A4["Elasticsearch Query"]
    end

    subgraph METHOD2["Method 2: Native Queries"]
        B1["Bool Query\nmust + filter + range"]
        B2["Highlight Query\nwith pre/post tags"]
        B3["Aggregation\navg, max, terms"]
        B4["Geo Query\ngeo_distance"]
        B1 & B2 & B3 & B4 -->|"Manual JSON\nor Java Client"| B5["Elasticsearch Query"]
    end

    A4 --> ES[(Elasticsearch)]
    B5 --> ES

    style METHOD1 fill:#e8eaf6,stroke:#283593,stroke-width:2px,color:#000
    style METHOD2 fill:#fce4ec,stroke:#c62828,stroke-width:2px,color:#000
    style ES fill:#fffde7,stroke:#f9a825,stroke-width:2px,color:#000
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

### Step 1: Register

Create a new account. The password is hashed with BCrypt before storing.

```mermaid
sequenceDiagram
    participant C as Client
    participant AC as AuthController
    participant AS as AuthService
    participant UR as UserRepository
    participant PE as PasswordEncoder
    participant AM as AuthenticationManager
    participant JP as JwtTokenProvider

    C->>AC: POST /api/auth/register
    AC->>AS: register(RegisterRequest)
    AS->>UR: existsByUsername("admin")
    UR-->>AS: false (not taken)
    AS->>UR: existsByEmail("admin@test.com")
    UR-->>AS: false (not used)
    AS->>PE: encode("password123")
    PE-->>AS: "$2a$10$hashed..."
    AS->>UR: save(new User with hashed password)
    AS->>AM: authenticate(username, password)
    AM-->>AS: Authentication object
    AS->>JP: generateToken(authentication)
    JP-->>AS: "eyJhbGciOi..."
    AS-->>C: {"token": "eyJhbGciOi...", "username": "admin"}
```

---

### Step 2: Login

Authenticate with existing credentials and receive a JWT token.

```mermaid
sequenceDiagram
    participant C as Client
    participant AC as AuthController
    participant AS as AuthService
    participant AM as AuthenticationManager
    participant JP as JwtTokenProvider
    participant UR as UserRepository

    C->>AC: POST /api/auth/login
    AC->>AS: login(LoginRequest)
    AS->>AM: authenticate("admin", "password123")
    AM-->>AS: Authentication object (verified)
    AS->>JP: generateToken(authentication)
    JP-->>AS: "eyJhbGciOi..."
    AS->>UR: findByUsername("admin")
    UR-->>AS: User object
    AS-->>C: {"token": "eyJhbGciOi...", "username": "admin", "role": "ADMIN"}
```

---

### Step 3: Use JWT Token

Attach the token to subsequent requests. The filter validates it on every call.

```mermaid
sequenceDiagram
    participant C as Client
    participant F as JwtAuthenticationFilter
    participant JP as JwtTokenProvider
    participant SC as SecurityContext
    participant CTRL as ProductController

    C->>C: Set header: Authorization: Bearer eyJhbGciOi...
    C->>F: GET /api/products
    F->>JP: extractUsername("eyJhbGciOi...")
    JP-->>F: "admin"
    F->>F: Load UserDetails for "admin"
    F->>SC: Set Authentication object
    F->>CTRL: Forward request (now authenticated)
    CTRL->>CTRL: Execute business logic
    CTRL-->>C: 200 OK + Product data
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

### Feature 1: Full-Text Search

Search across multiple text fields using `multi_match`. Elasticsearch analyzes the input (lowercases, tokenizes) and matches against analyzed content.

```mermaid
flowchart LR
    Q["query: \"programming\""] --> ANALYZE["Standard Analyzer\n\"programming\""]
    ANALYZE --> MATCH["Match against\nname + description fields"]
    MATCH --> RESULT["Results:\nBook (score: 1.5)"]

    style Q fill:#bbdefb,stroke:#1565c0,stroke-width:2px,color:#000
    style ANALYZE fill:#fff3e0,stroke:#e65100,stroke-width:2px,color:#000
    style MATCH fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
    style RESULT fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px,color:#000
```

---

### Feature 2: Fuzzy Search

Tolerates typos. Uses `match` query with `fuzziness: AUTO` so "Lapto" matches "Laptop".

```mermaid
flowchart LR
    Q["query: \"Lapto\""] --> ANALYZE["Standard Analyzer\n\"lapto\""]
    ANALYZE --> FUZZY["Fuzzy Match\nedit distance: 1"]
    FUZZY -->|"lapto → laptop\n(insert 'p')"| RESULT["Results:\nLaptop (score: 0.93)"]

    style Q fill:#bbdefb,stroke:#1565c0,stroke-width:2px,color:#000
    style ANALYZE fill:#fff3e0,stroke:#e65100,stroke-width:2px,color:#000
    style FUZZY fill:#fce4ec,stroke:#c62828,stroke-width:2px,color:#000
    style RESULT fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px,color:#000
```

---

### Feature 3: Wildcard Search

Pattern matching on keyword fields. "Pho*" matches "Phone".

```mermaid
flowchart LR
    Q["pattern: \"Pho*\"] --> FIELD["Match against\nname.keyword field"]
    FIELD --> WILDCARD["Wildcard Match\n(Pho → Phone)"]
    WILDCARD --> RESULT["Results:\nPhone"]

    style Q fill:#bbdefb,stroke:#1565c0,stroke-width:2px,color:#000
    style FIELD fill:#fff3e0,stroke:#e65100,stroke-width:2px,color:#000
    style WILDCARD fill:#f3e5f5,stroke:#6a1b9a,stroke-width:2px,color:#000
    style RESULT fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px,color:#000
```

---

### Feature 4: Bool Query

Combine multiple conditions with `must`, `filter`, and `range`.

```mermaid
flowchart TD
    Q["Bool Query"] --> MUST["must:\nname matches \"Laptop\""]
    Q --> FILTER["filter:\nprice >= 500 AND price <= 2000"]
    Q --> SORT["sort:\nby price ASC"]

    MUST --> EXECUTE["Execute"]
    FILTER --> EXECUTE
    SORT --> EXECUTE

    EXECUTE --> RESULT["Results:\nLaptop ($999.99)\nPhone ($699.99)"]

    style Q fill:#e3f2fd,stroke:#1565c0,stroke-width:2px,color:#000
    style MUST fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#000
    style FILTER fill:#fff3e0,stroke:#e65100,stroke-width:2px,color:#000
    style SORT fill:#f3e5f5,stroke:#6a1b9a,stroke-width:2px,color:#000
    style EXECUTE fill:#e0f2f1,stroke:#00695c,stroke-width:2px,color:#000
    style RESULT fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px,color:#000
```

---

### Feature 5: Highlight Search

Returns matching text snippets wrapped in `<em>` tags.

```mermaid
flowchart LR
    Q["query: \"gaming\""] --> SEARCH["multi_match on\nname + description"]
    SEARCH --> HIGHLIGHT["Highlight:\npre = <em>\npost = </em>"]
    HIGHLIGHT --> RESULT["Results:\n\"<em>gaming</em> laptop\""]

    style Q fill:#bbdefb,stroke:#1565c0,stroke-width:2px,color:#000
    style SEARCH fill:#fff3e0,stroke:#e65100,stroke-width:2px,color:#000
    style HIGHLIGHT fill:#fce4ec,stroke:#c62828,stroke-width:2px,color:#000
    style RESULT fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px,color:#000
```

---

### Feature 6: Aggregations

Compute statistics and group data without returning individual documents.

```mermaid
flowchart TD
    QUERY["Search Query (page size: 1)"] --> AGG1["avg_price:\navg(price)"]
    QUERY --> AGG2["max_price:\nmax(price)"]
    QUERY --> AGG3["min_price:\nmin(price)"]
    QUERY --> AGG4["sum_stock:\nsum(stockQuantity)"]
    QUERY --> AGG5["by_category:\nterms on category"]

    AGG1 --> RESULT["Aggregation Results:\navg_price: 583.32\nmax_price: 999.99\nmin_price: 49.99\nby_category: {Electronics: 2, Books: 1}"]
    AGG2 --> RESULT
    AGG3 --> RESULT
    AGG4 --> RESULT
    AGG5 --> RESULT

    style QUERY fill:#e3f2fd,stroke:#1565c0,stroke-width:2px,color:#000
    style AGG1 fill:#fff3e0,stroke:#e65100,stroke-width:2px,color:#000
    style AGG2 fill:#fff3e0,stroke:#e65100,stroke-width:2px,color:#000
    style AGG3 fill:#fff3e0,stroke:#e65100,stroke-width:2px,color:#000
    style AGG4 fill:#fff3e0,stroke:#e65100,stroke-width:2px,color:#000
    style AGG5 fill:#fff3e0,stroke:#e65100,stroke-width:2px,color:#000
    style RESULT fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px,color:#000
```

---

### Feature 7: Geo Queries

Find locations by distance or bounding box.

```mermaid
flowchart LR
    subgraph GEO_DISTANCE["Geo Distance Query"]
        A1["Point: lat=23.81, lon=90.41"]
        A2["Distance: 50km"]
        A1 --> A3["Find all locations\nwithin 50km of Dhaka"]
        A2 --> A3
        A3 --> A4["Dhaka Office\n(distance: 0.5km)"]
    end

    subgraph GEO_BBOX["Geo Bounding Box Query"]
        B1["Top-Left: 24, 91"]
        B2["Bottom-Right: 22, 90"]
        B1 --> B3["Find all locations\ninside the rectangle"]
        B2 --> B3
        B3 --> B4["Dhaka Office\nChittagong Hub"]
    end

    style GEO_DISTANCE fill:#e8eaf6,stroke:#283593,stroke-width:2px,color:#000
    style GEO_BBOX fill:#e0f2f1,stroke:#00695c,stroke-width:2px,color:#000
```

---

### Feature 8: Pagination & Sort

```mermaid
flowchart LR
    REQ["Request:\npage=0, size=2\nsortBy=price, direction=DESC"] --> PAGE["Spring Data\nPageRequest.of(0, 2, Sort.by(DESC, price))"]
    PAGE --> ES["Elasticsearch:\nsize=2, from=0, sort=price desc"]
    ES --> RESULT["Page Result:\ntotalElements: 3\npage 0 of 2\n[Phone $699, Laptop $999]"]

    style REQ fill:#bbdefb,stroke:#1565c0,stroke-width:2px,color:#000
    style PAGE fill:#fff3e0,stroke:#e65100,stroke-width:2px,color:#000
    style ES fill:#fffde7,stroke:#f9a825,stroke-width:2px,color:#000
    style RESULT fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px,color:#000
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
