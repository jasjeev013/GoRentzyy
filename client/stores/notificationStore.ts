// stores/notificationStore.ts
import { create } from 'zustand';
import { notificationService } from '../app/api/notification/services';


interface Notification {
  notificationId: number;
  title: string;
  message: string;
  type: string;
  sentAt: string;
}

interface NotificationState {
  notifications: Notification[];
  loading: boolean;
  error: string | null;

  fetchNotifications: () => Promise<void>;

}

export const useNotificationStore = create<NotificationState>((set) => ({
  notifications: [],
  loading: false,
  error: null,

  fetchNotifications: async () => {
    set({ loading: true, error: null });
    try {
      const notifications = await notificationService.getAllNotifications();

      set({ 
        notifications,
        loading: false 
      });
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Failed to fetch notifications',
        loading: false 
      });
    }
  },

  
}));