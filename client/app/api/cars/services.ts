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
    // Convert dates to ISO string format if they're not already
    const formattedStartDate = new Date(startDate).toISOString().split('Z')[0];
    const formattedEndDate = new Date(endDate).toISOString().split('Z')[0];
    // const formattedStartDate = "2025-04-04T16:09:27.136129"
    // const formattedEndDate = "2025-05-04T16:09:27.136129";

    console.log('Formatted Start Date:', formattedStartDate);
    console.log('Formatted End Date:', formattedEndDate);
    
    const response = await api.get('/api/car/getByCT', {
      params: { 
        city, 
        startDate: formattedStartDate,
        endDate: formattedEndDate 
      }
    });

    console.log('Response from fetchCarsByCityAndDate:', response.data);
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
  },

  fetchCarById: async (carId: number): Promise<Car> => {
    try {
      const response = await api.get(`/api/car/get/${carId}`);
      console.log('Response from fetchCarById:', response.data);
      return response.data;

    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch car details');
    }
  },
};