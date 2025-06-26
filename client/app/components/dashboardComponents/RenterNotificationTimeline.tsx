"use client";
import React, { useEffect } from 'react';
import { Bell, Check, ArrowRight, Plus, X } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '../../../components/ui/card';
import { ScrollArea } from '../../../components/ui/scroll-area';
import { Badge } from '../../../components/ui/badge';
import { Button } from '../../../components/ui/button';
import { Separator } from '../../../components/ui/separator';
import { useNotificationStore } from '../../../stores/notificationStore';

const RenterNotificationTimeline = ({ onClose }: { onClose: () => void }) => {
  const {
    notifications,
    loading,
    error,
    fetchNotifications,
  } = useNotificationStore();
  // Sample notification data
  useEffect(() => {
    fetchNotifications();
    console.log('Notifications fetched:', notifications.length);
  }, [fetchNotifications]);

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'Created':
        return <Check className="h-4 w-4 text-white" />;
      case 'Dispatched':
        return <ArrowRight className="h-4 w-4 text-white" />;
      case 'Closed':
        return <Plus className="h-4 w-4 text-white" />;
      default:
        return <Check className="h-4 w-4 text-white" />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Created':
        return 'bg-green-500';
      case 'Dispatched':
        return 'bg-red-500';
      case 'Closed':
        return 'bg-yellow-500';
      default:
        return 'bg-green-500';
    }
  };
  if (loading) {
    return (
      <div className="fixed inset-0 backdrop-blur-sm z-50 flex justify-end p-4">
        <Card className="w-full max-w-md bg-white dark:bg-gray-800 rounded-lg shadow-lg">
          <CardContent className="flex justify-center items-center h-64">
            <p>Loading notifications...</p>
          </CardContent>
        </Card>
      </div>
    );
  }

  if (error) {
    return (
      <div className="fixed inset-0 backdrop-blur-sm z-50 flex justify-end p-4">
        <Card className="w-full max-w-md bg-white dark:bg-gray-800 rounded-lg shadow-lg">
          <CardContent className="flex justify-center items-center h-64 text-red-500">
            <p>{error}</p>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 backdrop-blur-sm z-50 flex justify-end p-4">
      <Card className="w-full max-w-md bg-white dark:bg-gray-800 rounded-lg shadow-lg">
        <CardHeader className="pb-2">
          <div className="flex justify-between items-center">
            <CardTitle className="text-lg">Notifications</CardTitle>
            <Button
              variant="ghost"
              size="icon"
              onClick={onClose}
              className="text-gray-500 hover:text-gray-700 dark:hover:text-gray-300"
            >
              <X className="h-5 w-5" />
            </Button>
          </div>
        </CardHeader>
        <Separator className="mb-2" />
        <CardContent className="p-0">
          <ScrollArea className="h-[550px]">
            <div className="relative pl-6 pr-4 py-2">
              {/* Timeline line */}
              <div className="absolute left-7 top-0 h-full w-0.5 bg-gray-200 dark:bg-gray-700"></div>

              {notifications.length>0 &&  notifications.map((notification, index) => (
                <div key={index} className="relative pb-4 last:pb-0 group">
                  {/* Timeline dot */}
                  <div className={`absolute left-4 top-1 h-5 w-5 rounded-full ${getStatusColor(notification.type)} flex items-center justify-center`}>
                    {getStatusIcon(notification.type)}
                  </div>

                  {/* Notification card */}
                  <div className="ml-8 p-3 rounded-lg bg-gray-50 dark:bg-gray-700 group-hover:bg-gray-100 dark:group-hover:bg-gray-600 transition-colors">
                    <div className="flex justify-between items-start">
                      <div>
                        <div className="flex items-center gap-2">
                          <Badge variant="outline" className="text-xs font-medium">
                            {notification.type}
                          </Badge>
                          <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                             {notification.title}
                          </span>
                        </div>
                        <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                          {notification.message}
                        </p>
                      </div>
                      <span className="text-xs text-gray-500 dark:text-gray-400 whitespace-nowrap">
                        {notification.sentAt}
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </ScrollArea>
        </CardContent>
      </Card>
    </div>
  );
};

export default RenterNotificationTimeline;