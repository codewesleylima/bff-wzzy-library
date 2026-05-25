# WZZY Library - Complete Architecture Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Service Architecture](#service-architecture)
3. [Database Schema](#database-schema)
4. [REST API Reference](#rest-api-reference)
5. [Service-to-Service Communication](#service-to-service-communication)
6. [Data Flow Examples](#data-flow-examples)
7. [Implementation Patterns](#implementation-patterns)
8. [Entity/DTO Reference](#entitydto-reference)
9. [Quick Start for Development](#quick-start-for-development)
10. [Dependencies and Integration Points](#dependencies-and-integration-points)

---

## 1. Project Overview

### What is WZZY Library?
WZZY Library is a microservices-based digital library management system designed to handle book catalog, customer management, shopping carts, orders, payments, and inventory management. It's built with a modern distributed architecture approach.

### Architecture Pattern
**Backend for Frontend (BFF) + Microservices Architecture**
- Single entry point (BFF-Library) routes all requests to appropriate microservices
- Each microservice owns its data and business logic
- Services communicate via REST APIs using Spring Cloud OpenFeign
- Shared utilities and DTOs managed in bff-library package

### Core Microservices

| Service | Role | Database | Key Entities |
|---------|------|----------|-------------|
| **bff-library** | API Gateway & Shared Library | None | - |
| **ms-auth** | Authentication & JWT | MySQL | User |
| **ms-catalog** | Book Catalog Management | MySQL | Book, Author, Category, Editor |
| **ms-customers** | Customer Management | MySQL | Customer, Profile, Address, Favorite, Preference, PurchaseHistory |
| **ms-order** | Order Orchestration | MySQL | Order, OrderItem, ShoppingCart, CartItem |
| **ms-payments** | Payment Processing | MySQL | Payment, PaymentMethod, Transaction, WebhookEvent, FraudCheckResult |
| **ms-stock** | Inventory Management | MySQL | StockItem, StockMovement, Reservation, Availability |

### Technology Stack
- **Language**: Java 21
- **Framework**: Spring Boot 3
- **Service Communication**: Spring Cloud OpenFeign
- **Security**: Spring Security + JWT
- **Persistence**: Spring Data JPA + Hibernate
- **Database**: MySQL
- **Build Tool**: Gradle
- **Testing**: JUnit + Mockito

---

## 2. Service Architecture

### MS-AUTH (Authentication Microservice)
**Purpose**: Manages user registration, login, JWT token generation, and token validation

**Key Entities**:
- `User`: User profiles with credentials and authentication metadata

**Database Tables**:
- `users`: Stores user accounts, login credentials, verification status

**Endpoints** (5 total):
```
POST   /api/v1/auth/register          → User registration
POST   /api/v1/auth/login             → Authentication & token generation
POST   /api/v1/auth/refresh-token     → Refresh expired access token
POST   /api/v1/auth/logout            → Logout
GET    /api/v1/auth/validate          → Token validation
```

**Key Services**:
- `AuthService`: Handles registration and login logic
- `TokenService`: JWT token generation and validation

**Dependencies**: None (stateless service)

**File Locations**:
- Entities: `ms-auth/src/main/java/com/wzzy/library/msauth/entity/User.java`
- Controllers: `ms-auth/src/main/java/com/wzzy/library/msauth/controller/AuthController.java`
- Services: `ms-auth/src/main/java/com/wzzy/library/msauth/service/`
- Repositories: `ms-auth/src/main/java/com/wzzy/library/msauth/repository/UserRepository.java`

---

### MS-CATALOG (Book Catalog Microservice)
**Purpose**: Manages book catalog including books, authors, categories, and publishers

**Key Entities**:
- `Book`: Book information with pricing and availability status
- `Author`: Author details
- `Category`: Book categories for organization
- `Editor`: Publishing companies/editors

**Database Tables**:
- `books`: Main book catalog (relationships to authors, categories, editors)
- `authors`: Author profiles
- `categories`: Book categories
- `editors`: Publishing companies

**Endpoints** (15 total):
```
Books:
POST   /api/v1/books                  → Create book
GET    /api/v1/books/{id}             → Get book by ID
GET    /api/v1/books/isbn/{isbn}      → Get book by ISBN
GET    /api/v1/books                  → List all books (paginated)
GET    /api/v1/books/status/{status}  → Filter by status
GET    /api/v1/books/category/{id}    → Filter by category
GET    /api/v1/books/author/{id}      → Filter by author
GET    /api/v1/books/search           → Search by title
PUT    /api/v1/books/{id}             → Update book
DELETE /api/v1/books/{id}             → Delete book

Categories:
POST   /api/v1/categories             → Create category
GET    /api/v1/categories/{id}        → Get category
GET    /api/v1/categories             → List all categories
PUT    /api/v1/categories/{id}        → Update category
DELETE /api/v1/categories/{id}        → Delete category
```

**Key Services**:
- `BookService`: Book CRUD and search operations
- `CategoryService`: Category management
- `AuthorService`: Author management
- `EditorService`: Editor management

**Dependencies**: Called by ms-order (validate books), Called by ms-customers (book references)

**File Locations**:
- Entities: `ms-catalog/src/main/java/com/wzzy/library/mscatalog/entity/`
- Controllers: `ms-catalog/src/main/java/com/wzzy/library/mscatalog/controller/`
- Services: `ms-catalog/src/main/java/com/wzzy/library/mscatalog/service/`
- Repositories: `ms-catalog/src/main/java/com/wzzy/library/mscatalog/repository/`

---

### MS-CUSTOMERS (Customer Management Microservice)
**Purpose**: Manages customer profiles, addresses, preferences, favorites, and purchase history

**Key Entities**:
- `Customer`: Customer profile with contact info
- `Profile`: Extended customer profile (DOB, bio, image)
- `Address`: Customer delivery/billing addresses
- `Favorite`: Bookmarks/favorite books list
- `Preference`: Customer preferences (categories, notifications, etc.)
- `PurchaseHistory`: Historical record of purchases

**Database Tables**:
- `customers`: Main customer records
- `profiles`: Extended customer profiles
- `addresses`: Customer addresses
- `favorites`: Favorite books
- `preferences`: User preferences
- `purchase_history`: Purchase records

**Endpoints** (10 total):
```
Customers:
POST   /api/v1/customers              → Create customer
GET    /api/v1/customers/{id}         → Get customer
GET    /api/v1/customers/user/{id}    → Get customer by user ID
GET    /api/v1/customers              → List all customers
PUT    /api/v1/customers/{id}         → Update customer
DELETE /api/v1/customers/{id}         → Delete customer

Profiles:
POST   /api/v1/profiles               → Create profile
GET    /api/v1/profiles/customer/{id} → Get profile
PUT    /api/v1/profiles/{id}          → Update profile
DELETE /api/v1/profiles/{id}          → Delete profile
```

**Key Services**:
- `CustomerService`: Customer management
- `ProfileService`: Profile management
- `AddressService`: Address management
- `FavoriteService`: Favorite books management
- `PreferenceService`: Preferences management
- `PurchaseHistoryService`: Purchase history tracking

**Dependencies**: Called by ms-order (record purchases)

**File Locations**:
- Entities: `ms-customers/src/main/java/com/wzzy/library/mscustomers/entity/`
- Controllers: `ms-customers/src/main/java/com/wzzy/library/mscustomers/controller/`
- Services: `ms-customers/src/main/java/com/wzzy/library/mscustomers/service/`
- Repositories: `ms-customers/src/main/java/com/wzzy/library/mscustomers/repository/`

---

### MS-ORDER (Order & Shopping Cart Orchestration)
**Purpose**: Manages shopping carts and order orchestration, coordinates with other services for checkout

**Key Entities**:
- `Order`: Customer orders with totals and addresses
- `OrderItem`: Individual items in an order
- `ShoppingCart`: Customer's active shopping cart
- `CartItem`: Items in shopping cart

**Database Tables**:
- `orders`: Customer orders
- `order_items`: Line items in orders
- `shopping_carts`: Active shopping carts
- `cart_items`: Items in shopping carts

**Endpoints** (17 total):
```
Orders:
POST   /api/v1/orders                 → Create order
GET    /api/v1/orders/{id}            → Get order
GET    /api/v1/orders/number/{num}    → Get order by number
GET    /api/v1/orders/customer/{id}   → Get customer's orders
GET    /api/v1/orders/status/{status} → Filter by status
PUT    /api/v1/orders/{id}            → Update order
PUT    /api/v1/orders/{id}/status/{s} → Update status
POST   /api/v1/orders/{id}/cancel     → Cancel order
DELETE /api/v1/orders/{id}            → Delete order

Shopping Cart:
GET    /api/v1/shopping-cart/customer/{id}              → Get cart
POST   /api/v1/shopping-cart/customer/{id}/items        → Add to cart
DELETE /api/v1/shopping-cart/customer/{id}/items/{id}   → Remove from cart
PUT    /api/v1/shopping-cart/customer/{id}/items/{id}   → Update quantity
DELETE /api/v1/shopping-cart/customer/{id}              → Clear cart
GET    /api/v1/shopping-cart/customer/{id}/total-items  → Get cart count

Checkout:
POST   /api/v1/checkout               → Process checkout
POST   /api/v1/checkout/validate      → Validate checkout
```

**Key Services**:
- `OrderService`: Order management
- `ShoppingCartService`: Shopping cart operations
- `CheckoutService`: Checkout orchestration (calls ms-catalog, ms-stock, ms-payments, ms-customers)

**Dependencies**:
- Calls `ms-catalog` (validate books, get prices)
- Calls `ms-stock` (check availability, reserve items)
- Calls `ms-payments` (process payments)
- Calls `ms-customers` (record purchase history)

**File Locations**:
- Entities: `ms-order/src/main/java/com/wzzy/library/msorder/entity/`
- Controllers: `ms-order/src/main/java/com/wzzy/library/msorder/controller/`
- Services: `ms-order/src/main/java/com/wzzy/library/msorder/service/`
- Repositories: `ms-order/src/main/java/com/wzzy/library/msorder/repository/`

---

### MS-PAYMENTS (Payment Processing Microservice)
**Purpose**: Processes payments, handles external payment gateway integration, performs fraud checks

**Key Entities**:
- `Payment`: Payment records for orders
- `PaymentMethod`: Customer payment methods (cards, PIX keys)
- `Transaction`: Individual payment transactions with gateway responses
- `WebhookEvent`: Webhook events from payment gateway
- `FraudCheckResult`: Fraud assessment results

**Database Tables**:
- `payments`: Payment records
- `payment_methods`: Customer payment methods
- `transactions`: Payment transactions
- `webhook_events`: Webhook events
- `fraud_check_results`: Fraud check results

**Endpoints** (11 total):
```
Payments:
POST   /api/v1/payments                      → Create payment
GET    /api/v1/payments/{id}                 → Get payment
GET    /api/v1/payments/reference/{ref}      → Get by reference
GET    /api/v1/payments/order/{id}           → Get by order
GET    /api/v1/payments/customer/{id}        → Get customer payments
GET    /api/v1/payments/status/{status}      → Filter by status
POST   /api/v1/payments/{id}/process         → Process payment
POST   /api/v1/payments/{id}/refund          → Refund payment

Webhooks:
POST   /api/v1/webhooks/payment-gateway      → Payment gateway webhook
POST   /api/v1/webhooks/confirm/{id}         → Confirm webhook receipt
GET    /api/v1/webhooks/health               → Webhook health check
```

**Key Services**:
- `PaymentService`: Payment management
- `PaymentGatewayService`: External gateway integration
- `FraudCheckService`: Fraud checking
- `WebhookService`: Webhook processing

**Feign Clients**:
- `ExternalPaymentGatewayClient`: Calls external payment provider
  - `POST /pix/process` - Process PIX payment
  - `POST /card/authorize` - Authorize card
  - `POST /card/capture` - Capture card payment
  - `POST /fraud-check` - Perform fraud check
  - `POST /refund` - Refund payment

**Dependencies**: Called by ms-order (during checkout)

**File Locations**:
- Entities: `ms-payments/src/main/java/com/wzzy/library/mspayments/entity/`
- Controllers: `ms-payments/src/main/java/com/wzzy/library/mspayments/controller/`
- Services: `ms-payments/src/main/java/com/wzzy/library/mspayments/service/`
- Repositories: `ms-payments/src/main/java/com/wzzy/library/mspayments/repository/`
- Feign Client: `ms-payments/src/main/java/com/wzzy/library/mspayments/client/ExternalPaymentGatewayClient.java`

---

### MS-STOCK (Inventory Management Microservice)
**Purpose**: Manages inventory, tracks stock movements, handles reservations, monitors availability

**Key Entities**:
- `StockItem`: Book inventory with quantities and location
- `StockMovement`: Record of stock movements (entries, exits, adjustments)
- `Reservation`: Temporary holds on stock for pending orders
- `Availability`: Availability status and restock information

**Database Tables**:
- `stock_items`: Current inventory
- `stock_movements`: Historical movements
- `reservations`: Pending item reservations
- `availability`: Availability tracking

**Endpoints** (16 total):
```
Stock:
POST   /api/v1/stock                              → Create stock
GET    /api/v1/stock/{id}                         → Get stock item
GET    /api/v1/stock/book/{id}                    → Get by book ID
GET    /api/v1/stock/book/{id}/available-quantity → Get available qty
GET    /api/v1/stock/book/{id}/has-stock          → Check availability
POST   /api/v1/stock/movement                     → Record movement
GET    /api/v1/stock/{id}/movements               → Get movements history
PUT    /api/v1/stock/{id}                         → Update stock
DELETE /api/v1/stock/{id}                         → Delete stock

Reservations:
POST   /api/v1/reservations                       → Create reservation
GET    /api/v1/reservations/{id}                  → Get reservation
GET    /api/v1/reservations/code/{code}           → Get by code
GET    /api/v1/reservations/customer/{id}         → Get customer reservations
GET    /api/v1/reservations/order/{id}            → Get order reservations
POST   /api/v1/reservations/{id}/confirm          → Confirm reservation
POST   /api/v1/reservations/{id}/cancel           → Cancel reservation
```

**Key Services**:
- `StockService`: Stock management and queries
- `ReservationService`: Reservation management
- `AvailabilityService`: Availability tracking

**Dependencies**: Called by ms-order (check availability, reserve items)

**File Locations**:
- Entities: `ms-stock/src/main/java/com/wzzy/library/msstock/entity/`
- Controllers: `ms-stock/src/main/java/com/wzzy/library/msstock/controller/`
- Services: `ms-stock/src/main/java/com/wzzy/library/msstock/service/`
- Repositories: `ms-stock/src/main/java/com/wzzy/library/msstock/repository/`

---

### BFF-LIBRARY (Backend for Frontend & Shared Library)
**Purpose**: 
- Central API gateway routing all requests
- Shared DTOs and exceptions used across services
- Common security and authentication utilities

**Key Components**:
- `ApiResponse<T>`: Generic response wrapper for all API responses
- `TokenDTO`: JWT token data structure
- `Role` enum: User roles (ADMIN, USER, CUSTOMER, GUEST)
- `TokenType` enum: Token types (ACCESS, REFRESH)
- `AuthenticatedUserDTO`: Authenticated user information

**Responsibilities**:
- Routes client requests to appropriate microservices
- Validates JWT tokens via ms-auth
- Aggregates responses from multiple services
- Handles cross-cutting concerns (logging, error handling)

**Technologies**:
- Spring Cloud Gateway: Request routing and filtering
- Spring Cloud OpenFeign: Service-to-service communication
- Spring Security: Authentication and authorization

**File Locations**:
- Shared DTOs: `bff-library/src/main/java/com/wzzy/library/bfflibrary/dto/`
- Enums: `bff-library/src/main/java/com/wzzy/library/bfflibrary/enums/`
- Security: `bff-library/src/main/java/com/wzzy/library/bfflibrary/security/`

---

## 3. Database Schema

### Entity Relationship Diagram (Text Format)

```
╔════════════════════════════════════════════════════════════════════════════╗
║                          MS-AUTH DATABASE                                  ║
╠════════════════════════════════════════════════════════════════════════════╣
║ USERS                                                                       ║
║ ├─ id (UUID, PK)                                                            ║
║ ├─ email (String, UNIQUE)                                                  ║
║ ├─ username (String, UNIQUE)                                               ║
║ ├─ password (String)                                                        ║
║ ├─ firstName, lastName (String)                                             ║
║ ├─ active (Boolean)                                                         ║
║ └─ timestamps                                                               ║
╚════════════════════════════════════════════════════════════════════════════╝

╔════════════════════════════════════════════════════════════════════════════╗
║                        MS-CATALOG DATABASE                                 ║
╠════════════════════════════════════════════════════════════════════════════╣
║ BOOKS ─────────────────────────────────────────────────────────────────────║
║ ├─ id (UUID, PK)                                                            ║
║ ├─ title, isbn (String, isbn UNIQUE)                                       ║
║ ├─ description (LONGTEXT)                                                   ║
║ ├─ basePrice (BigDecimal)                                                   ║
║ ├─ status (BookStatus: AVAILABLE, OUT_OF_STOCK, DISCONTINUED...)            ║
║ ├─ language, format, pageCount, edition (String/Int)                        ║
║ ├─ publicationDate (LocalDate)                                              ║
║ ├─ author_id (FK → AUTHORS)                                                 ║
║ ├─ category_id (FK → CATEGORIES)                                            ║
║ ├─ editor_id (FK → EDITORS)                                                 ║
║ └─ timestamps                                                               ║
║                                                                             ║
║ AUTHORS                           CATEGORIES         EDITORS               ║
║ ├─ id (UUID, PK)                  ├─ id (UUID, PK)    ├─ id (UUID, PK)    ║
║ ├─ name                            ├─ name (UNIQUE)    ├─ name (UNIQUE)    ║
║ ├─ biography                       ├─ description      ├─ website, address ║
║ ├─ birthDate, nationality          ├─ displayOrder     ├─ phone, email     ║
║ └─ timestamps                      └─ timestamps       └─ timestamps       ║
╚════════════════════════════════════════════════════════════════════════════╝

╔════════════════════════════════════════════════════════════════════════════╗
║                      MS-CUSTOMERS DATABASE                                 ║
╠════════════════════════════════════════════════════════════════════════════╣
║ CUSTOMERS ─────────────────────────────────────────────────────────────────║
║ ├─ id (UUID, PK)                                                            ║
║ ├─ userId (String, UNIQUE) → references ms-auth.users                      ║
║ ├─ firstName, lastName, email (String, email UNIQUE)                        ║
║ ├─ phone (String, UNIQUE)                                                   ║
║ ├─ status (CustomerStatus: ACTIVE, INACTIVE, SUSPENDED, DELETED)            ║
║ ├─ cpf, cnpj (String)                                                       ║
║ └─ timestamps                                                               ║
║                                                                             ║
│ ◄── 1:1 ──► PROFILES                                                        ║
║             ├─ id (UUID, PK)                                                ║
║             ├─ dateOfBirth, gender                                          ║
║             ├─ bio, profileImageUrl                                         ║
║             └─ timestamps                                                   ║
║                                                                             ║
│ ◄── 1:M ──► ADDRESSES                                                       ║
║             ├─ id (UUID, PK)                                                ║
║             ├─ street, number, complement, city, state, zipCode             ║
║             ├─ addressType, isDefault (Boolean)                             ║
║             └─ timestamps                                                   ║
║                                                                             ║
│ ◄── 1:M ──► FAVORITES                                                       ║
║             ├─ id (UUID, PK)                                                ║
║             ├─ bookId → references ms-catalog.books                         ║
║             ├─ bookTitle (String)                                           ║
║             └─ createdAt                                                    ║
║                                                                             ║
│ ◄── 1:M ──► PREFERENCES                                                     ║
║             ├─ id (UUID, PK)                                                ║
║             ├─ preferenceType (PreferenceType enum)                         ║
║             ├─ value, description (String)                                  ║
║             └─ timestamps                                                   ║
║                                                                             ║
│ ◄── 1:M ──► PURCHASE_HISTORY                                                ║
║             ├─ id (UUID, PK)                                                ║
║             ├─ orderId → references ms-order.orders                         ║
║             ├─ bookId → references ms-catalog.books                         ║
║             ├─ bookTitle, purchasePrice, quantity                           ║
║             └─ purchasedAt (LocalDateTime)                                  ║
╚════════════════════════════════════════════════════════════════════════════╝

╔════════════════════════════════════════════════════════════════════════════╗
║                         MS-ORDER DATABASE                                  ║
╠════════════════════════════════════════════════════════════════════════════╣
║ ORDERS ──────────────────────────────────────────────────────────────────  ║
║ ├─ id (UUID, PK)                                                            ║
║ ├─ orderNumber (String, UNIQUE)                                             ║
║ ├─ customerId (String) → references ms-customers.customers                 ║
║ ├─ status (OrderStatus: PENDING, CONFIRMED, PROCESSING, SHIPPED...)         ║
║ ├─ totalAmount, discountAmount, taxAmount, shippingCost (BigDecimal)        ║
║ ├─ shippingAddress, billingAddress (String)                                 ║
║ ├─ notes, trackingNumber (String)                                           ║
║ ├─ createdAt, shippedAt, deliveredAt, cancelledAt (LocalDateTime)           ║
║ └─ updatedAt                                                                ║
║                                                                             ║
│ ◄── 1:M ──► ORDER_ITEMS                                                     ║
║             ├─ id (UUID, PK)                                                ║
║             ├─ bookId → references ms-catalog.books                         ║
║             ├─ bookTitle, isbn (String)                                     ║
║             ├─ quantity, unitPrice, totalPrice (BigDecimal)                 ║
║             └─ addedAt (LocalDateTime)                                      ║
║                                                                             ║
║ SHOPPING_CARTS ─────────────────────────────────────────────────────────   ║
║ ├─ id (UUID, PK)                                                            ║
║ ├─ customerId (String, UNIQUE) → references ms-customers.customers         ║
║ ├─ totalItems (Integer)                                                     ║
║ ├─ active (Boolean)                                                         ║
║ ├─ createdAt, lastAccessedAt, abandonedAt (LocalDateTime)                  ║
║ └─ updatedAt                                                                ║
║                                                                             ║
│ ◄── 1:M ──► CART_ITEMS                                                      ║
║             ├─ id (UUID, PK)                                                ║
║             ├─ bookId → references ms-catalog.books                         ║
║             ├─ bookTitle, isbn (String)                                     ║
║             ├─ quantity, unitPrice, totalPrice (BigDecimal)                 ║
║             ├─ addedAt, updatedAt (LocalDateTime)                           ║
╚════════════════════════════════════════════════════════════════════════════╝

╔════════════════════════════════════════════════════════════════════════════╗
║                       MS-PAYMENTS DATABASE                                 ║
╠════════════════════════════════════════════════════════════════════════════╣
║ PAYMENTS ────────────────────────────────────────────────────────────────  ║
║ ├─ id (UUID, PK)                                                            ║
║ ├─ paymentReference (String, UNIQUE)                                        ║
║ ├─ orderId (String) → references ms-order.orders                            ║
║ ├─ customerId (String) → references ms-customers.customers                 ║
║ ├─ amount (BigDecimal)                                                      ║
║ ├─ paymentType (PaymentType: CREDIT_CARD, DEBIT_CARD, PIX...)               ║
║ ├─ status (PaymentStatus: PENDING, PROCESSING, COMPLETED, FAILED...)        ║
║ ├─ description (String)                                                     ║
║ ├─ externalTransactionId, merchantReference (String)                        ║
║ ├─ createdAt, processedAt, completedAt (LocalDateTime)                      ║
║ └─ updatedAt                                                                ║
║                                                                             ║
│ ◄── 1:M ──► TRANSACTIONS                                                    ║
║             ├─ id (UUID, PK)                                                ║
║             ├─ transactionCode (String, UNIQUE)                             ║
║             ├─ amount (BigDecimal)                                          ║
║             ├─ status (TransactionStatus: AUTHORIZED, CAPTURED, FAILED...)   ║
║             ├─ gatewayResponse, authorizationCode, errorMessage (String)    ║
║             └─ createdAt, processedAt (LocalDateTime)                       ║
║                                                                             ║
│ ◄── 1:M ──► FRAUD_CHECK_RESULTS                                             ║
║             ├─ id (UUID, PK)                                                ║
║             ├─ fraudLevel (FraudLevel: LOW, MEDIUM, HIGH)                   ║
║             ├─ riskScore (Double)                                           ║
║             ├─ riskAnalysis, checkDetails (String/LONGTEXT)                 ║
║             ├─ approved (Boolean)                                           ║
║             └─ checkedAt (LocalDateTime)                                    ║
║                                                                             ║
║ PAYMENT_METHODS                          WEBHOOK_EVENTS                    ║
║ ├─ id (UUID, PK)                         ├─ id (UUID, PK)                  ║
║ ├─ customerId (String)                   ├─ externalEventId (UNIQUE)        ║
║ ├─ type (PaymentType)                    ├─ eventType (String)              ║
║ ├─ cardNumber, cvv (masked)              ├─ paymentReference (String)       ║
║ ├─ pixKey, pixKeyType (String)           ├─ payload (LONGTEXT)              ║
║ ├─ maskedCardNumber (String)             ├─ processed (Boolean)             ║
║ ├─ isDefault, isActive (Boolean)         ├─ processingResult (String)       ║
║ └─ timestamps                            └─ receivedAt, processedAt         ║
╚════════════════════════════════════════════════════════════════════════════╝

╔════════════════════════════════════════════════════════════════════════════╗
║                        MS-STOCK DATABASE                                   ║
╠════════════════════════════════════════════════════════════════════════════╣
║ STOCK_ITEMS ────────────────────────────────────────────────────────────   ║
║ ├─ id (UUID, PK)                                                            ║
║ ├─ bookId (String, UNIQUE) → references ms-catalog.books                   ║
║ ├─ bookTitle (String)                                                       ║
║ ├─ quantity, reservedQuantity, availableQuantity (Integer)                  ║
║ ├─ minimumStockLevel (Integer)                                              ║
║ ├─ status (AvailabilityStatus: AVAILABLE, LOW_STOCK, OUT_OF_STOCK...)       ║
║ ├─ warehouseLocation, barcode (String)                                      ║
║ ├─ lastRestockDate (LocalDate)                                              ║
║ └─ timestamps                                                               ║
║                                                                             ║
│ ◄── 1:1 ──► AVAILABILITY                                                    ║
║             ├─ id (UUID, PK)                                                ║
║             ├─ isAvailable (Boolean)                                        ║
║             ├─ nextAvailableDate, estimatedRestockDate (LocalDate)          ║
║             ├─ availabilityNotes (String)                                   ║
║             └─ updatedAt (LocalDateTime)                                    ║
║                                                                             ║
│ ◄── 1:M ──► STOCK_MOVEMENTS                                                 ║
║             ├─ id (UUID, PK)                                                ║
║             ├─ movementType (MovementType: ENTRY, EXIT, RETURN...)           ║
║             ├─ quantity (Integer)                                           ║
║             ├─ reason, reference (String)                                   ║
║             └─ createdAt, processedAt (LocalDateTime)                       ║
║                                                                             ║
│ ◄── 1:M ──► RESERVATIONS                                                    ║
║             ├─ id (UUID, PK)                                                ║
║             ├─ customerId (String) → references ms-customers.customers     ║
║             ├─ orderId (String) → references ms-order.orders                ║
║             ├─ reservedQuantity (Integer)                                   ║
║             ├─ status (ReservationStatus: PENDING, CONFIRMED, EXPIRED...)    ║
║             ├─ reservationCode (String)                                     ║
║             ├─ reservedAt, expiresAt (LocalDateTime)                        ║
║             ├─ confirmedAt, cancelledAt (LocalDateTime)                     ║
║             └─ updatedAt                                                    ║
╚════════════════════════════════════════════════════════════════════════════╝
```

### Cross-Service Reference Summary

| Reference | From | To | Purpose |
|-----------|------|----|---------| 
| userId | customers.Customer | auth.User | Link customer to user account |
| bookId | customers.Favorite | catalog.Book | Store favorite books |
| bookId | customers.PurchaseHistory | catalog.Book | Record purchased books |
| bookId | order.OrderItem | catalog.Book | Link order items to catalog |
| bookId | order.CartItem | catalog.Book | Link cart items to catalog |
| bookId | stock.StockItem | catalog.Book | Link stock to books (one-to-one) |
| orderId | payments.Payment | order.Order | Link payments to orders |
| orderId | stock.Reservation | order.Order | Link reservations to orders |
| customerId | payments.Payment | customers.Customer | Link payments to customers |
| customerId | stock.Reservation | customers.Customer | Link reservations to customers |

### Database Statistics

| Aspect | Count |
|--------|-------|
| Total Entity Classes | 26 |
| Total Database Tables | 18 |
| Total Repositories | 24 |
| Total DTOs | 27+ |
| Total Enums | 13 |
| One-to-One Relationships | 2 |
| One-to-Many Relationships | 12 |
| Cross-Service References | 8 |

---

## 4. REST API Reference

### General API Conventions

**Base URL**: `/api/v1`

**Response Wrapper**: All endpoints return wrapped responses
```json
{
  "success": boolean,
  "message": "string",
  "data": <T>,
  "timestamp": "ISO-8601",
  "path": "string"
}
```

**Pagination**: List endpoints support
```
Query Parameters:
- page: 0 (default, zero-indexed)
- size: 20 (default)

Response includes:
- content: T[] (array of items)
- totalElements: long
- totalPages: int
- currentPage: int
```

**Authentication**: Include JWT token in header
```
Authorization: Bearer <jwt_token>
```

### Complete Endpoint Reference

#### MS-AUTH Endpoints

```
POST /api/v1/auth/register
├─ Request: RegisterRequest { email, username, password, firstName, lastName }
├─ Response: ApiResponse<AuthResponse>
└─ Purpose: User registration

POST /api/v1/auth/login
├─ Request: LoginRequest { email, password }
├─ Response: ApiResponse<AuthResponse> { tokens, user info }
└─ Purpose: User authentication and token generation

POST /api/v1/auth/refresh-token
├─ Request: RefreshTokenRequest { refreshToken }
├─ Response: ApiResponse<AuthResponse> { new tokens }
└─ Purpose: Refresh expired access token

POST /api/v1/auth/logout
├─ Headers: Authorization: Bearer <token>
├─ Response: ApiResponse<Void>
└─ Purpose: Logout and invalidate tokens

GET /api/v1/auth/validate
├─ Headers: Authorization: Bearer <token>
├─ Response: ApiResponse<Boolean> (true if valid)
└─ Purpose: Token validation
```

#### MS-CATALOG Endpoints

```
Books:

POST /api/v1/books
├─ Request: BookDTO
├─ Response: ApiResponse<BookDTO>
└─ Purpose: Create new book

GET /api/v1/books/{id}
├─ Path Param: id (UUID)
├─ Response: ApiResponse<BookDTO>
└─ Purpose: Retrieve book by ID

GET /api/v1/books/isbn/{isbn}
├─ Path Param: isbn (String)
├─ Response: ApiResponse<BookDTO>
└─ Purpose: Retrieve book by ISBN

GET /api/v1/books
├─ Query: page (default 0), size (default 20)
├─ Response: ApiResponse<PaginationDTO<BookDTO>>
└─ Purpose: List all books with pagination

GET /api/v1/books/status/{status}
├─ Path Param: status (BookStatus enum)
├─ Query: page, size
├─ Response: ApiResponse<PaginationDTO<BookDTO>>
└─ Purpose: Filter books by status

GET /api/v1/books/category/{categoryId}
├─ Path Param: categoryId (UUID)
├─ Query: page, size
├─ Response: ApiResponse<PaginationDTO<BookDTO>>
└─ Purpose: Filter books by category

GET /api/v1/books/author/{authorId}
├─ Path Param: authorId (UUID)
├─ Query: page, size
├─ Response: ApiResponse<PaginationDTO<BookDTO>>
└─ Purpose: Filter books by author

GET /api/v1/books/search
├─ Query: title (String), page, size
├─ Response: ApiResponse<PaginationDTO<BookDTO>>
└─ Purpose: Search books by title

PUT /api/v1/books/{id}
├─ Path Param: id (UUID)
├─ Request: BookDTO
├─ Response: ApiResponse<BookDTO>
└─ Purpose: Update book details

DELETE /api/v1/books/{id}
├─ Path Param: id (UUID)
├─ Response: ApiResponse<Void>
└─ Purpose: Delete book

Categories:

POST /api/v1/categories
├─ Request: CategoryDTO
├─ Response: ApiResponse<CategoryDTO>
└─ Purpose: Create category

GET /api/v1/categories/{id}
├─ Path Param: id (UUID)
├─ Response: ApiResponse<CategoryDTO>
└─ Purpose: Retrieve category

GET /api/v1/categories
├─ Query: page, size
├─ Response: ApiResponse<PaginationDTO<CategoryDTO>>
└─ Purpose: List all categories

PUT /api/v1/categories/{id}
├─ Path Param: id (UUID)
├─ Request: CategoryDTO
├─ Response: ApiResponse<CategoryDTO>
└─ Purpose: Update category

DELETE /api/v1/categories/{id}
├─ Path Param: id (UUID)
├─ Response: ApiResponse<Void>
└─ Purpose: Delete category
```

#### MS-CUSTOMERS Endpoints

```
POST /api/v1/customers
├─ Request: CustomerDTO
├─ Response: ApiResponse<CustomerDTO>
└─ Purpose: Create customer

GET /api/v1/customers/{id}
├─ Path Param: id (UUID)
├─ Response: ApiResponse<CustomerDTO>
└─ Purpose: Get customer by ID

GET /api/v1/customers/user/{userId}
├─ Path Param: userId (String)
├─ Response: ApiResponse<CustomerDTO>
└─ Purpose: Get customer by user ID (from ms-auth)

GET /api/v1/customers
├─ Query: page, size
├─ Response: ApiResponse<PaginationDTO<CustomerDTO>>
└─ Purpose: List all customers

PUT /api/v1/customers/{id}
├─ Path Param: id (UUID)
├─ Request: CustomerDTO
├─ Response: ApiResponse<CustomerDTO>
└─ Purpose: Update customer

DELETE /api/v1/customers/{id}
├─ Path Param: id (UUID)
├─ Response: ApiResponse<Void>
└─ Purpose: Delete customer

Profile Endpoints (similar CRUD pattern for profiles)
```

#### MS-ORDER Endpoints

```
Orders:

POST /api/v1/orders
├─ Request: OrderDTO
├─ Response: ApiResponse<OrderDTO>
└─ Purpose: Create order

GET /api/v1/orders/{id}
├─ Path Param: id (UUID)
├─ Response: ApiResponse<OrderDTO>
└─ Purpose: Get order by ID

GET /api/v1/orders/number/{orderNumber}
├─ Path Param: orderNumber (String)
├─ Response: ApiResponse<OrderDTO>
└─ Purpose: Get order by order number

GET /api/v1/orders/customer/{customerId}
├─ Path Param: customerId (String)
├─ Query: page, size
├─ Response: ApiResponse<PaginationDTO<OrderDTO>>
└─ Purpose: Get all orders for customer

GET /api/v1/orders/status/{status}
├─ Path Param: status (OrderStatus enum)
├─ Query: page, size
├─ Response: ApiResponse<PaginationDTO<OrderDTO>>
└─ Purpose: Filter orders by status

PUT /api/v1/orders/{id}
├─ Path Param: id (UUID)
├─ Request: OrderDTO
├─ Response: ApiResponse<OrderDTO>
└─ Purpose: Update order

PUT /api/v1/orders/{id}/status/{status}
├─ Path Params: id (UUID), status (OrderStatus)
├─ Response: ApiResponse<OrderDTO>
└─ Purpose: Update order status

POST /api/v1/orders/{id}/cancel
├─ Path Param: id (UUID)
├─ Response: ApiResponse<Void>
└─ Purpose: Cancel order

DELETE /api/v1/orders/{id}
├─ Path Param: id (UUID)
├─ Response: ApiResponse<Void>
└─ Purpose: Delete order

Shopping Cart:

GET /api/v1/shopping-cart/customer/{customerId}
├─ Path Param: customerId (String)
├─ Response: ApiResponse<ShoppingCartDTO>
└─ Purpose: Get customer's shopping cart

POST /api/v1/shopping-cart/customer/{customerId}/items
├─ Path Param: customerId (String)
├─ Request: CartItemDTO
├─ Response: ApiResponse<ShoppingCartDTO>
└─ Purpose: Add item to cart

DELETE /api/v1/shopping-cart/customer/{customerId}/items/{itemId}
├─ Path Params: customerId, itemId (String)
├─ Response: ApiResponse<ShoppingCartDTO>
└─ Purpose: Remove item from cart

PUT /api/v1/shopping-cart/customer/{customerId}/items/{itemId}
├─ Path Params: customerId, itemId
├─ Query: quantity (Integer)
├─ Response: ApiResponse<ShoppingCartDTO>
└─ Purpose: Update item quantity

DELETE /api/v1/shopping-cart/customer/{customerId}
├─ Path Param: customerId
├─ Response: ApiResponse<ShoppingCartDTO>
└─ Purpose: Clear entire cart

GET /api/v1/shopping-cart/customer/{customerId}/total-items
├─ Path Param: customerId
├─ Response: ApiResponse<Integer>
└─ Purpose: Get total items in cart

Checkout:

POST /api/v1/checkout
├─ Request: CheckoutDTO (customer, cart items, payment method, addresses)
├─ Response: ApiResponse<OrderDTO> (created order)
└─ Purpose: Process checkout and create order
  - Calls ms-catalog to validate books
  - Calls ms-stock to check and reserve items
  - Calls ms-payments to process payment
  - Calls ms-customers to record purchase history

POST /api/v1/checkout/validate
├─ Request: CheckoutDTO
├─ Response: ApiResponse<Void>
└─ Purpose: Validate checkout data without processing
```

#### MS-PAYMENTS Endpoints

```
POST /api/v1/payments
├─ Request: PaymentDTO
├─ Response: ApiResponse<PaymentDTO>
└─ Purpose: Create payment record

GET /api/v1/payments/{id}
├─ Path Param: id (UUID)
├─ Response: ApiResponse<PaymentDTO>
└─ Purpose: Get payment by ID

GET /api/v1/payments/reference/{paymentReference}
├─ Path Param: paymentReference (String)
├─ Response: ApiResponse<PaymentDTO>
└─ Purpose: Get payment by reference

GET /api/v1/payments/order/{orderId}
├─ Path Param: orderId (String)
├─ Response: ApiResponse<PaymentDTO>
└─ Purpose: Get payment for order

GET /api/v1/payments/customer/{customerId}
├─ Path Param: customerId (String)
├─ Query: page, size
├─ Response: ApiResponse<PaginationDTO<PaymentDTO>>
└─ Purpose: Get customer's payments

GET /api/v1/payments/status/{status}
├─ Path Param: status (PaymentStatus)
├─ Query: page, size
├─ Response: ApiResponse<PaginationDTO<PaymentDTO>>
└─ Purpose: Filter by status

POST /api/v1/payments/{id}/process
├─ Path Param: id (UUID)
├─ Response: ApiResponse<PaymentDTO>
└─ Purpose: Process payment (call external gateway)

POST /api/v1/payments/{id}/refund
├─ Path Param: id (UUID)
├─ Response: ApiResponse<Void>
└─ Purpose: Refund payment

Webhooks:

POST /api/v1/webhooks/payment-gateway
├─ Headers: X-Webhook-Signature (for validation)
├─ Request: WebhookPayloadDTO
├─ Response: ApiResponse<Void>
└─ Purpose: Receive gateway webhook events

POST /api/v1/webhooks/confirm/{eventId}
├─ Path Param: eventId (String)
├─ Response: ApiResponse<Void>
└─ Purpose: Confirm webhook receipt

GET /api/v1/webhooks/health
├─ Response: ApiResponse<String>
└─ Purpose: Check webhook service health
```

#### MS-STOCK Endpoints

```
Stock:

POST /api/v1/stock
├─ Request: StockItemDTO
├─ Response: ApiResponse<StockItemDTO>
└─ Purpose: Create stock item

GET /api/v1/stock/{id}
├─ Path Param: id (UUID)
├─ Response: ApiResponse<StockItemDTO>
└─ Purpose: Get stock by ID

GET /api/v1/stock/book/{bookId}
├─ Path Param: bookId (String)
├─ Response: ApiResponse<StockItemDTO>
└─ Purpose: Get stock for book

GET /api/v1/stock/book/{bookId}/available-quantity
├─ Path Param: bookId (String)
├─ Response: ApiResponse<Integer>
└─ Purpose: Get available quantity

GET /api/v1/stock/book/{bookId}/has-stock
├─ Path Param: bookId (String)
├─ Query: requiredQuantity (Integer, default 1)
├─ Response: ApiResponse<Boolean>
└─ Purpose: Check if required quantity available

POST /api/v1/stock/movement
├─ Request: StockMovementDTO
├─ Response: ApiResponse<StockMovementDTO>
└─ Purpose: Record stock movement

GET /api/v1/stock/{stockItemId}/movements
├─ Path Param: stockItemId (String)
├─ Query: page, size
├─ Response: ApiResponse<PaginationDTO<StockMovementDTO>>
└─ Purpose: Get movement history

PUT /api/v1/stock/{id}
├─ Path Param: id (UUID)
├─ Request: StockItemDTO
├─ Response: ApiResponse<StockItemDTO>
└─ Purpose: Update stock

DELETE /api/v1/stock/{id}
├─ Path Param: id (UUID)
├─ Response: ApiResponse<Void>
└─ Purpose: Delete stock item

Reservations:

POST /api/v1/reservations
├─ Request: ReservationDTO
├─ Response: ApiResponse<ReservationDTO>
└─ Purpose: Create reservation

GET /api/v1/reservations/{id}
├─ Path Param: id (UUID)
├─ Response: ApiResponse<ReservationDTO>
└─ Purpose: Get reservation

GET /api/v1/reservations/code/{code}
├─ Path Param: code (String)
├─ Response: ApiResponse<ReservationDTO>
└─ Purpose: Get by reservation code

GET /api/v1/reservations/customer/{customerId}
├─ Path Param: customerId (String)
├─ Response: ApiResponse<List<ReservationDTO>>
└─ Purpose: Get customer's reservations

GET /api/v1/reservations/order/{orderId}
├─ Path Param: orderId (String)
├─ Response: ApiResponse<List<ReservationDTO>>
└─ Purpose: Get order's reservations

POST /api/v1/reservations/{id}/confirm
├─ Path Param: id (UUID)
├─ Response: ApiResponse<ReservationDTO>
└─ Purpose: Confirm reservation

POST /api/v1/reservations/{id}/cancel
├─ Path Param: id (UUID)
├─ Response: ApiResponse<ReservationDTO>
└─ Purpose: Cancel reservation
```

---

## 5. Service-to-Service Communication

### Communication Matrix

```
┌─────────────────────┬─────────────────────┬──────────────────┬────────────────┐
│ Calling Service     │ Called Service      │ Endpoints Called  │ Feign Client   │
├─────────────────────┼─────────────────────┼──────────────────┼────────────────┤
│ BFF-Library         │ MS-Auth             │ GET /validate     │ ✓              │
│ BFF-Library         │ All Services        │ All routes        │ ✓              │
│ MS-Order            │ MS-Catalog          │ GET /books/{id}   │ To implement   │
│ MS-Order            │ MS-Stock            │ GET/POST /stock/* │ To implement   │
│ MS-Order            │ MS-Payments         │ POST /payments    │ To implement   │
│ MS-Order            │ MS-Customers        │ POST /history     │ To implement   │
│ MS-Payments         │ External Gateway    │ POST /*/          │ ✓ Implemented  │
└─────────────────────┴─────────────────────┴──────────────────┴────────────────┘
```

### Feign Client Configuration

#### 1. MS-Order Feign Clients (To be implemented)

```java
// CatalogServiceClient
@FeignClient(name = "catalog-service", url = "${catalog.service.url}")
public interface CatalogServiceClient {
    @GetMapping("/api/v1/books/{id}")
    ApiResponse<BookDTO> getBook(@PathVariable String id);
}

// StockServiceClient
@FeignClient(name = "stock-service", url = "${stock.service.url}")
public interface StockServiceClient {
    @GetMapping("/api/v1/stock/book/{bookId}")
    ApiResponse<StockItemDTO> getStock(@PathVariable String bookId);
    
    @PostMapping("/api/v1/reservations")
    ApiResponse<ReservationDTO> createReservation(@RequestBody ReservationDTO dto);
}

// PaymentServiceClient
@FeignClient(name = "payment-service", url = "${payment.service.url}")
public interface PaymentServiceClient {
    @PostMapping("/api/v1/payments")
    ApiResponse<PaymentDTO> createPayment(@RequestBody PaymentDTO dto);
}

// CustomerServiceClient
@FeignClient(name = "customer-service", url = "${customer.service.url}")
public interface CustomerServiceClient {
    @PostMapping("/api/v1/customers/{customerId}/purchase-history")
    ApiResponse<Void> recordPurchase(@PathVariable String customerId, 
                                     @RequestBody PurchaseHistoryDTO dto);
}
```

#### 2. MS-Payments Feign Client (Already implemented)

```java
@FeignClient(name = "payment-gateway-client", 
             url = "${payment.gateway.url}")
public interface ExternalPaymentGatewayClient {
    
    @PostMapping("/pix/process")
    GatewayResponse processPixPayment(
        @RequestBody PixPaymentRequest request);
    
    @PostMapping("/card/authorize")
    GatewayResponse authorizeCardPayment(
        @RequestBody CardPaymentRequest request);
    
    @PostMapping("/card/capture")
    GatewayResponse captureCardPayment(
        @RequestBody CaptureRequest request);
    
    @PostMapping("/fraud-check")
    FraudCheckResponse performFraudCheck(
        @RequestBody FraudCheckRequest request);
    
    @PostMapping("/refund")
    GatewayResponse refund(@RequestBody RefundRequest request);
}
```

### Service Dependencies Configuration

**application.properties** should include:

```properties
# Service URLs (for Feign clients)
catalog.service.url=http://localhost:8082
stock.service.url=http://localhost:8086
payment.service.url=http://localhost:8085
customer.service.url=http://localhost:8083
auth.service.url=http://localhost:8081

# External Gateway
payment.gateway.url=https://api.payment-gateway.com/v1

# Service Timeouts
feign.client.config.default.connectTimeout=10000
feign.client.config.default.readTimeout=30000
```

---

## 6. Data Flow Examples

### Flow 1: User Registration to First Order

```
1. CLIENT → BFF: POST /api/v1/auth/register
   BFF → MS-AUTH: POST /api/v1/auth/register
   MS-AUTH → MySQL: INSERT INTO users
   MS-AUTH → CLIENT: Return JWT tokens
   
2. CLIENT → BFF: POST /api/v1/customers
   BFF → MS-CUSTOMERS: POST /api/v1/customers
   MS-CUSTOMERS → MySQL: INSERT INTO customers
   CLIENT: Customer profile created

3. CLIENT → BFF: POST /api/v1/shopping-cart/items
   BFF → MS-ORDER: POST /api/v1/shopping-cart/customer/{id}/items
   BFF → MS-CATALOG: GET /api/v1/books/{id} (validate book)
   MS-ORDER → MySQL: INSERT INTO cart_items
   CLIENT: Item added to cart

4. CLIENT → BFF: POST /api/v1/checkout
   BFF → MS-ORDER: POST /api/v1/checkout
   MS-ORDER → MS-CATALOG: GET /api/v1/books/{id} (validate prices)
   MS-ORDER → MS-STOCK: GET /api/v1/stock/book/{id} (check availability)
   MS-ORDER → MS-STOCK: POST /api/v1/reservations (reserve items)
   MS-ORDER → MS-PAYMENTS: POST /api/v1/payments (process payment)
   MS-PAYMENTS → ExternalGateway: POST /authorize, /capture
   MS-ORDER → MS-CUSTOMERS: POST /purchase-history (record purchase)
   MS-ORDER → MySQL: INSERT INTO orders, order_items
   MS-STOCK → MySQL: UPDATE reservations
   CLIENT: Order confirmed
```

### Flow 2: Payment Processing with Fraud Check

```
1. CLIENT → BFF: POST /api/v1/checkout
   BFF → MS-ORDER: POST /api/v1/checkout
   MS-ORDER → MS-PAYMENTS: POST /api/v1/payments
   
2. MS-PAYMENTS → MySQL: INSERT INTO payments (status=PENDING)
   
3. MS-PAYMENTS → FraudCheckService: Check fraud score
   MS-PAYMENTS → ExternalGateway: POST /fraud-check
   ExternalGateway → MS-PAYMENTS: Fraud results
   MS-PAYMENTS → MySQL: INSERT INTO fraud_check_results
   
4. If fraudLevel <= MEDIUM:
   MS-PAYMENTS → ExternalGateway: POST /card/authorize
   ExternalGateway → MS-PAYMENTS: Authorization code
   MS-PAYMENTS → MySQL: INSERT INTO transactions (status=AUTHORIZED)
   
   MS-PAYMENTS → ExternalGateway: POST /card/capture
   ExternalGateway → MS-PAYMENTS: Capture confirmation
   MS-PAYMENTS → MySQL: UPDATE transactions (status=CAPTURED)
   MS-PAYMENTS → MySQL: UPDATE payments (status=COMPLETED)
   
   MS-PAYMENTS → WebhookService: Trigger webhook
   
5. Else:
   MS-PAYMENTS → MySQL: UPDATE payments (status=FAILED)
   MS-PAYMENTS → CLIENT: Payment declined (fraud score too high)
```

### Flow 3: Stock Reservation and Fulfillment

```
1. MS-ORDER → MS-STOCK: POST /api/v1/reservations
   {
     "stockItemId": "uuid",
     "customerId": "uuid",
     "orderId": "uuid",
     "reservedQuantity": 2,
     "expiresAt": "2026-06-01T10:00:00"
   }
   
2. MS-STOCK → MySQL: 
   INSERT INTO reservations (status=PENDING)
   UPDATE stock_items SET reservedQuantity += 2, availableQuantity -= 2
   
3. [After payment confirmed]
   MS-ORDER → MS-STOCK: POST /api/v1/reservations/{id}/confirm
   
4. MS-STOCK → MySQL:
   UPDATE reservations SET status=CONFIRMED, confirmedAt=NOW()
   INSERT INTO stock_movements 
     (movementType=RESERVATION, quantity=2, reference=orderId)
   
5. [If reservation expires without confirmation]
   Background Job → MS-STOCK: Finds expired reservations
   MS-STOCK → MySQL:
   UPDATE reservations SET status=EXPIRED, cancelledAt=NOW()
   UPDATE stock_items SET reservedQuantity -= 2, availableQuantity += 2
```

### Flow 4: Book Search with Pagination

```
1. CLIENT → BFF: GET /api/v1/books/search?title=Harry&page=0&size=20
   
2. BFF → MS-CATALOG: GET /api/v1/books/search?title=Harry&page=0&size=20
   
3. MS-CATALOG → MySQL: 
   SELECT * FROM books 
   WHERE LOWER(title) LIKE '%harry%'
   LIMIT 20 OFFSET 0
   
4. MS-CATALOG → MySQL:
   SELECT COUNT(*) FROM books 
   WHERE LOWER(title) LIKE '%harry%'
   
5. MS-CATALOG → BFF:
   {
     "success": true,
     "data": {
       "content": [...20 books...],
       "totalElements": 45,
       "totalPages": 3,
       "currentPage": 0
     }
   }
   
6. BFF → CLIENT: Display results with pagination controls
```

---

## 7. Implementation Patterns

### Entity Creation Pattern

```java
@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(unique = true, nullable = false)
    private String isbn;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Enumerated(EnumType.STRING)
    private BookStatus status;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### Repository Pattern with Custom Queries

```java
@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    
    // Simple finders
    Optional<Book> findByIsbn(String isbn);
    
    // Pagination methods
    Page<Book> findByStatus(BookStatus status, Pageable pageable);
    Page<Book> findByAuthorId(String authorId, Pageable pageable);
    Page<Book> findByCategoryId(String categoryId, Pageable pageable);
    
    // Search methods
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    // Complex queries
    Page<Book> findByAuthorIdAndStatus(String authorId, BookStatus status, Pageable pageable);
    
    // Custom JPQL queries
    @Query("SELECT b FROM Book b WHERE b.status = :status AND b.category.id = :categoryId")
    Page<Book> findAvailableByCategory(@Param("categoryId") String categoryId, 
                                       @Param("status") BookStatus status, 
                                       Pageable pageable);
}
```

### DTO and Mapping Pattern

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {
    private String id;
    private String title;
    private String isbn;
    private String description;
    private BigDecimal basePrice;
    private String authorId;
    private String authorName;
    private String categoryId;
    private String categoryName;
    private BookStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Mapping examples in Service
public BookDTO convertToDTO(Book book) {
    return BookDTO.builder()
        .id(book.getId())
        .title(book.getTitle())
        .isbn(book.getIsbn())
        .description(book.getDescription())
        .basePrice(book.getBasePrice())
        .authorId(book.getAuthor().getId())
        .authorName(book.getAuthor().getName())
        .categoryId(book.getCategory().getId())
        .categoryName(book.getCategory().getName())
        .status(book.getStatus())
        .createdAt(book.getCreatedAt())
        .updatedAt(book.getUpdatedAt())
        .build();
}
```

### Service Layer Pattern

```java
@Service
@RequiredArgsConstructor
public class BookService {
    
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    
    // Create
    public BookDTO createBook(BookDTO dto) {
        Author author = authorRepository.findById(dto.getAuthorId())
            .orElseThrow(() -> new AuthorNotFoundException());
        
        Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException());
        
        Book book = Book.builder()
            .title(dto.getTitle())
            .isbn(dto.getIsbn())
            .description(dto.getDescription())
            .basePrice(dto.getBasePrice())
            .author(author)
            .category(category)
            .status(BookStatus.AVAILABLE)
            .build();
        
        Book saved = bookRepository.save(book);
        return convertToDTO(saved);
    }
    
    // Read
    public BookDTO getBook(String id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException());
        return convertToDTO(book);
    }
    
    // Search with pagination
    public Page<BookDTO> searchBooks(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Book> books = bookRepository.findByTitleContainingIgnoreCase(title, pageable);
        return books.map(this::convertToDTO);
    }
    
    // Update
    public BookDTO updateBook(String id, BookDTO dto) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException());
        
        book.setTitle(dto.getTitle());
        book.setDescription(dto.getDescription());
        book.setBasePrice(dto.getBasePrice());
        // ... more field updates
        
        Book updated = bookRepository.save(book);
        return convertToDTO(updated);
    }
    
    // Delete
    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }
}
```

### Controller Pattern

```java
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    
    private final BookService bookService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<BookDTO>> createBook(@RequestBody BookDTO dto) {
        BookDTO created = bookService.createBook(dto);
        return ResponseEntity.ok(
            ApiResponse.<BookDTO>builder()
                .success(true)
                .message("Book created successfully")
                .data(created)
                .build()
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDTO>> getBook(@PathVariable String id) {
        BookDTO book = bookService.getBook(id);
        return ResponseEntity.ok(
            ApiResponse.<BookDTO>builder()
                .success(true)
                .data(book)
                .build()
        );
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BookDTO>>> searchBooks(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<BookDTO> books = bookService.searchBooks(title, page, size);
        return ResponseEntity.ok(
            ApiResponse.<Page<BookDTO>>builder()
                .success(true)
                .data(books)
                .build()
        );
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDTO>> updateBook(
            @PathVariable String id,
            @RequestBody BookDTO dto) {
        BookDTO updated = bookService.updateBook(id, dto);
        return ResponseEntity.ok(
            ApiResponse.<BookDTO>builder()
                .success(true)
                .message("Book updated successfully")
                .data(updated)
                .build()
        );
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(
            ApiResponse.<Void>builder()
                .success(true)
                .message("Book deleted successfully")
                .build()
        );
    }
}
```

### Exception Handling Pattern

```java
@Getter
@AllArgsConstructor
public class BookNotFoundException extends RuntimeException {
    private final String message;
    
    public BookNotFoundException() {
        this("Book not found");
    }
}

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleBookNotFound(
            BookNotFoundException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
}
```

---

## 8. Entity/DTO Reference

### Complete Entity List

**MS-Auth Entities** (1):
1. User - User accounts and authentication

**MS-Catalog Entities** (4):
1. Book - Book catalog records
2. Author - Book authors
3. Category - Book categories
4. Editor - Publishing companies

**MS-Customers Entities** (6):
1. Customer - Customer profiles
2. Profile - Extended profile info
3. Address - Delivery/billing addresses
4. Favorite - Favorite books
5. Preference - User preferences
6. PurchaseHistory - Purchase records

**MS-Order Entities** (4):
1. Order - Customer orders
2. OrderItem - Order line items
3. ShoppingCart - Active carts
4. CartItem - Cart items

**MS-Payments Entities** (5):
1. Payment - Payment records
2. PaymentMethod - Payment methods
3. Transaction - Payment transactions
4. WebhookEvent - Gateway webhooks
5. FraudCheckResult - Fraud assessments

**MS-Stock Entities** (4):
1. StockItem - Inventory records
2. StockMovement - Inventory movements
3. Reservation - Item reservations
4. Availability - Availability tracking

### Enum Reference

| Enum Name | Values | Used In |
|-----------|--------|---------|
| **BookStatus** | AVAILABLE, OUT_OF_STOCK, DISCONTINUED, ARCHIVED, COMING_SOON | Book |
| **CustomerStatus** | ACTIVE, INACTIVE, SUSPENDED, DELETED | Customer |
| **OrderStatus** | PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED, ON_HOLD, AWAITING_PAYMENT | Order |
| **PaymentStatus** | PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED, DISPUTED, EXPIRED, AWAITING_CONFIRMATION | Payment |
| **PaymentType** | CREDIT_CARD, DEBIT_CARD, PIX, BANK_TRANSFER, DIGITAL_WALLET, BOLETO | Payment, PaymentMethod |
| **TransactionStatus** | PENDING, AUTHORIZED, CAPTURED, DECLINED, FAILED, CANCELLED, REFUNDED, CHARGEBACK | Transaction |
| **FraudLevel** | LOW, MEDIUM, HIGH | FraudCheckResult |
| **MovementType** | ENTRY, EXIT, RESERVATION, RETURN, ADJUSTMENT, DAMAGE, LOSS, TRANSFER | StockMovement |
| **ReservationStatus** | PENDING, CONFIRMED, CANCELLED, EXPIRED, COMPLETED | Reservation |
| **AvailabilityStatus** | AVAILABLE, LOW_STOCK, OUT_OF_STOCK, DISCONTINUED, RESTOCKING | StockItem |
| **PreferenceType** | CATEGORY, AUTHOR, NOTIFICATION_EMAIL, NOTIFICATION_SMS, LANGUAGE, CURRENCY, THEME | Preference |
| **Role** | ADMIN, USER, CUSTOMER, GUEST | Shared |
| **TokenType** | ACCESS, REFRESH | Shared |

---

## 9. Quick Start for Development

### Setting up Development Environment

1. **Clone all repositories**
```bash
git clone https://github.com/codewesleylima/bff-wzzy-library.git
git clone https://github.com/codewesleylima/ms-wzzy-auth.git
git clone https://github.com/codewesleylima/ms-wzzy-catalog.git
git clone https://github.com/codewesleylima/ms-wzzy-customers.git
git clone https://github.com/codewesleylima/ms-wzzy-order.git
git clone https://github.com/codewesleylima/ms-wzzy-payments.git
git clone https://github.com/codewesleylima/ms-wzzy-stock.git
```

2. **Configure environment variables**
Create `.env` file in parent directory:
```
DB_URL=jdbc:mysql://localhost:3306/WZZY_LIBRARY
DB_USER=root
DB_PASSWORD=wzzyroot
payment.gateway.url=<your-payment-gateway-url>
```

3. **Create MySQL database**
```sql
CREATE DATABASE WZZY_LIBRARY
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;
```

4. **Run each service**
```bash
# Terminal 1: MS-Auth
cd ms-auth
./gradlew bootRun

# Terminal 2: MS-Catalog
cd ms-catalog
./gradlew bootRun

# Terminal 3: MS-Customers
cd ms-customers
./gradlew bootRun

# Terminal 4: MS-Order
cd ms-order
./gradlew bootRun

# Terminal 5: MS-Payments
cd ms-payments
./gradlew bootRun

# Terminal 6: MS-Stock
cd ms-stock
./gradlew bootRun

# Terminal 7: BFF-Library
cd bff-library
./gradlew bootRun
```

### Understanding Code Organization

```
Each Service Structure:
├── src/main/java/com/wzzy/library/{service}/
│   ├── entity/          # JPA entities
│   ├── repository/      # Spring Data repositories
│   ├── dto/             # Data Transfer Objects
│   ├── controller/      # REST controllers
│   ├── service/         # Business logic services
│   ├── enums/           # Enum classes
│   ├── exception/       # Custom exceptions
│   ├── client/          # Feign clients (if applicable)
│   └── {service}Application.java  # Main class
│
├── src/test/java/      # Unit tests
├── build.gradle        # Gradle configuration
└── application.properties  # Spring configuration
```

### Finding Specific Functionality

| Functionality | Location |
|--------------|----------|
| Book management | ms-catalog/service/BookService.java |
| Shopping cart | ms-order/service/ShoppingCartService.java |
| Payment processing | ms-payments/service/PaymentService.java |
| Stock management | ms-stock/service/StockService.java |
| Customer profiles | ms-customers/service/CustomerService.java |
| Authentication | ms-auth/service/AuthService.java |
| API responses | bff-library/dto/ApiResponse.java |
| Exception handling | */exception/*.java |

### How to Add a New Feature

#### Example: Add "Reviews" to Books

1. **Create Entity** (ms-catalog)
   - File: `entity/Review.java`
   - Links to Book (ManyToOne)
   - Fields: rating, comment, customerId, createdAt

2. **Create Repository** (ms-catalog)
   - File: `repository/ReviewRepository.java`
   - Methods: findByBookId, findByCustomerId

3. **Create DTO** (ms-catalog)
   - File: `dto/ReviewDTO.java`
   - Fields matching Review entity

4. **Create Service** (ms-catalog)
   - File: `service/ReviewService.java`
   - Methods: createReview, getReviews, deleteReview

5. **Create Controller** (ms-catalog)
   - File: `controller/ReviewController.java`
   - Endpoints: POST /reviews, GET /reviews/book/{id}, DELETE /reviews/{id}

6. **Update Database** (ms-catalog/src/main/resources/)
   - Spring Data JPA will auto-create table with `ddl-auto=update`

---

## 10. Dependencies and Integration Points

### MySQL Database Setup

**Connection Details** (from .env):
```
URL: jdbc:mysql://localhost:3306/WZZY_LIBRARY
User: root
Password: wzzyroot
```

**Tables Created** (7 databases total, one per service):
- Each service can have its own database or share (configure in DB_URL)
- Spring Data JPA auto-creates tables from entities
- `spring.jpa.hibernate.ddl-auto=update` allows schema evolution

**Backup Strategy**:
```bash
# Backup
mysqldump -u root -p WZZY_LIBRARY > backup.sql

# Restore
mysql -u root -p WZZY_LIBRARY < backup.sql
```

### External Payment Gateway Integration

**Feign Client**: `ExternalPaymentGatewayClient.java`

**Endpoints**:
- `POST /pix/process` - Process PIX transfer
- `POST /card/authorize` - Authorize card payment
- `POST /card/capture` - Capture authorized payment
- `POST /fraud-check` - Fraud check before payment
- `POST /refund` - Process refunds

**Configuration** (application.properties):
```properties
payment.gateway.url=https://api.payment-gateway.com/v1
payment.gateway.api.key=<your-api-key>
payment.gateway.timeout=30000
```

**Webhook Integration**:
- Receives `POST /api/v1/webhooks/payment-gateway`
- Validates signature: `X-Webhook-Signature` header
- Updates payment status in database
- Triggers order fulfillment if successful

### JWT Token Handling

**Token Generation** (MS-Auth):
```java
// Access Token
JwtToken accessToken = new JwtToken();
accessToken.setTokenType(TokenType.ACCESS);
accessToken.setExpiresIn(900); // 15 minutes
accessToken.setIssuedAt(LocalDateTime.now());

// Refresh Token
JwtToken refreshToken = new JwtToken();
refreshToken.setTokenType(TokenType.REFRESH);
refreshToken.setExpiresIn(604800); // 7 days
```

**Token Validation** (BFF-Library):
- All requests include: `Authorization: Bearer <token>`
- BFF calls `GET /api/v1/auth/validate` to verify token
- Sets authenticated user context
- Routes request to appropriate service

**Token Refresh**:
- Client: `POST /api/v1/auth/refresh-token` with refreshToken
- MS-Auth validates refresh token
- Returns new accessToken and refreshToken

### Spring Cloud OpenFeign Configuration

**In build.gradle**:
```gradle
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
}

ext {
    set('springCloudVersion', "2024.0.0")
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}
```

**In Application class**:
```java
@SpringBootApplication
@EnableFeignClients
public class MsOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsOrderApplication.class, args);
    }
}
```

**Feign Configuration** (application.properties):
```properties
feign.client.config.default.connectTimeout=10000
feign.client.config.default.readTimeout=30000
feign.client.config.default.loggerLevel=basic
```

---

## Conclusion

This WZZY Library microservices architecture provides a scalable, maintainable foundation for building a digital library management system. Each microservice is independently deployable and testable while remaining loosely coupled through REST APIs and Feign clients.

**Key Principles**:
- **Separation of Concerns**: Each service owns its business logic and data
- **REST API Standards**: Consistent endpoint naming and response formats
- **Service Orchestration**: MS-Order coordinates multi-service workflows
- **Database Per Service**: Autonomy and independent scaling
- **Shared Utilities**: Common DTOs and exceptions in bff-library

**For Support**:
- Refer to individual service README.md files
- Check endpoint documentation in Section 4
- Review implementation patterns in Section 7
- Follow quick start guide in Section 9

**Future Enhancements**:
- API Gateway authentication filter
- Circuit breaker for Feign clients
- Event-driven architecture (Kafka/RabbitMQ)
- API versioning strategy
- Rate limiting and throttling
- Distributed tracing (Sleuth + Zipkin)
- Service mesh (Istio) for production

---

*Last Updated: 2026-05-25*  
*Version: 1.0.0*  
*Author: Wesley Lima*
