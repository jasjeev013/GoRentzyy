// stores/bookingStore.ts
import { create } from 'zustand';
import { bookingService } from '../app/api/bookings/service';


interface Booking {
  bookingId: number;
  carId: number;
  renterId: number;
  hostId: number;
  startDate: string;
  endDate: string;
  totalPrice: number;
  bookingStatus: string;
  createdAt: string;
  updatedAt: string;
  car: {
    name: string;
    photos: string[];
  };
}

interface BookingState {
  hostBookings: Booking[];
  renterBookings: Booking[];
  loading: boolean;
  error: string | null;
  fetchHostBookings: () => Promise<void>;
  fetchRenterBookings: () => Promise<void>;
  createBooking: (carId: number, bookingData: {
    startDate: string;
    endDate: string;
    totalPrice: number;
  }) => Promise<void>;
}

export const useBookingStore = create<BookingState>((set) => ({
  hostBookings: [],
  renterBookings: [],
  loading: false,
  error: null,

  fetchHostBookings: async () => {
    set({ loading: true, error: null });
    try {
      const bookings = await bookingService.fetchHostBookings();
      set({ hostBookings: bookings, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  fetchRenterBookings: async () => {
    set({ loading: true, error: null });
    try {
      const bookings = await bookingService.fetchRenterBookings();
      set({ renterBookings: bookings, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  createBooking: async (carId, bookingData) => {
    set({ loading: true, error: null });
    try {
      const newBooking = await bookingService.createBooking(carId, bookingData);
      set((state) => ({
        renterBookings: [...state.renterBookings, newBooking],
        loading: false
      }));
      return newBooking;
    } catch (error) {
      set({ error: error.message, loading: false });
      throw error;
    }
  },
}));