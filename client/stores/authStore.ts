// stores/authStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface UserData {
  userId: number;
  fullName: string;
  email: string;
  phoneNumber: string;
  address: string;
  role: string;
  createdAt: string;
  updatedAt: string;
}

interface AuthState {
  token: string | null;
  role: string | null;
  isAuthenticated: boolean;
  userData: UserData | null;
  login: (token: string, role: string) => void;
  logout: () => void;
  setUserData: (userData: UserData) => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
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
      setUserData: (userData) => set({ userData })
    }),
    {
      name: 'auth-storage',
    }
  )
);