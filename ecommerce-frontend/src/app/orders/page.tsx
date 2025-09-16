'use client';

import { useEffect, useState } from 'react';
import { useAuthStore } from '@/store/auth';
import { useRouter } from 'next/navigation';
import { 
  Package,
  Clock,
  CheckCircle,
  Truck,
  ShoppingBag
} from 'lucide-react';

export default function OrdersPage() {
  const { user, isAuthenticated, isLoading } = useAuthStore();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      router.push('/login');
      return;
    }
  }, [isAuthenticated, isLoading, router]);

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return null;
  }

  const mockOrders = [
    {
      id: 1,
      orderNumber: 'ORD-001',
      date: '2024-01-15',
      status: 'delivered',
      total: 299.99,
      items: 3
    },
    {
      id: 2,
      orderNumber: 'ORD-002',
      date: '2024-01-20',
      status: 'shipped',
      total: 149.99,
      items: 2
    },
    {
      id: 3,
      orderNumber: 'ORD-003',
      date: '2024-01-25',
      status: 'processing',
      total: 89.99,
      items: 1
    }
  ];

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'delivered':
        return <CheckCircle className="h-5 w-5 text-green-600" />;
      case 'shipped':
        return <Truck className="h-5 w-5 text-blue-600" />;
      case 'processing':
        return <Clock className="h-5 w-5 text-orange-600" />;
      default:
        return <Package className="h-5 w-5 text-gray-600" />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'delivered':
        return 'text-green-600 bg-green-100';
      case 'shipped':
        return 'text-blue-600 bg-blue-100';
      case 'processing':
        return 'text-orange-600 bg-orange-100';
      default:
        return 'text-gray-600 bg-gray-100';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 text-black">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">My Orders</h1>
          <p className="text-gray-600 mt-2">Track and manage your orders</p>
        </div>

        {/* Orders List */}
        {mockOrders.length === 0 ? (
          <div className="bg-white rounded-lg shadow-sm border p-8 text-center">
            <ShoppingBag className="h-16 w-16 mx-auto text-gray-400 mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">No orders yet</h3>
            <p className="text-gray-600 mb-6">Start shopping to see your orders here</p>
            <button
              onClick={() => router.push('/products')}
              className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors"
            >
              Browse Products
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {mockOrders.map((order) => (
              <div key={order.id} className="bg-white rounded-lg shadow-sm border p-6 hover:shadow-md transition-shadow">
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center space-x-4">
                    <div className="flex items-center space-x-2">
                      {getStatusIcon(order.status)}
                      <span className={`px-3 py-1 rounded-full text-sm font-medium capitalize ${getStatusColor(order.status)}`}>
                        {order.status}
                      </span>
                    </div>
                    <div>
                      <h3 className="text-lg font-semibold text-gray-900">{order.orderNumber}</h3>
                      <p className="text-gray-600 text-sm">Placed on {new Date(order.date).toLocaleDateString()}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-lg font-bold text-gray-900">${order.total}</p>
                    <p className="text-gray-600 text-sm">{order.items} item{order.items > 1 ? 's' : ''}</p>
                  </div>
                </div>

                <div className="flex items-center justify-between pt-4 border-t">
                  <div className="text-sm text-gray-600">
                    Order details and tracking information
                  </div>
                  <div className="flex space-x-3">
                    <button className="text-blue-600 hover:text-blue-700 text-sm font-medium">
                      View Details
                    </button>
                    <button className="text-blue-600 hover:text-blue-700 text-sm font-medium">
                      Track Order
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Order Summary */}
        <div className="mt-8 bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Order Summary</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="text-center">
              <p className="text-2xl font-bold text-blue-600">{mockOrders.length}</p>
              <p className="text-gray-600 text-sm">Total Orders</p>
            </div>
            <div className="text-center">
              <p className="text-2xl font-bold text-green-600">
                ${mockOrders.reduce((sum, order) => sum + order.total, 0).toFixed(2)}
              </p>
              <p className="text-gray-600 text-sm">Total Spent</p>
            </div>
            <div className="text-center">
              <p className="text-2xl font-bold text-orange-600">
                {mockOrders.filter(order => order.status === 'delivered').length}
              </p>
              <p className="text-gray-600 text-sm">Completed</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}