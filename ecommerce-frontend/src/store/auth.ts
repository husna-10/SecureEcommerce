import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { apiClient, tokenUtils } from '@/lib/api';

interface User {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';
}

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  
  // Actions
  login: (credentials: { username: string; password: string }) => Promise<void>;
  register: (userData: {
    firstName: string;
    lastName: string;
    email: string;
    username: string;
    password: string;
  }) => Promise<void>;
  logout: () => void;
  checkAuth: () => Promise<void>;
  updateUser: (userData: Partial<User>) => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      isAuthenticated: false,
      isLoading: false,

      login: async (credentials) => {
        set({ isLoading: true });
        try {
          const response = await apiClient.login(credentials);
          const data = response.data;
          
          // Backend returns accessToken, not token
          const token = data.accessToken;
          const user = {
            id: data.userId,
            firstName: data.fullName.split(' ')[0],
            lastName: data.fullName.split(' ')[1] || '',
            username: data.username,
            email: data.email,
            role: data.role
          };
          
          tokenUtils.setToken(token);
          set({ 
            user, 
            isAuthenticated: true, 
            isLoading: false 
          });
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      register: async (userData) => {
        set({ isLoading: true });
        try {
          const response = await apiClient.register(userData);
          const { token, user } = response.data.data;
          
          tokenUtils.setToken(token);
          set({ 
            user, 
            isAuthenticated: true, 
            isLoading: false 
          });
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      logout: () => {
        tokenUtils.removeToken();
        set({ 
          user: null, 
          isAuthenticated: false 
        });
        
        // Call logout API in background
        apiClient.logout().catch(() => {
          // Ignore logout API errors
        });
      },

      checkAuth: async () => {
        if (!tokenUtils.hasToken()) {
          set({ isAuthenticated: false, user: null });
          return;
        }

        set({ isLoading: true });
        try {
          const response = await apiClient.getProfile();
          const user = response.data.data;
          
          set({ 
            user, 
            isAuthenticated: true, 
            isLoading: false 
          });
        } catch (error) {
          // Token is invalid, clear it
          tokenUtils.removeToken();
          set({ 
            user: null, 
            isAuthenticated: false, 
            isLoading: false 
          });
        }
      },

      updateUser: (userData) => {
        const currentUser = get().user;
        if (currentUser) {
          set({ 
            user: { ...currentUser, ...userData } 
          });
        }
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ 
        user: state.user, 
        isAuthenticated: state.isAuthenticated 
      }),
    }
  )
);