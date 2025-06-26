import React from 'react';
import { Calendar, Clock, User } from 'lucide-react';

interface HostInfoProps {
  host: {
    userId: number;
    fullName: string;
    profilePicture?: string;
    email: string;
    phoneNumber: string;
    createdAt: string;
  };
}

const HostInfo = ({ host }: HostInfoProps) => {
  const joinDate = new Date(host.createdAt).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long'
  });

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mt-6 border border-gray-200 dark:border-gray-700">
      <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">About the Host</h2>
      
      <div className="flex items-start gap-4">
        <div className="flex-shrink-0">
          <img 
            src={host?.profilePicture} 
            alt={host.fullName}
            className="h-16 w-16 rounded-full object-cover"
          />
        </div>
        
        <div>
          <h3 className="font-medium text-gray-900 dark:text-white">{host.fullName}</h3>
          <div className="flex items-center text-sm text-gray-600 dark:text-gray-400 mt-1">
            <User className="h-4 w-4 mr-1" />
            <span>Host since {joinDate}</span>
          </div>
          
          <div className="mt-4 space-y-2">
            <div className="flex items-center text-sm">
              <span className="text-gray-600 dark:text-gray-400 w-24">Email:</span>
              <span className="text-gray-900 dark:text-white">{host.email}</span>
            </div>
            <div className="flex items-center text-sm">
              <span className="text-gray-600 dark:text-gray-400 w-24">Phone:</span>
              <span className="text-gray-900 dark:text-white">{host.phoneNumber}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HostInfo;