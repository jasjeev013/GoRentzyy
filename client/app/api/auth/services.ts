import { api } from '../config';

interface LoginResponse {
  status: string;
  jwtToken: string;
  role: string;
}

export const authService = {
  login: async (username: string, password: string): Promise<LoginResponse> => {
    const response = await api.post('/api/user/login', { username, password });
    return response.data;
  },
};