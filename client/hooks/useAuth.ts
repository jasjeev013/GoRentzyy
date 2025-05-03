import { authService } from '../app/api/auth/services';
import { useAuthStore } from '../stores/authStore';


export const useAuth = () => {
  const { token, role, isAuthenticated, login, logout } = useAuthStore();

  const handleLogin = async (username: string, password: string) => {
    try {
      const response = await authService.login(username, password);
  
      console.log('Login response:', response); // Log the response for debugging
      if (response.status === 'OK') {
        login(response.jwtToken, response.role);
        return { success: true };
      }
      return { success: false, error: 'Login failed' };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Login failed' 
      };
    }
  };

  return {
    token,
    role,
    isAuthenticated,
    login: handleLogin,
    logout,
  };
};