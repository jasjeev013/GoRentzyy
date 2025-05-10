"use client";
import React from 'react';
import { Bell, Check, ArrowRight, Plus, X } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '../../../components/ui/card';
import { ScrollArea } from '../../../components/ui/scroll-area';
import { Badge } from '../../../components/ui/badge';
import { Button } from '../../../components/ui/button';
import { Separator } from '../../../components/ui/separator';

const RenterNotificationTimeline = ({ onClose }: { onClose: () => void }) => {
  // Sample notification data
  const notifications = [
    {
      id: 'BK-2023-78945',
      action: 'Created',
      user: { name: 'Dheeraj', email: 'admin@taxisoft.in' },
      timestamp: 'Mar 21 2023 10:59 AM',
      status: 'Created'
    },
    {
      id: 'BK-2023-78945',
      action: 'Created',
      user: { name: 'Dheeraj', email: 'admin@taxisoft.in' },
      timestamp: 'Mar 21 2023 10:59 AM',
      status: 'Created'
    },
    {
      id: 'BK-2023-78945',
      action: 'Created',
      user: { name: 'Dheeraj', email: 'admin@taxisoft.in' },
      timestamp: 'Mar 21 2023 10:59 AM',
      status: 'Created'
    },
    {
      id: 'BK-2023-78945',
      action: 'Created',
      user: { name: 'Dheeraj', email: 'admin@taxisoft.in' },
      timestamp: 'Mar 21 2023 10:59 AM',
      status: 'Created'
    },
    {
      id: 'BK-2023-78945',
      action: 'Created',
      user: { name: 'Dheeraj', email: 'admin@taxisoft.in' },
      timestamp: 'Mar 21 2023 10:59 AM',
      status: 'Created'
    },
    {
      id: 'BK-2023-78945',
      action: 'Created',
      user: { name: 'Dheeraj', email: 'admin@taxisoft.in' },
      timestamp: 'Mar 21 2023 10:59 AM',
      status: 'Created'
    },
    {
      id: 'BK-2023-78945',
      action: 'Created',
      user: { name: 'Dheeraj', email: 'admin@taxisoft.in' },
      timestamp: 'Mar 21 2023 10:59 AM',
      status: 'Created'
    },
    {
      id: 'BK-2023-78945',
      action: 'Created',
      user: { name: 'Dheeraj', email: 'admin@taxisoft.in' },
      timestamp: 'Mar 21 2023 10:59 AM',
      status: 'Created'
    },
    {
      id: 'BK-2023-78945',
      action: 'Created',
      user: { name: 'Dheeraj', email: 'admin@taxisoft.in' },
      timestamp: 'Mar 21 2023 10:59 AM',
      status: 'Created'
    },
    {
      id: 'BK-2023-78945',
      action: 'Created',
      user: { name: 'Dheeraj', email: 'admin@taxisoft.in' },
      timestamp: 'Mar 21 2023 10:59 AM',
      status: 'Created'
    },
    {
      id: 'BK-2023-78944',
      action: 'Dispatched',
      user: { name: 'Priya', email: 'priya@taxisoft.in' },
      timestamp: 'Mar 20 2023 02:30 PM',
      status: 'Dispatched'
    },
    {
      id: 'BK-2023-78943',
      action: 'Closed',
      user: { name: 'Rahul', email: 'rahul@taxisoft.in' },
      timestamp: 'Mar 19 2023 05:45 PM',
      status: 'Closed'
    },
    {
      id: 'BK-2023-78942',
      action: 'Created',
      user: { name: 'Neha', email: 'neha@taxisoft.in' },
      timestamp: 'Mar 18 2023 09:15 AM',
      status: 'Created'
    },
    {
      id: 'BK-2023-78941',
      action: 'Dispatched',
      user: { name: 'Arjun', email: 'arjun@taxisoft.in' },
      timestamp: 'Mar 17 2023 11:20 AM',
      status: 'Dispatched'
    },
    {
      id: 'BK-2023-78940',
      action: 'Closed',
      user: { name: 'Sonia', email: 'sonia@taxisoft.in' },
      timestamp: 'Mar 16 2023 03:10 PM',
      status: 'Closed'
    }
  ];

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
              
              {notifications.map((notification, index) => (
                <div key={index} className="relative pb-4 last:pb-0 group">
                  {/* Timeline dot */}
                  <div className={`absolute left-4 top-1 h-5 w-5 rounded-full ${getStatusColor(notification.status)} flex items-center justify-center`}>
                    {getStatusIcon(notification.status)}
                  </div>
                  
                  {/* Notification card */}
                  <div className="ml-8 p-3 rounded-lg bg-gray-50 dark:bg-gray-700 group-hover:bg-gray-100 dark:group-hover:bg-gray-600 transition-colors">
                    <div className="flex justify-between items-start">
                      <div>
                        <div className="flex items-center gap-2">
                          <Badge variant="outline" className="text-xs font-medium">
                            {notification.id}
                          </Badge>
                          <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                            Booking {notification.action}
                          </span>
                        </div>
                        <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                          {notification.user.name} ({notification.user.email})
                        </p>
                      </div>
                      <span className="text-xs text-gray-500 dark:text-gray-400 whitespace-nowrap">
                        {notification.timestamp}
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