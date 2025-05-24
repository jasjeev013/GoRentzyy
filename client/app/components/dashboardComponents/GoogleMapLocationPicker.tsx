// components/GoogleMapLocationPicker.tsx
'use client';
import React, { useState, useCallback } from 'react';
import { GoogleMap, useJsApiLoader, Marker, InfoWindow } from '@react-google-maps/api';

const containerStyle = {
  width: '100%',
  height: '400px'
};

interface GoogleMapLocationPickerProps {
  onLocationSelect: (lat: number, lng: number) => void;
  initialLocation?: { lat: number; lng: number };
}

const GoogleMapLocationPicker: React.FC<GoogleMapLocationPickerProps> = ({ 
  onLocationSelect, 
  initialLocation 
}) => {
  const [map, setMap] = useState<google.maps.Map | null>(null);
  const [selectedLocation, setSelectedLocation] = useState<{ lat: number; lng: number } | null>(
    initialLocation || null
  );

  const { isLoaded } = useJsApiLoader({
    id: 'google-map-script',
    googleMapsApiKey: process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY || ''
  });

  const onMapClick = useCallback((e: google.maps.MapMouseEvent) => {
    if (e.latLng) {
      const lat = e.latLng.lat();
      const lng = e.latLng.lng();
      setSelectedLocation({ lat, lng });
      onLocationSelect(lat, lng);
    }
  }, [onLocationSelect]);

  const onLoad = useCallback((map: google.maps.Map) => {
    setMap(map);
  }, []);

  const onUnmount = useCallback(() => {
    setMap(null);
  }, []);

  if (!isLoaded) return <div>Loading Map...</div>;

  return (
    <div className="rounded-lg overflow-hidden border border-gray-300">
      <GoogleMap
        mapContainerStyle={containerStyle}
        center={selectedLocation || { lat: 20.5937, lng: 78.9629 }} // Default to India center
        zoom={selectedLocation ? 15 : 5}
        onClick={onMapClick}
        onLoad={onLoad}
        onUnmount={onUnmount}
      >
        {selectedLocation && (
          <Marker
            position={selectedLocation}
          />
        )}
      </GoogleMap>
    </div>
  );
};

export default GoogleMapLocationPicker;