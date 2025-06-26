// /hooks/useAuth.ts
import { authService } from '../app/api/auth/services';
import { useAuthStore } from '../stores/authStore';

export const useAuth = () => {
  const { token, role, isAuthenticated, userData, login, logout, setUserData } = useAuthStore();

  // Existing login handler (unchanged)
  const handleLogin = async (username: string, password: string) => {
    try {
      const response = await authService.login(username, password);
  
      if (response.status === 'OK') {
        login(response.jwtToken, response.role);
        
        // Fetch user data after successful login
        const user = await authService.getUserData(response.jwtToken);
        setUserData(user);
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



  const handleOAuthCallback = async (token: string, role: string) => {
    try {
      // Use your existing login function
      login(token, role);
      
      // Fetch user data using your existing service
      const user = await authService.getUserData(token);
      setUserData(user);
      
      return { success: true };
    } catch (error) {
      return {
        success: false,
        error: error instanceof Error ? error.message : 'Authentication failed'
      };
    }
  };
  return {
    token,
    role,
    isAuthenticated,
    userData,
    login: handleLogin, // Original login
    handleOAuthCallback, // New social login
    logout,
  };
};