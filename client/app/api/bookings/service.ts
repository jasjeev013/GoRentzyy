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
    return response.data.data.flat();
  },

  fetchRenterBookings: async (): Promise<Booking[]> => {
    const response = await api.get('/api/booking/getByRenter');
    return response.data.data.flat();
  },

  createBooking: async (carId: number, bookingData: {
    startDate: string;
    endDate: string;
    totalPrice: number;
  }): Promise<any> => {
    const response = await api.post(`/api/booking/create/${carId}`, bookingData);
    console.log(response.data);
    return response.data.object;
  },
};