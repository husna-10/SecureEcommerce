# 🛍️ E-Commerce Frontend - Next.js 14

A modern, responsive e-commerce frontend built with **Next.js 14**, **TypeScript**, and **Tailwind CSS**. This frontend seamlessly integrates with the Spring Boot backend to provide a complete e-commerce solution.

## 🚀 **Features**

### 🔐 **Authentication**
- User registration and login
- JWT token-based authentication
- Protected routes and authentication guards
- Persistent login sessions

### 🛒 **Shopping Experience**
- Product catalog with search and filtering
- Category-based browsing
- Shopping cart management
- Real-time cart updates
- Order placement and tracking

### 🎨 **Modern UI/UX**
- Responsive design for all devices
- Clean and intuitive interface
- Loading states and animations
- Toast notifications for user feedback
- Accessible design principles

### ⚡ **Performance & Technology**
- **Next.js 14** with App Router
- **TypeScript** for type safety
- **Tailwind CSS** for styling
- **Zustand** for state management
- **React Hook Form** with **Zod** validation
- **Axios** for API communication

## 🛠️ **Tech Stack**

| Category | Technology |
|----------|------------|
| **Framework** | Next.js 14 (React 18) |
| **Language** | TypeScript |
| **Styling** | Tailwind CSS |
| **State Management** | Zustand |
| **Forms** | React Hook Form + Zod |
| **HTTP Client** | Axios |
| **Icons** | Lucide React |
| **Notifications** | React Hot Toast |
| **UI Components** | Headless UI |

## 📦 **Installation**

### Prerequisites
- Node.js 18+ 
- npm or yarn
- Spring Boot backend running on http://localhost:8080

### Quick Start

1. **Clone & Install**
   ```bash
   cd ecommerce-frontend
   npm install
   ```

2. **Environment Setup**
   ```bash
   # .env.local is already configured with:
   NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
   NEXT_PUBLIC_APP_NAME=E-Commerce Store
   ```

3. **Start Development Server**
   ```bash
   npm run dev
   ```

4. **Open Browser**
   ```
   http://localhost:3000
   ```

## 🎯 **Key Pages & Features**

### 🏠 **Home Page** (`/`)
- Hero section with call-to-action
- Featured products showcase  
- Technology stack highlights
- Responsive design

### 🔐 **Authentication**
- **Login Page** (`/login`) - User authentication with demo credentials
- **Register Page** (`/register`) - New user registration with form validation

### 🛍️ **Products** (`/products`)
- Product grid with search and filters
- Category-based filtering
- Sorting options (name, price)
- Add to cart functionality
- Stock quantity display

### 🛒 **Shopping Cart** (`/cart`)
- Cart items management
- Quantity updates
- Order summary
- Checkout process

### 📦 **Orders** (`/orders`)
- Order history
- Order tracking
- Order details view

### ⚙️ **Profile** (`/profile`)
- User profile management
- Account settings
- Order history access

### 🔧 **Admin Panel** (`/admin`)
- Product management (CRUD operations)
- User management
- Order management
- Dashboard analytics

## 🎨 **Demo Credentials**

For testing the application, use these demo accounts:

| Role | Username | Password |
|------|----------|----------|
| **Admin** | `admin` | `admin123` |
| **User** | `user` | `user123` |

## 🚀 **Deployment**

### **Vercel Deployment** (Recommended)
```bash
# Deploy to Vercel
npm i -g vercel
vercel --prod
```

### **Local Development**
```bash
npm run dev          # Start development server
npm run build        # Build for production
npm run start        # Start production server
npm run lint         # Run ESLint
```

## 🔗 **API Integration**

The frontend communicates with the Spring Boot backend at `http://localhost:8080/api/v1`

### **Key Endpoints Used:**
- `POST /auth/login` - User authentication
- `POST /auth/register` - User registration
- `GET /products` - Product listing
- `GET /cart` - Shopping cart
- `POST /cart/items` - Add to cart
- `GET /orders` - Order history

**Built with ❤️ using Next.js 14 and modern web technologies**
