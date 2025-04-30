import React from 'react';
import { Car, CarSpecificationsProps } from '../../types';



const CarSpecifications = ({ car }: CarSpecificationsProps) => {
  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
      <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">Car Details</h2>
      
      <div className="grid grid-cols-2 gap-4 mb-6">
        <div>
          <p className="text-sm text-gray-500 dark:text-gray-400">Make</p>
          <p className="font-medium">{car.make}</p>
        </div>
        <div>
          <p className="text-sm text-gray-500 dark:text-gray-400">Model</p>
          <p className="font-medium">{car.model}</p>
        </div>
        <div>
          <p className="text-sm text-gray-500 dark:text-gray-400">Year</p>
          <p className="font-medium">{car.year}</p>
        </div>
        <div>
          <p className="text-sm text-gray-500 dark:text-gray-400">Color</p>
          <p className="font-medium">{car.color}</p>
        </div>
        <div>
          <p className="text-sm text-gray-500 dark:text-gray-400">Transmission</p>
          <p className="font-medium">{car.transmissionMode}</p>
        </div>
        <div>
          <p className="text-sm text-gray-500 dark:text-gray-400">Fuel Type</p>
          <p className="font-medium">{car.fuelType}</p>
        </div>
        <div>
          <p className="text-sm text-gray-500 dark:text-gray-400">Seats</p>
          <p className="font-medium">{car.seatingCapacity}</p>
        </div>
        <div>
          <p className="text-sm text-gray-500 dark:text-gray-400">Luggage</p>
          <p className="font-medium">{car.luggageCapacity} bags</p>
        </div>
      </div>
      
      <div className="mb-6">
        <h3 className="font-medium mb-2">Insurance</h3>
        <p className="text-sm text-gray-600 dark:text-gray-300">{car.insurance}</p>
      </div>
      
      <div className="mb-6">
        <h3 className="font-medium mb-2">Roadside Assistance</h3>
        <p className="text-sm text-gray-600 dark:text-gray-300">{car.roadsideAssistance}</p>
      </div>
      
      <div className="mb-6">
        <h3 className="font-medium mb-2">Fuel Policy</h3>
        <p className="text-sm text-gray-600 dark:text-gray-300">{car.fuelPolicy}</p>
      </div>
      
      <div>
        <h3 className="font-medium mb-2">Important Points</h3>
        <ul className="list-disc pl-5 space-y-1 text-sm text-gray-600 dark:text-gray-300">
          {car.importantPoints?.map((point, index) => (
            <li key={index}>{point}</li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default CarSpecifications;