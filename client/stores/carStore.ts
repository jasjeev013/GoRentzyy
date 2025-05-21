// stores/carStore.ts
import { create } from "zustand";
import { carService } from "../app/api/cars/services";

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
  photos: string[]; // Array of photo URLs (strings)
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
  roadSideAssistance: boolean; // Changed from string to boolean
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

// Add this type for creating/updating cars
interface CarDto extends Omit<Car, 
  'carId' | 'createdAt' | 'updatedAt' | 'host' | 'location' | 'reviews' | 'photos'
> {
  photos?: File[]; // For file uploads during creation/update
}


interface CarState {
  cars: Car[];
  availableCars: Car[];
  currentCar: Car | null; // Add this for single car
  hostCars: Car[]; // Add this for host's cars
  loading: boolean;
  error: string | null;
  fetchAllCars: () => Promise<void>;
  fetchCarsByCity: (city: string) => Promise<void>;
  fetchCarsByCityAndDate: (
    city: string,
    startDate: string,
    endDate: string
  ) => Promise<void>;
  fetchCarsByMakeAndModel: (make: string, model: string) => Promise<void>;
  fetchCarById: (carId: number) => Promise<void>; // Add this
  fetchAllCarsOfHost: () => Promise<void>;
  addNewCar: (
    carData: CarDto, photos: File[]
  ) => Promise<Car>;
  updateCar: (carId: number, carData: Partial<Car>) => Promise<void>;
  clearAvailableCars: () => void;
  clearCurrentCar: () => void; // Add this
}

export const useCarStore = create<CarState>((set) => ({
  cars: [],
  availableCars: [],
  currentCar: null,
  loading: false,
  error: null,
  hostCars: [],
  fetchAllCars: async () => {
    set({ loading: true, error: null, availableCars: [] });
    try {
      const cars = await carService.fetchAllCars();
      set({ cars, loading: false });

    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
  fetchCarsByCity: async (city) => {
    set({ loading: true, error: null, availableCars: [] });
    try {
      const cars = await carService.fetchCarsByCity(city);
      set({ cars, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
  fetchCarsByCityAndDate: async (city, startDate, endDate) => {
    set({ loading: true, error: null });
    try {
      const availableCars = await carService.fetchCarsByCityAndDate(
        city,
        startDate,
        endDate
      );
      set({ availableCars, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
  fetchCarsByMakeAndModel: async (make, model) => {
    set({ loading: true, error: null, availableCars: [] });
    try {
      const cars = await carService.fetchCarsByMakeAndModel(make, model);
      set({ cars, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
  fetchCarById: async (carId) => {
    set({ loading: true, error: null });
    try {
      const car = await carService.fetchCarById(carId);
      console.log("Fetched car:", car);
      set({ currentCar: car, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
  fetchAllCarsOfHost: async () => {
    set({ loading: true, error: null });
    try {
      const cars = await carService.fetchAllCarsOfHost();
      set({ hostCars: cars, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  addNewCar: async (carData, photos) => {
    set({ loading: true, error: null });
    try {
      const newCar = await carService.addNewCar(carData, photos);
      set((state) => ({
        hostCars: [...state.hostCars, newCar],
        loading: false,
      }));
      return newCar;
    } catch (error) {
      set({ error: error.message, loading: false });
      throw error;
    }
  },

  updateCar: async (carId, carData) => {
    set({ loading: true, error: null });
    try {
      const updatedCar = await carService.updateCar(carId, carData);
      set((state) => ({
        hostCars: state.hostCars.map((car) =>
          car.carId === carId ? updatedCar : car
        ),
        currentCar:
          state.currentCar?.carId === carId ? updatedCar : state.currentCar,
        loading: false,
      }));
      // return updatedCar;
    } catch (error) {
      set({ error: error.message, loading: false });
      throw error;
    }
  },
  clearCurrentCar: () => set({ currentCar: null }),
  clearAvailableCars: () => set({ availableCars: [] }),
}));
