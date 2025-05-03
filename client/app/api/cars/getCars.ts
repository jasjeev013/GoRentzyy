import { api } from '../config';

interface Car {
  id: string;
  name: string;
  // ... other car properties
}

export const getCars = async (): Promise<Car[]> => {
  const response = await api.get<Car[]>('/cars');
  return response.data;
};