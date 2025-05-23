// app/api/bookings/services.ts

import { api } from '../config';

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