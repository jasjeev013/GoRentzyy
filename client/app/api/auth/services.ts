// /auth/service.ts
import axios from "axios";
import { api } from "../config";

interface LoginResponse {
  status: string;
  jwtToken: string;
  role: string;
}

interface UserData {
  userId: number;
  fullName: string;
  email: string;
  phoneNumber: string;
  address: string;
  role: string;
  createdAt: string;
  updatedAt: string;
  emailVerified: boolean;
  phoneNumberVerified: boolean;
  profilePicture: string;
}

export const authService = {
  login: async (username: string, password: string): Promise<LoginResponse> => {
    const response = await api.post("/api/user/login", { username, password });

    return response.data;
  },

  getUserData: async (token: string): Promise<UserData> => {
    const response = await api.get("/api/user/get", {
      headers: {
        Authorization: token,
      },
    });

    return response.data.object;
  },

  register: async (userData: {
    fullName: string;
    email: string;
    phoneNumber: string;
    address: string;
    role: string;
    password: string;
  }): Promise<UserData> => {
    const response = await api.post("/api/user/create", userData);
    return response.data.object;
  },

  updateUserData: async (
    userData: Partial<UserData>,
    image: File | null,
    token: string
  ): Promise<UserData> => {
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

    return response.data.object;
  },
  exchangeCodeForToken: async (code: string) => {
    console.log("Google OAuth callback received 5");

    const axiosInstance = axios.create({
      baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080",
      headers: {
        "Content-Type": "application/json",
      },
    });

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
  requestEmailOTP: async (token: string): Promise<{ status: string }> => {
    const response = await api.get("/api/user/getOTPEmail", {
      headers: {
        Authorization: token,
      },
    });
    return response.data;
  },

  verifyEmailOTP: async (token: string, otp: string): Promise<{ status: string }> => {
    const response = await api.post(
      "/api/user/verifyOTPEmail",
      { token: otp },
      {
        headers: {
          Authorization: token,
        },
      }
    );
    return response.data;
  },
};
