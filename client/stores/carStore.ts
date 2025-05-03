import { create } from 'zustand';
import { Car } from '../types';

interface CarState {
  cars: Car[];
  availableCars: Car[];
  loading: boolean;
  error: string | null;
  fetchCars: () => Promise<void>;
  fetchAvailableCars: (area: string, startDate: string, endDate: string) => Promise<void>;
}

export const useCarStore = create<CarState>((set) => ({
  cars: [],
  availableCars: [],
  loading: false,
  error: null,
  fetchCars: async () => {
    set({ loading: true, error: null });
    try {
      const response = await getCars();
      set({ cars: response, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
  fetchAvailableCars: async (area, startDate, endDate) => {
    set({ loading: true, error: null });
    try {
      const response = await getCarsByAreaAndDate({ area, startDate, endDate });
      set({ availableCars: response, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
}));