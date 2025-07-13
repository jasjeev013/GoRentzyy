// api/config.ts
import axios from "axios";
import { useAuthStore } from '../../stores/authStore';
export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

// Add request interceptor
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  console.log("Token from localStorage:", token);
  if (token) {
    const payload = JSON.parse(atob(token.split(".")[1]));
    const expirationTime = payload.exp * 1000; // Convert to milliseconds
    const expDate = new Date(expirationTime);
    const presentDate = new Date();

    if (expDate <= presentDate) {
      // Token is already expired
      console.log("Token expired, logging out");
      useAuthStore.getState().logout(); // Call the logout function from the store
      window.location.href = "/login";
    } else {
      config.headers.Authorization = `${token}`;
    }
  }
  return config;
});

// Add response interceptor
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized access
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);
