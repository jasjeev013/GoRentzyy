// stores/authStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { authService } from '../app/api/auth/services';

interface UserData {
  userId: number;
  fullName: string;
  email: string;
  phoneNumber: string;
  address: string;
  role: string;
  createdAt: string;
  updatedAt: string;
  emailVerified: boolean;
  phoneNumberVerified: boolean;
  profilePicture: string;
}

interface AuthState {
  token: string | null;
  role: string | null;
  isAuthenticated: boolean;
  userData: UserData | null;
  login: (token: string, role: string) => void;
  logout: () => void;
  setUserData: (userData: UserData) => void;
  updateUserData: (userData: Partial<UserData>, image: File | null) => Promise<void>;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      role: null,
      isAuthenticated: false,
      userData: null,
      login: (token, role) => {
        set({ token, role, isAuthenticated: true });
        localStorage.setItem('token', token);
      },
      logout: () => {
        set({ token: null, role: null, isAuthenticated: false, userData: null });
        localStorage.removeItem('token');
      },
      setUserData: (userData) => set({ userData }),
      updateUserData: async (userData, image) => {
        const { token } = get();
        if (!token) throw new Error('Not authenticated');
        
        try {
          const updatedUser = await authService.updateUserData(userData, image, token);
          set({ userData: updatedUser });
        } catch (error) {
          console.error('Failed to update user data:', error);
          throw error;
        }
      }
    }),
    {
      name: 'auth-storage',
    }
  )
);