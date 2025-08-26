// /auth/service.ts
import axios from "axios";
import { api } from "../config";
import { ApiResponseObject, LoginResponse, RegisterDto, UserDto } from "../../types";




const axiosInstance = axios.create({
      baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080",
      headers: {
        "Content-Type": "application/json",
      },
    });

export const authService = {
  login: async (username: string, password: string): Promise<LoginResponse> => {
    const response = await api.post("/api/user/login", { username, password });

    return response.data;
  },

  getUserData: async (token: string): Promise<ApiResponseObject<UserDto>> => {
    const response = await api.get("/api/user/get", {
      headers: {
        Authorization: token,
      },
    });

    return response.data;
  },
  deleteUser: async (): Promise<ApiResponseObject<null>> => {
    const response = await api.delete("/api/user/delete");
    return response.data;
  },

  register: async (userData: RegisterDto): Promise<ApiResponseObject<UserDto>> => {
    const response = await api.post("/api/user/create", userData);
    return response.data;
  },

  updateUserData: async (
    userData: Partial<UserDto>,
    image: File | null,
    token: string
  ): Promise<ApiResponseObject<UserDto>> => {
    const formData = new FormData();

    // Append user data as JSON
    formData.append(
      "userDto",
      new Blob([JSON.stringify(userData)], {
        type: "application/json",
      })
    );

    // Append image if exists
    if (image) {
      formData.append("image", image);
    }

    const response = await api.put("/api/user/update", formData, {
      headers: {
        Authorization: token,
        "Content-Type": "multipart/form-data",
      },
    });

    return response.data;
  },
  exchangeCodeForToken: async (code: string) => {
    console.log("Google OAuth callback received 5");

    

    try {
      const response = await axiosInstance.post("/api/google/callback", {
        code,
      });
      return response.data; // { jwtToken, role, status }
    } catch (error) {
      console.error("Error during Google OAuth callback:", error);
      throw error;
    }
  },
  requestEmailOTP: async (token: string): Promise<ApiResponseObject<null>> => {
    const response = await api.get("/api/user/getOTPEmail", {
      headers: {
        Authorization: token,
      },
    });
    return response.data.result;
  },

  verifyEmailOTP: async (token: string, otp: string): Promise<ApiResponseObject<null>> => {
    const response = await api.post(
      "/api/user/verifyOTPEmail",
      { token: otp },
      {
        headers: {
          Authorization: token,
        },
      }
    );
    console.log('OTP verification response:', response.data);
    return response.data.result;
  },

   forgotPassword: async (email: string): Promise<{ result: boolean }> => {
    const response = await axiosInstance.post("/api/user/forgot-password", { email });
    return response.data;
  },

  validateResetToken: async (token: string): Promise<{ result: boolean }> => {
    const response = await axiosInstance.get(`/api/user/validate-reset-token?token=${token}`);
    return response.data;
  },

  resetPassword: async (token: string, newPassword: string): Promise<{ result: boolean  }> => {
    const response = await axiosInstance.post("/api/user/reset-password", { 
      token, 
      newPassword 
    });
    return response.data;
  }
};
