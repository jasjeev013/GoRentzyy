import { api } from '../config';

interface RegisterData {
  name: string;
  email: string;
  password: string;
}

export const register = async (data: RegisterData) => {
  const response = await api.post('/auth/register', data);
  return response.data;
};