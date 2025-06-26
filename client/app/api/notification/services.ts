// app/api/notification/services.ts
import { api } from '../config';

interface Notification {
  notificationId: number;
  title: string;
  message: string;
  type: string;
  sentAt: string;
  // Add any additional fields you need
}

export const notificationService = {
  getAllNotifications: async (): Promise<Notification[]> => {
    const response = await api.get('/api/notification/getAll');
    console.log('Fetched notifications:', response.data);
    return response.data.data[0];
  },
  
  
  // Add other notification-related API calls as needed
};