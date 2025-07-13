// app/api/notification/services.ts
import { api } from '../config';

interface Review {
  notificationId: number;
  rating: Number;
  comments: string;
  createdAt: string;
  // Add any additional fields you need
}
interface ReviewDto {

  rating: Number;
  comments: string;

  // Add any additional fields you need
}

export const reviewService = {
  addReview: async (id:Number,reviewDto:ReviewDto): Promise<Review> => {
    const response = await api.post(`/api/review/create/${id}`,reviewDto);
    console.log('Fetched Reviews:', response.data);
    return response.data;
  },
  
  
  // Add other notification-related API calls as needed
};