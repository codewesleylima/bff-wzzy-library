<p align="center">
  <img src="https://iili.io/3FFO5cF.png" alt="Universidade Católica de Brasília">
</p>

## PERSONAL PROJECT - BFF-WZZY-LIBRARY 🚪

### 📖 Description
Backend for Frontend responsible for **orchestrating requests and responses** across the library's microservices. BFF-WZZY-Library is responsible for:
- 🚪 **Route requests** to appropriate microservices
- 🔐 **Validate authentication tokens** from all requests
- 📦 **Aggregate responses** from multiple services
- 🔄 **Transform data** between frontend and backend formats
- ⚡ **Cache responses** for improved performance
- 📊 **Log and monitor** all API traffic
- 🛡️ **Apply rate limiting** and security policies
- 🔗 **Integrate with external** services when needed

This service is part of a microservices architecture composed of:

- [`bff-wzzy-library`](https://github.com/codewesleylima/bff-wzzy-library) – Central gateway and orchestration
- [`ms-wzzy-auth`](https://github.com/codewesleylima/ms-wzzy-auth) – Authentication and JWT token management
- [`ms-wzzy-catalog`](https://github.com/codewesleylima/ms-wzzy-catalog) – Book catalog, authors, publishers, and categories
- [`ms-wzzy-customers`](https://github.com/codewesleylima/ms-wzzy-customers) – Customer profile, address, and preference management
- [`ms-wzzy-order`](https://github.com/codewesleylima/ms-wzzy-order) – Shopping cart and order orchestration
- [`ms-wzzy-payments`](https://github.com/codewesleylima/ms-wzzy-payments) – Payment processing and gateway integration
- [`ms-wzzy-stock`](https://github.com/codewesleylima/ms-wzzy-stock) – Inventory and availability management

---

## 🔁 Request Flow

**Standard Request Flow:**

1. **Client** → BFF: Sends API request with JWT token
2. BFF → **ms-auth**: Validates token
3. **ms-auth** → BFF: Returns validation result
4. BFF → Appropriate Service: Routes request to target microservice
5. Service → Database: Executes business logic
6. Service → BFF: Returns response data
7. BFF → **Client**: Transforms and returns formatted response

**Aggregation Flow (Multiple Services):**

1. **Client** → BFF: Requests aggregated data
2. BFF → **ms-catalog**: Fetches product information
3. BFF → **ms-stock**: Fetches availability information
4. BFF → **ms-customers**: Fetches customer preferences
5. **All services** → BFF: Return their respective data
6. BFF → **Client**: Aggregates and returns combined response

---

### ⚡ Features

1. 🚪 Route requests to appropriate microservices
2. 🔐 Validate JWT tokens on all requests
3. 📦 Aggregate responses from multiple services
4. 🔄 Transform request/response data formats
5. ⚡ Cache responses for performance optimization
6. 📊 Log all API traffic for monitoring
7. 🛡️ Apply rate limiting and security policies
8. 🔗 Handle external service integrations
9. ❌ Handle error responses from services gracefully
10. 📈 Monitor performance metrics

---

### 🖥️ **Running Locally**

To run the project locally:

```sh
./gradlew bootRun
```

> 💡 Make sure all microservices (ms-auth, ms-catalog, ms-customers, ms-order, ms-payments, ms-stock) are running and accessible.

---

#### 🛠️ Technologies Used

- ☕ Java 21
- 🍃 Spring Boot 3
- 🌐 Spring Cloud Gateway (routing and load balancing)
- 🔐 Spring Security with JWT
- 📦 Spring Data JPA
- 🌐 Spring Cloud OpenFeign (service-to-service communication)
- 📊 Spring Boot Actuator (monitoring)
- 🧪 JUnit / Mockito
- 🔧 Gradle

---

### 🛺 Author

<table>
  <tr>
    <td align="center">
      <a href="https://www.linkedin.com/in/wesslima/" title="Wesley Lima">
        <img src="https://media.licdn.com/dms/image/v2/D4D03AQH8pgDMsT7zMw/profile-displayphoto-crop_800_800/B4DZs03OodH8AM-/0/1766118457145?e=1781136000&v=beta&t=-N2WNA9CWJ7Io6nX33GPNvYtFl9ZQMAM-jALLlYNGc8" width="100px;" alt="Wesley Lima Photo"/><br>
        <sub>
          <b>Wesley Lima</b>
        </sub>
      </a>
    </td>
  </tr>
</table>
