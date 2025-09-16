# 🛒 E-Commerce Backend API

A production-ready, secure E-Commerce backend API built with Spring Boot, demonstrating OWASP Top 10 security practices and modern development standards.

## 🚀 Features

### 🔐 **Security Features (OWASP Top 10 Aligned)**

1. **Broken Access Control Prevention**
   - Role-based access control (RBAC) with `@PreAuthorize` annotations
   - JWT-based authentication with proper token validation
   - User-specific data access restrictions

2. **Cryptographic Failures Protection**
   - BCrypt password hashing with high strength (12 rounds)
   - Secure JWT token generation with HMAC-SHA512
   - HTTPS enforcement in production

3. **Injection Prevention**
   - Parameterized JPA queries to prevent SQL injection
   - Input validation using Bean Validation
   - XSS protection headers

4. **Insecure Design Mitigation**
   - Business rule enforcement (users can only access own data)
   - Account lockout after failed login attempts
   - Proper error handling without information disclosure

5. **Security Misconfiguration Prevention**
   - Secure HTTP headers (CSP, HSTS, X-Frame-Options)
   - Disabled default error pages
   - Secure actuator endpoints

6. **Authentication & Session Management**
   - Strong password policy enforcement
   - JWT token expiration and refresh mechanism
   - Account lockout and security monitoring

### 🏪 **Core E-Commerce Features**

- **User Management**: Registration, authentication, profile management
- **Product Catalog**: CRUD operations, search, filtering, pagination
- **Shopping Cart**: Add/remove items, persistent user carts
- **Order Management**: Place orders, track status, order history
- **Admin Functions**: Product management, order status updates

### 🛠 **Technical Features**

- **REST API** with OpenAPI/Swagger documentation
- **Database**: PostgreSQL with JPA/Hibernate
- **Authentication**: JWT tokens with refresh capability
- **Security**: Spring Security with comprehensive protection
- **Testing**: Unit and integration tests
- **Containerization**: Docker and Docker Compose
- **Monitoring**: Spring Boot Actuator
- **Logging**: Structured logging with security events

## 🏗 Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │    Business     │    │   Persistence   │
│     Layer       │────│     Layer       │────│     Layer       │
│  (Controllers)  │    │   (Services)    │    │ (Repositories)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Cross-Cutting │
                    │   Concerns      │
                    │ (Security, etc) │
                    └─────────────────┘
```

## 🚦 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Docker** and **Docker Compose**
- **PostgreSQL 12+** (if running locally)

## ⚡ Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd ecommerce-backend
```

### 2. Using Docker Compose (Recommended)
```bash
# Start all services
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f ecommerce-backend
```

### 3. Local Development Setup
```bash
# Create PostgreSQL database
createdb ecommerce_db

# Set environment variables
export DATABASE_URL=jdbc:postgresql://localhost:5432/ecommerce_db
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=your_password
export JWT_SECRET=your-256-bit-secret

# Run the application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DATABASE_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/ecommerce_db` | Yes |
| `DATABASE_USERNAME` | Database username | `postgres` | Yes |
| `DATABASE_PASSWORD` | Database password | `password` | Yes |
| `JWT_SECRET` | JWT signing secret (min 256 bits) | - | Yes |
| `JWT_EXPIRATION` | JWT token expiration (ms) | `86400000` | No |
| `SPRING_PROFILES_ACTIVE` | Active profile | `dev` | No |

### Application Profiles

- **`dev`**: Development with H2 in-memory database, verbose logging
- **`prod`**: Production with PostgreSQL, optimized settings
- **`test`**: Testing configuration

## 📚 API Documentation

### Base URL
- Development: `http://localhost:8080/api/v1`
- Production: `https://your-domain.com/api/v1`

### Swagger UI
- Access interactive API docs at: `http://localhost:8080/api/v1/swagger-ui.html`

### Authentication Flow

1. **Login**
   ```bash
   POST /auth/login
   {
     "usernameOrEmail": "user@example.com",
     "password": "SecurePassword123!"
   }
   ```

2. **Use Access Token**
   ```bash
   Authorization: Bearer <access_token>
   ```

3. **Refresh Token**
   ```bash
   POST /auth/refresh
   Authorization: Bearer <refresh_token>
   ```

### Key Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/login` | User login | No |
| POST | `/auth/refresh` | Refresh token | No |
| GET | `/products` | List products | No |
| GET | `/products/{id}` | Get product details | No |
| POST | `/products` | Create product | Admin |
| GET | `/cart` | Get user's cart | User |
| POST | `/cart/items` | Add item to cart | User |
| GET | `/orders` | Get user's orders | User |
| POST | `/orders` | Place order | User |
| GET | `/admin/orders` | Admin order management | Admin |

## 🧪 Testing

### Run Tests
```bash
# Unit tests
./mvnw test

# Integration tests
./mvnw verify

# With coverage
./mvnw test jacoco:report
```

### Security Testing
```bash
# OWASP Dependency Check
./mvnw org.owasp:dependency-check-maven:check

# Security headers test
curl -I http://localhost:8080/api/v1/actuator/health
```

## 🔒 Security Considerations

### Password Policy
- Minimum 8 characters
- Must contain uppercase, lowercase, number, and special character
- Passwords are hashed with BCrypt (strength 12)

### Account Lockout
- Account locked after 5 failed login attempts
- 30-minute lockout period
- Security events logged for monitoring

### JWT Tokens
- Access tokens expire in 24 hours
- Refresh tokens expire in 7 days
- Tokens signed with HMAC-SHA512

### Security Headers
- `X-Frame-Options: DENY`
- `X-Content-Type-Options: nosniff`
- `X-XSS-Protection: 1; mode=block`
- `Strict-Transport-Security: max-age=31536000; includeSubDomains`
- `Content-Security-Policy: default-src 'self'`

## 🚀 Deployment

### Docker Production Deployment
```bash
# Build and deploy
docker-compose -f docker-compose.prod.yml up -d

# Scale application
docker-compose -f docker-compose.prod.yml up -d --scale ecommerce-backend=3
```

### Environment Setup
```bash
# Create production environment file
cp .env.example .env

# Edit environment variables
nano .env
```

## 📊 Monitoring & Logging

### Health Checks
- Application: `http://localhost:8080/api/v1/actuator/health`
- Database: Automatic connection validation
- JWT Service: Token validation endpoints

### Logging
- Application logs: `logs/ecommerce-backend.log`
- Security events: Separate security log stream
- Audit trail: User actions and authentication events

### Metrics
- JVM metrics via Actuator
- Custom business metrics
- Database connection pool monitoring

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

### Code Standards
- Follow Java coding conventions
- Write unit tests for new features
- Update documentation
- Run security checks

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Check what's using port 8080
   lsof -i :8080
   
   # Kill the process
   kill -9 <PID>
   ```

2. **Database Connection Issues**
   ```bash
   # Check PostgreSQL status
   docker-compose ps postgresql
   
   # Check logs
   docker-compose logs postgresql
   ```

3. **JWT Token Issues**
   - Ensure JWT_SECRET is at least 256 bits
   - Check token expiration settings
   - Verify token format in Authorization header

### Getting Help

- Check the logs: `docker-compose logs ecommerce-backend`
- Verify environment variables
- Ensure all services are running: `docker-compose ps`

## 🎯 Future Enhancements

- [ ] OAuth2 integration (Google, GitHub)
- [ ] Redis caching layer
- [ ] Elasticsearch for product search
- [ ] Email notifications
- [ ] Payment gateway integration
- [ ] Advanced analytics dashboard
- [ ] Multi-language support
- [ ] Rate limiting with Redis
- [ ] API versioning strategy

---

**Built with ❤️ using Spring Boot and security best practices**