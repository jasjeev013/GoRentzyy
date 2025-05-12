// app/api/bookings/services.ts

import { api } from '../config';

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

export const bookingService = {
  fetchHostBookings: async (): Promise<Booking[]> => {
    const response = await api.get('/api/booking/getByHost');
    return response.data;
  },

  fetchRenterBookings: async (): Promise<Booking[]> => {
    const response = await api.get('/api/booking/getByRenter');
    return response.data;
  },

  createBooking: async (carId: number, bookingData: {
    startDate: string;
    endDate: string;
    totalPrice: number;
  }): Promise<Booking> => {
    const response = await api.post(`/api/booking/create/${carId}`, bookingData);
    return response.data;
  },
};