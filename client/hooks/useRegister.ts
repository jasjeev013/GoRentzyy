// hooks/useRegister.ts
import { authService } from '../app/api/auth/services';
import { useAuthStore } from '../stores/authStore';

export const useRegister = () => {
  const { login,setUserData } = useAuthStore();

  const register = async (userData: {
    fullName: string;
    email: string;
    phoneNumber: string;
    address: string;
    role: string;
    password: string;
  }) => {
    try {
      const registeredUser = await authService.register(userData);
      
      // Automatically log in the user after registration
      const loginResponse = await authService.login(userData.email, userData.password);
      
      if (loginResponse.status === 'OK') {
        login(loginResponse.jwtToken, loginResponse.role);

        const user = await authService.getUserData(loginResponse.jwtToken);
        setUserData(user);
        console.log('User data:', user);
        return { success: true, user: registeredUser };
      }
      
      return { success: false, error: 'Registration successful but login failed' };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Registration failed' 
      };
    }
  };

  return { register };
};