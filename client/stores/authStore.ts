import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  token: string | null;
  role: string | null;
  isAuthenticated: boolean;
  login: (token: string, role: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      role: null,
      isAuthenticated: false,
      login: (token, role) => {
        set({ token, role, isAuthenticated: true });
        localStorage.setItem('token', token);
      },
      logout: () => {
        set({ token: null, role: null, isAuthenticated: false });
        localStorage.removeItem('token');
      },
    }),
    {
      name: 'auth-storage',
    }
  )
);