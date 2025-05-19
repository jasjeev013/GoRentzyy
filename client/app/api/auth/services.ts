import { api } from '../config';

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
    const response = await api.post('/api/user/login', { username, password });
    return response.data;
  },

  getUserData: async (token: string): Promise<UserData> => {
    const response = await api.get('/api/user/get', {
      headers: {
        Authorization: token
      }
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
    const response = await api.post('/api/user/create', userData);
    return response.data.object;
  }
};