// hooks/useAuth.ts
import { authService } from '../app/api/auth/services';
import { useAuthStore } from '../stores/authStore';

export const useAuth = () => {
  const { token, role, isAuthenticated, userData, login, logout, setUserData } = useAuthStore();

  const handleLogin = async (username: string, password: string) => {
    try {
      const response = await authService.login(username, password);
  
      if (response.status === 'OK') {
        login(response.jwtToken, response.role);
        
        // Fetch user data after successful login
        const user = await authService.getUserData(response.jwtToken);
        setUserData(user);
        console.log('User data:', user);
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
    userData,
    login: handleLogin,
    logout,
  };
};