# E-Commerce Platform

A full-stack e-commerce application built with **Next.js 15** frontend and **Spring Boot 3** backend.

## 🚀 Features

### Frontend (Next.js)
- 🏪 Product catalog with search and filtering
- 🛒 Shopping cart functionality
- 👤 User authentication (login/register)
- 👨‍💼 Admin dashboard
- 📱 Responsive design with Tailwind CSS
- 🔐 JWT-based authentication
- 🛡️ Protected routes and role-based access
- 📦 Order management
- 🎨 Modern UI with Lucide icons

### Backend (Spring Boot)
- 🔐 JWT Authentication & Authorization
- 👤 User management with roles (USER, ADMIN)
- 🏪 Product management (CRUD operations)
- 🛒 Shopping cart operations
- 📦 Order processing
- 🛡️ Spring Security integration
- 🗄️ PostgreSQL/H2 database support
- 📚 OpenAPI/Swagger documentation
- ⚡ RESTful API design
- 🔄 Transaction management

## 🛠️ Tech Stack

### Frontend
- **Framework**: Next.js 15
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **State Management**: Zustand
- **Form Handling**: React Hook Form + Zod
- **HTTP Client**: Axios
- **Icons**: Lucide React
- **Notifications**: React Hot Toast

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Security**: Spring Security + JWT
- **Database**: PostgreSQL (production), H2 (development)
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven
- **Documentation**: SpringDoc OpenAPI 3
- **Validation**: Bean Validation (Hibernate Validator)

## 📋 Prerequisites

- **Java 17+** (for backend)
- **Node.js 18+** (for frontend)
- **PostgreSQL 12+** (for production, optional for development)
- **Maven 3.8+** (for backend build)
- **Git** (for version control)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/ecommerce-platform.git
cd ecommerce-platform
```

### 2. Backend Setup

#### Using H2 (Development - Recommended for quick start)
```bash
cd ecommerce-backend

# Run with development profile (uses H2 in-memory database)
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

#### Using PostgreSQL (Production)
```bash
# Install and start PostgreSQL
sudo systemctl start postgresql

# Create database
sudo -u postgres createdb ecommerce_db

# Update credentials in application.yml if needed
cd ecommerce-backend
mvn spring-boot:run
```

The backend will be available at `http://localhost:8080`

### 3. Frontend Setup
```bash
cd ecommerce-frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will be available at `http://localhost:3000`

## 🔑 Default Accounts

The application comes with pre-seeded accounts:

| Role  | Username | Password  | Email |
|-------|----------|-----------|-------|
| Admin | admin    | admin1234 | admin@example.com |
| User  | user     | user1234  | user@example.com |

## 📁 Project Structure

```
ecommerce-platform/
├── ecommerce-backend/          # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ecommerce/backend/
│   │   │   │   ├── controller/     # REST controllers
│   │   │   │   ├── service/        # Business logic
│   │   │   │   ├── entity/         # JPA entities
│   │   │   │   ├── repository/     # Data repositories
│   │   │   │   ├── dto/            # Data transfer objects
│   │   │   │   ├── config/         # Configuration classes
│   │   │   │   ├── security/       # Security configuration
│   │   │   │   └── exception/      # Exception handlers
│   │   │   └── resources/
│   │   │       ├── application.yml # Configuration
│   │   │       └── data.sql        # Initial data
│   │   └── test/                   # Test classes
│   ├── target/                     # Build output
│   └── pom.xml                     # Maven configuration
└── ecommerce-frontend/             # Next.js frontend
    ├── src/
    │   ├── app/                    # Next.js App Router
    │   │   ├── (pages)/           # Application pages
    │   │   ├── globals.css        # Global styles
    │   │   └── layout.tsx         # Root layout
    │   ├── components/            # Reusable components
    │   ├── store/                 # Zustand stores
    │   └── lib/                   # Utilities
    ├── public/                    # Static assets
    ├── package.json              # Dependencies
    └── tailwind.config.js        # Tailwind configuration
```

## 🔧 Configuration

### Backend Configuration
Key configuration files:
- `src/main/resources/application.yml` - Main configuration
- `src/main/resources/data.sql` - Initial data seeding

### Frontend Configuration
Key configuration files:
- `.env.local` - Environment variables
- `tailwind.config.js` - Tailwind CSS configuration
- `next.config.ts` - Next.js configuration

## 📚 API Documentation

Once the backend is running, visit:
- **Swagger UI**: `http://localhost:8080/api/v1/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api/v1/api-docs`

### Key API Endpoints

#### Authentication
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/logout` - User logout
- `POST /api/v1/auth/refresh` - Refresh JWT token

#### Products
- `GET /api/v1/products` - Get all products (with pagination)
- `GET /api/v1/products/{id}` - Get product by ID
- `POST /api/v1/products` - Create product (Admin only)
- `PUT /api/v1/products/{id}` - Update product (Admin only)
- `DELETE /api/v1/products/{id}` - Delete product (Admin only)

#### Cart
- `GET /api/v1/cart` - Get user's cart
- `POST /api/v1/cart/items` - Add item to cart
- `PUT /api/v1/cart/items/{itemId}` - Update cart item quantity
- `DELETE /api/v1/cart/items/{itemId}` - Remove item from cart
- `DELETE /api/v1/cart/clear` - Clear entire cart

#### Orders
- `GET /api/v1/orders` - Get user's orders
- `POST /api/v1/orders` - Create new order
- `GET /api/v1/orders/{id}` - Get order details

## 🧪 Development

### Running Tests
```bash
# Backend tests
cd ecommerce-backend
mvn test

# Frontend tests (if implemented)
cd ecommerce-frontend
npm test
```

### Building for Production
```bash
# Backend
cd ecommerce-backend
mvn clean package

# Frontend
cd ecommerce-frontend
npm run build
```

## 🐳 Docker Support (Future Enhancement)

The project can be containerized using Docker:

```bash
# Build and run with docker-compose
docker-compose up --build
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🚨 Security

This is a demo application. For production use, ensure you:
- Use strong, unique JWT secrets
- Implement proper input validation
- Use HTTPS in production
- Implement rate limiting
- Regular security audits
- Proper database security

## 📞 Support

If you have any questions or issues, please open an issue in the GitHub repository.

## 🎯 Future Enhancements

- [ ] Payment integration (Stripe/PayPal)
- [ ] Email notifications
- [ ] Product reviews and ratings
- [ ] Wishlist functionality
- [ ] Advanced search with filters
- [ ] Multi-language support
- [ ] Docker containerization
- [ ] CI/CD pipeline
- [ ] Monitoring and logging
- [ ] Performance optimization

---

**Happy coding!** 🚀