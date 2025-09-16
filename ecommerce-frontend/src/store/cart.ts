import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { apiClient } from '@/lib/api';
import { toast } from 'react-hot-toast';

interface CartItem {
  id: number;
  productId: number;
  productName: string;
  productSku?: string;
  unitPrice: number;
  quantity: number;
  subTotal: number;
}

interface Cart {
  id: number;
  items: CartItem[];
  totalItems: number;
  totalAmount: number;
}

interface CartState {
  cart: Cart | null;
  isLoading: boolean;
  
  // Actions
  fetchCart: () => Promise<void>;
  addToCart: (productId: number, quantity?: number) => Promise<void>;
  updateCartItem: (itemId: number, quantity: number) => Promise<void>;
  removeFromCart: (itemId: number) => Promise<void>;
  clearCart: () => Promise<void>;
  getItemQuantity: (productId: number) => number;
}

export const useCartStore = create<CartState>()(
  persist(
    (set, get) => ({
      cart: null,
      isLoading: false,

      fetchCart: async () => {
        set({ isLoading: true });
        try {
          const response = await apiClient.getCart();
          const cart = response.data.data;
          set({ cart, isLoading: false });
        } catch (error) {
          set({ isLoading: false });
          // Don't show error toast for cart fetch failures
          console.error('Failed to fetch cart:', error);
        }
      },

      addToCart: async (productId, quantity = 1) => {
        set({ isLoading: true });
        try {
          await apiClient.addToCart(productId, quantity);
          await get().fetchCart(); // Refresh cart
          toast.success('Item added to cart');
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      updateCartItem: async (itemId, quantity) => {
        if (quantity <= 0) {
          await get().removeFromCart(itemId);
          return;
        }
        
        set({ isLoading: true });
        try {
          await apiClient.updateCartItem(itemId, quantity);
          await get().fetchCart(); // Refresh cart
          toast.success('Cart updated');
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      removeFromCart: async (itemId) => {
        set({ isLoading: true });
        try {
          await apiClient.removeFromCart(itemId);
          await get().fetchCart(); // Refresh cart
          toast.success('Item removed from cart');
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      clearCart: async () => {
        set({ isLoading: true });
        try {
          await apiClient.clearCart();
          set({ cart: null, isLoading: false });
          toast.success('Cart cleared');
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      getItemQuantity: (productId) => {
        const cart = get().cart;
        if (!cart) return 0;
        
        const item = cart.items.find(item => item.productId === productId);
        return item?.quantity || 0;
      },
    }),
    {
      name: 'cart-storage',
      partialize: (state) => ({ 
        cart: state.cart 
      }),
    }
  )
);