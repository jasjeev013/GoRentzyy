// stores/bookingStore.ts
import { create } from 'zustand';
import { bookingService } from '../app/api/bookings/service';


interface Booking {
  bookingId: number;
  startDate: string;
  endDate: string;
  totalPrice: number;
  status: string;
  renter: {
      userId: number;
      fullName: string;
      email: string;
      phoneNumber: string;
      address: string;
      role: string;
      emailVerified: boolean;
      phoneNumberVerified: boolean;
      profilePicture: string;
      createdAt: string;
      updatedAt: string;
    };
  car: {
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
    roadSideAssistance: string | null;
    fuelPolicy: string;
    features: string;
    importantPoints: string;
    location: string | null;
    reviews: any[]; // Replace `any` with a `Review` interface if reviews have a defined structure
    host: {
      userId: number;
      fullName: string;
      email: string;
      phoneNumber: string;
      address: string;
      role: string;
      emailVerified: boolean;
      phoneNumberVerified: boolean;
      profilePicture: string;
      createdAt: string;
      updatedAt: string;
    };
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
    status: string;
  }) => Promise<any>;
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