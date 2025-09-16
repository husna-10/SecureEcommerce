'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { ArrowRight, ShoppingBag, Star, Shield, Truck } from 'lucide-react';
import { useAuthStore } from '@/store/auth';
import { apiClient } from '@/lib/api';
import { formatPrice, getImageUrl } from '@/lib/utils';

interface Product {
  id: number;
  name: string;
  price: number;
  imageUrl?: string;
  category: string;
  description?: string;
}

export default function Home() {
  const [featuredProducts, setFeaturedProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const { isAuthenticated } = useAuthStore();

  useEffect(() => {
    const fetchFeaturedProducts = async () => {
      try {
        const response = await apiClient.getProducts({ size: 6 });
        setFeaturedProducts(response.data.data.content || []);
      } catch (error) {
        console.error('Failed to fetch featured products:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchFeaturedProducts();
  }, []);

  return (
    <div className="bg-white">
      {/* Hero Section */}
      <div className="relative bg-gradient-to-r from-blue-600 to-purple-700 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
          <div className="text-center">
            <h1 className="text-4xl md:text-6xl font-bold mb-6">
              Welcome to Our Store
            </h1>
            <p className="text-xl md:text-2xl mb-8 opacity-90">
              Discover amazing products at unbeatable prices
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Link
                href="/products"
                className="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md text-blue-700 bg-white hover:bg-gray-50 transition-colors"
              >
                Shop Now
                <ArrowRight className="ml-2 h-5 w-5" />
              </Link>
              {!isAuthenticated && (
                <Link
                  href="/register"
                  className="inline-flex items-center px-6 py-3 border-2 border-white text-base font-medium rounded-md text-white hover:bg-white hover:text-blue-700 transition-colors"
                >
                  Sign Up Free
                </Link>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Features Section */}
      <div className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">
              Why Choose Us?
            </h2>
            <p className="text-lg text-gray-600 max-w-3xl mx-auto">
              We provide the best shopping experience with top-quality products and excellent service
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="bg-blue-100 rounded-full p-6 w-16 h-16 mx-auto mb-4 flex items-center justify-center">
                <Shield className="h-8 w-8 text-blue-600" />
              </div>
              <h3 className="text-xl font-semibold mb-2">Secure Shopping</h3>
              <p className="text-gray-600">
                Your data and payments are protected with enterprise-level security
              </p>
            </div>
            
            <div className="text-center">
              <div className="bg-green-100 rounded-full p-6 w-16 h-16 mx-auto mb-4 flex items-center justify-center">
                <Truck className="h-8 w-8 text-green-600" />
              </div>
              <h3 className="text-xl font-semibold mb-2">Fast Delivery</h3>
              <p className="text-gray-600">
                Quick and reliable shipping to get your products when you need them
              </p>
            </div>
            
            <div className="text-center">
              <div className="bg-purple-100 rounded-full p-6 w-16 h-16 mx-auto mb-4 flex items-center justify-center">
                <Star className="h-8 w-8 text-purple-600" />
              </div>
              <h3 className="text-xl font-semibold mb-2">Quality Products</h3>
              <p className="text-gray-600">
                Carefully curated selection of high-quality products from trusted brands
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Call to Action for Demo */}
      <div className="py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <div className="bg-gradient-to-r from-blue-500 to-purple-600 rounded-2xl p-8 text-white">
            <h2 className="text-3xl font-bold mb-4">
              🚀 Modern E-Commerce Demo
            </h2>
            <p className="text-xl mb-6 opacity-90">
              Built with Next.js 14, Spring Boot, and modern best practices
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Link
                href="/products"
                className="inline-flex items-center px-6 py-3 bg-white text-blue-600 font-medium rounded-lg hover:bg-gray-100 transition-colors"
              >
                Explore Products
                <ArrowRight className="ml-2 h-5 w-5" />
              </Link>
              {!isAuthenticated && (
                <Link
                  href="/login"
                  className="inline-flex items-center px-6 py-3 border-2 border-white text-white font-medium rounded-lg hover:bg-white hover:text-blue-600 transition-colors"
                >
                  Try Demo Login
                </Link>
              )}
            </div>
            
            <div className="mt-8 pt-6 border-t border-white/20">
              <p className="text-sm opacity-80 mb-2">Demo Features:</p>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-2 text-sm">
                <span>🔐 JWT Authentication</span>
                <span>🛒 Shopping Cart</span>
                <span>📦 Order Management</span>
                <span>🔧 Admin Panel</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Tech Stack */}
      <div className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl font-bold text-gray-900 mb-8">
            Built with Modern Technologies
          </h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            <div className="p-6 bg-white rounded-lg shadow-sm">
              <div className="text-2xl mb-2">⚛️</div>
              <h3 className="font-semibold">Next.js 14</h3>
              <p className="text-sm text-gray-600">React Framework</p>
            </div>
            <div className="p-6 bg-white rounded-lg shadow-sm">
              <div className="text-2xl mb-2">🍃</div>
              <h3 className="font-semibold">Spring Boot</h3>
              <p className="text-sm text-gray-600">Backend API</p>
            </div>
            <div className="p-6 bg-white rounded-lg shadow-sm">
              <div className="text-2xl mb-2">🎨</div>
              <h3 className="font-semibold">Tailwind CSS</h3>
              <p className="text-sm text-gray-600">Styling</p>
            </div>
            <div className="p-6 bg-white rounded-lg shadow-sm">
              <div className="text-2xl mb-2">🗄️</div>
              <h3 className="font-semibold">PostgreSQL</h3>
              <p className="text-sm text-gray-600">Database</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
