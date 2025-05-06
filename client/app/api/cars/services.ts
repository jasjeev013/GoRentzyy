// app/api/cars/services.ts
import { api } from '../config';

interface Car {
  host: {
    userId: number;
    fullName: string;
    email: string;
    phoneNumber: string;
    address: string;
    role: string;
    createdAt: string;
    updatedAt: string;
  };
  carId: number;
  name: string;
  make: string;
  model: string;
  year: number;
  color: string;
  registrationNumber: string;
  photos: string[];
  carCategory: string;
  carType: string;
  fuelType: string;
  transmissionMode: string;
  seatingCapacity: number;
  luggageCapacity: number;
  rentalPricePerDay: number;
  rentalPricePerWeek: number;
  rentalPricePerMonth: number;
  availabilityStatus: string;
  maintenanceDueDate: string;
  createdAt: string;
  updatedAt: string;
  insurance: string;
  roadSideAssistance: string;
  fuelPolicy: string;
  features: string;
  importantPoints: string;
  location: {
    locationId: number;
    city: string;
    address: string;
    latitude: number;
    longitude: number;
  };
  reviews: {
    reviewId: number;
    rating: number;
    comments: string;
    createdAt: string;
  }[];
}

export const carService = {
  fetchAllCars: async (): Promise<Car[]> => {
    const response = await api.get('/api/car/getAll');
    return response.data.data.flat();
  },

  fetchCarsByCity: async (city: string): Promise<Car[]> => {
    const response = await api.get('/api/car/getByC', {
      params: { city }
    });
    return response.data.data.flat();
  },

  fetchCarsByCityAndDate: async (
    city: string, 
    startDate: string, 
    endDate: string
  ): Promise<Car[]> => {
    const response = await api.get('/api/car/getByCT', {
      params: { city, startDate, endDate }
    });
    return response.data.data.flat();
  },

  fetchCarsByMakeAndModel: async (
    make: string, 
    model: string
  ): Promise<Car[]> => {
    const response = await api.get('/api/car/getByMM', {
      params: { make, model }
    });
    return response.data.data.flat();
  }
};