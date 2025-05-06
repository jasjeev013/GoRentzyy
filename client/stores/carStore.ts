// stores/carStore.ts
import { create } from 'zustand';
import { carService } from '../app/api/cars/services';


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

interface CarState {
  cars: Car[];
  availableCars: Car[];
  loading: boolean;
  error: string | null;
  fetchAllCars: () => Promise<void>;
  fetchCarsByCity: (city: string) => Promise<void>;
  fetchCarsByCityAndDate: (city: string, startDate: string, endDate: string) => Promise<void>;
  fetchCarsByMakeAndModel: (make: string, model: string) => Promise<void>;
}

export const useCarStore = create<CarState>((set) => ({
  cars: [],
  availableCars: [],
  loading: false,
  error: null,
  fetchAllCars: async () => {
    set({ loading: true, error: null });
    try {
      const cars = await carService.fetchAllCars();
      set({ cars, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
  fetchCarsByCity: async (city) => {
    set({ loading: true, error: null });
    try {
      console.log("Fetching cars by city:", city);
      const cars = await carService.fetchCarsByCity(city);
      console.log("Hello cars aa gyee ",cars);
      set({ cars, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
  fetchCarsByCityAndDate: async (city, startDate, endDate) => {
    set({ loading: true, error: null });
    try {
      const availableCars = await carService.fetchCarsByCityAndDate(city, startDate, endDate);
      set({ availableCars, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
  fetchCarsByMakeAndModel: async (make, model) => {
    set({ loading: true, error: null });
    try {
      const cars = await carService.fetchCarsByMakeAndModel(make, model);
      set({ cars, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  }
}));