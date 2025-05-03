import { api } from '../config';

interface GetCarsParams {
  area: string;
  startDate: string;
  endDate: string;
}

export const getCarsByAreaAndDate = async (params: GetCarsParams) => {
  const response = await api.get('/cars/availability', { params });
  return response.data;
};