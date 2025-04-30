'use client';
import { RadioGroup, RadioGroupItem } from '../../../components/ui/radio-group';
import { Label } from '../../../components/ui/label';

const PickupOptions = () => {
  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
      <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">Pickup & Drop Location</h2>
      
      <RadioGroup defaultValue="home" className="space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <RadioGroupItem value="home" id="home" />
            <Label htmlFor="home">From renter's home</Label>
          </div>
          <span className="text-sm text-gray-600 dark:text-gray-300">₹0</span>
        </div>
        
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <RadioGroupItem value="airport" id="airport" />
            <Label htmlFor="airport">Airport/Railway Station (Within 5km)</Label>
          </div>
          <span className="text-sm text-gray-600 dark:text-gray-300">₹100</span>
        </div>
        
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <RadioGroupItem value="metro" id="metro" />
            <Label htmlFor="metro">Metro Station (Within 5km)</Label>
          </div>
          <span className="text-sm text-gray-600 dark:text-gray-300">₹100</span>
        </div>
        
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <RadioGroupItem value="beyond" id="beyond" />
            <Label htmlFor="beyond">Beyond 10km</Label>
          </div>
          <span className="text-sm text-gray-600 dark:text-gray-300">₹200</span>
        </div>
      </RadioGroup>
    </div>
  );
};

export default PickupOptions;