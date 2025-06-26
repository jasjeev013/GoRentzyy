interface CarSpecificationsProps {
  car: {
    make: string;
    model: string;
    year: number;
    color: string;
    transmissionMode: string;
    fuelType: string;
    seatingCapacity: number;
    luggageCapacity: number;
    insurance: string;
    roadSideAssistance: boolean | null;
    fuelPolicy: string;
    features: string;
    importantPoints: string;
    createdAt: string;
    updatedAt: string;
  };
}

const CarSpecifications = ({ car }: CarSpecificationsProps) => {
  const formattedCreatedAt = new Date(car.createdAt).toLocaleDateString();
  const formattedUpdatedAt = new Date(car.updatedAt).toLocaleDateString();

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 border border-gray-200 dark:border-gray-700">
      <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">Specifications</h2>
      
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
      
      <div className="space-y-4">
        <div>
          <h3 className="font-medium mb-2">Insurance</h3>
          <p className="text-sm text-gray-600 dark:text-gray-300">{car.insurance}</p>
        </div>
        
        <div>
          <h3 className="font-medium mb-2">Roadside Assistance</h3>
          <p className="text-sm text-gray-600 dark:text-gray-300">
            {car.roadSideAssistance || 'Not available'}
          </p>
        </div>
        
        <div>
          <h3 className="font-medium mb-2">Fuel Policy</h3>
          <p className="text-sm text-gray-600 dark:text-gray-300">{car.fuelPolicy}</p>
        </div>
        
        <div>
          <h3 className="font-medium mb-2">Features</h3>
          <p className="text-sm text-gray-600 dark:text-gray-300">{car.features}</p>
        </div>
        
        <div>
          <h3 className="font-medium mb-2">Important Points</h3>
          <p className="text-sm text-gray-600 dark:text-gray-300">{car.importantPoints}</p>
        </div>
        
        <div className="text-xs text-gray-500 dark:text-gray-400">
          <p>Listed on: {formattedCreatedAt}</p>
          <p>Last updated: {formattedUpdatedAt}</p>
        </div>
      </div>
    </div>
  );
};

export default CarSpecifications;