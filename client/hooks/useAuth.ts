// /hooks/useAuth.ts
import { authService } from "../app/api/auth/services";
import { RegisterDto } from "../app/types";
import { useAuthStore } from "../stores/authStore";

export const useAuth = () => {
  const { token, role, isAuthenticated, userData, login, logout, setUserData } =
    useAuthStore();

  const handleLogin = async (username: string, password: string) => {
    try {
      const response = await authService.login(username, password);

      if (response.status === "OK") {
        login(response.jwtToken, response.role);
        const user = await authService.getUserData(response.jwtToken);
        if (user) {
          setUserData(user.object);
        }
        console.log("Login successful:", user.object);
        return { success: true, emailVerified: response.emailVerify };
      } else {
        console.error("Login failed:", response);
      }
      return { success: false, error: "Login failed" };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || "Login failed",
      };
    }
  };

  const register = async (userData: RegisterDto) => {
    try {
      const registeredUser = await authService.register(userData);

      // Automatically log in the user after registration
      const loginResponse = await authService.login(
        userData.email,
        userData.password
      );

      if (loginResponse.status === "OK") {
        login(loginResponse.jwtToken, loginResponse.role);

        const user = await authService.getUserData(loginResponse.jwtToken);
        setUserData(user.object);
        console.log("User data:", user);
        return { success: true, user: registeredUser };
      }

      return {
        success: false,
        error: "Registration successful but login failed",
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || "Registration failed",
      };
    }
  };

  const handleOAuthCallback = async (token: string, role: string) => {
    try {
      // Use your existing login function
      login(token, role);

      // Fetch user data using your existing service
      const user = await authService.getUserData(token);
      setUserData(user.object);

      return { success: true };
    } catch (error) {
      return {
        success: false,
        error: error instanceof Error ? error.message : "Authentication failed",
      };
    }
  };
  const forgotPassword = async (email: string) => {
    try {
      const response = await authService.forgotPassword(email);
      return { success: true, error: "" };
    } catch (error) {
      return { success: false, error: "Failed to send reset email" };
    }
  };

  const resetPassword = async (token: string, newPassword: string) => {
    try {
      const response = await authService.resetPassword(token, newPassword);
      return { success: true, error: "" };
    } catch (error) {
      return { success: false, error: "Failed to reset password" };
    }
  };
  return {
    token,
    role,
    isAuthenticated,
    userData,
    login: handleLogin,
    register,
    handleOAuthCallback, 
    logout,
    forgotPassword,
    resetPassword,
  };
};
