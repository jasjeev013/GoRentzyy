import { useAuthStore } from '../stores/authStore';
import { login as apiLogin, register as apiRegister } from '../app/api/auth';

export const useAuth = () => {
  const { token, user, isAuthenticated, login, logout } = useAuthStore();

  const handleLogin = async (email: string, password: string) => {
    try {
      const response = await apiLogin({ email, password });
      login(response.token, response.user);
      return { success: true };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Login failed' };
    }
  };

  const handleRegister = async (name: string, email: string, password: string) => {
    try {
      await apiRegister({ name, email, password });
      return { success: true };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Registration failed' };
    }
  };

  return {
    token,
    user,
    isAuthenticated,
    login: handleLogin,
    register: handleRegister,
    logout,
  };
};