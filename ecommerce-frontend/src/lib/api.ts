import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import Cookies from 'js-cookie';
import { toast } from 'react-hot-toast';

// API Configuration
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';
const TOKEN_KEY = 'ecommerce_token';

// Create axios instance
const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config: any) => {
    const token = Cookies.get(TOKEN_KEY);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  (error) => {
    const { response } = error;

    // Handle different error status codes
    switch (response?.status) {
      case 401:
        // Unauthorized - clear token and redirect to login
        Cookies.remove(TOKEN_KEY);
        toast.error('Session expired. Please login again.');
        if (typeof window !== 'undefined') {
          window.location.href = '/login';
        }
        break;
      case 403:
        toast.error('Access denied. You do not have permission to perform this action.');
        break;
      case 404:
        toast.error('Resource not found.');
        break;
      case 500:
        toast.error('Server error. Please try again later.');
        break;
      default:
        if (response?.data?.message) {
          toast.error(response.data.message);
        } else {
          toast.error('An unexpected error occurred.');
        }
    }

    return Promise.reject(error);
  }
);

// API methods
export const apiClient = {
  // Authentication
  login: (credentials: { username: string; password: string }) =>
    api.post('/auth/login', { 
      usernameOrEmail: credentials.username, 
      password: credentials.password 
    }),
  
  register: (userData: { 
    firstName: string; 
    lastName: string; 
    email: string; 
    username: string; 
    password: string;
  }) => api.post('/auth/register', userData),
  
  refreshToken: () => api.post('/auth/refresh'),
  
  logout: () => api.post('/auth/logout'),

  // Products
  getProducts: (params?: { 
    page?: number; 
    size?: number; 
    search?: string; 
    category?: string; 
    sortBy?: string; 
    sortDir?: string; 
  }) => api.get('/products', { params }),
  
  getProduct: (id: number) => api.get(`/products/${id}`),
  
  createProduct: (product: any) => api.post('/products', product),
  
  updateProduct: (id: number, product: any) => api.put(`/products/${id}`, product),
  
  deleteProduct: (id: number) => api.delete(`/products/${id}`),

  // Cart
  getCart: () => api.get('/cart'),
  
  addToCart: (productId: number, quantity: number) =>
    api.post('/cart/items', { productId, quantity }),
  
  updateCartItem: (itemId: number, quantity: number) =>
    api.put(`/cart/items/${itemId}`, { quantity }),
  
  removeFromCart: (itemId: number) => api.delete(`/cart/items/${itemId}`),
  
  clearCart: () => api.delete('/cart/clear'),

  // Orders
  getOrders: (params?: { page?: number; size?: number }) =>
    api.get('/orders', { params }),
  
  getOrder: (id: number) => api.get(`/orders/${id}`),
  
  createOrder: (orderData: {
    shippingAddress: {
      addressLine1: string;
      addressLine2?: string;
      city: string;
      state: string;
      postalCode: string;
      country: string;
    };
    paymentMethod?: string;
    notes?: string;
  }) => api.post('/orders', orderData),

  // User Profile
  getProfile: () => api.get('/users/profile'),
  
  updateProfile: (profile: any) => api.put('/users/profile', profile),

  // Admin endpoints
  getAllUsers: (params?: { page?: number; size?: number }) =>
    api.get('/users/admin', { params }),
  
  getAllOrders: (params?: { page?: number; size?: number }) =>
    api.get('/orders/admin', { params }),
};

// Token management utilities
export const tokenUtils = {
  setToken: (token: string) => {
    Cookies.set(TOKEN_KEY, token, { expires: 7 }); // 7 days
  },
  
  getToken: () => {
    return Cookies.get(TOKEN_KEY);
  },
  
  removeToken: () => {
    Cookies.remove(TOKEN_KEY);
  },
  
  hasToken: () => {
    return !!Cookies.get(TOKEN_KEY);
  }
};

export default api;