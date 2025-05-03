import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  token: string | null;
  user: {
    id: string;
    email: string;
    name: string;
  } | null;
  isAuthenticated: boolean;
  login: (token: string, user: any) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      isAuthenticated: false,
      login: (token, user) => {
        set({ token, user, isAuthenticated: true });
        localStorage.setItem('token', token);
      },
      logout: () => {
        set({ token: null, user: null, isAuthenticated: false });
        localStorage.removeItem('token');
      },
    }),
    {
      name: 'auth-storage', // name for localStorage
    }
  )
);