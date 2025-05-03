import { api } from '../config';

export const authService = {
  login: async (email: string, password: string) => {
    const response = await api.post('/auth/login', { email, password });
    return response.data;
  },
  register: async (userData: { name: string; email: string; password: string }) => {
    const response = await api.post('/auth/register', userData);
    return response.data;
  }
};