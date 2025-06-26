import React from 'react';
import { MapPin } from 'lucide-react';

interface LocationMapProps {
  latitude: number;
  longitude: number;
  address: string;
}

const LocationMap = ({ latitude, longitude, address }: LocationMapProps) => {
  const googleMapsUrl = `https://www.google.com/maps?q=${latitude},${longitude}`;

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
      <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">Location</h2>
      
      <div className="aspect-w-16 aspect-h-9 mb-4 rounded-lg overflow-hidden">
        <iframe
          width="100%"
          height="300"
          frameBorder="0"
          style={{ border: 0 }}
          src={`https://www.google.com/maps/embed/v1/view?key=${process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY}&center=${latitude},${longitude}&zoom=15&maptype=roadmap`}
          allowFullScreen
        ></iframe>
      </div>
      
      <div className="flex items-start gap-2">
        <MapPin className="h-5 w-5 text-gray-500 dark:text-gray-400 mt-0.5 flex-shrink-0" />
        <div>
          <p className="text-gray-900 dark:text-white">{address}</p>
          <a
            href={googleMapsUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="text-blue-600 dark:text-blue-400 hover:underline text-sm"
          >
            Open in Google Maps
          </a>
        </div>
      </div>
    </div>
  );
};

export default LocationMap;