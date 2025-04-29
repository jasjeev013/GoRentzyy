import React, { useState } from 'react';
import { FiFilter, FiChevronDown, FiChevronUp } from 'react-icons/fi';
import { Slider } from '../../../components/ui/slider';
import { Checkbox } from '../../../components/ui/checkbox';
import { Label } from '../../../components/ui/label';
import { Button } from '../../../components/ui/button';
import { Input } from '../../../components/ui/input';

interface Filters {
  carCategory: string[];
  carType: string[];
  fuelType: string[];
  transmission: string[];
  luggage: string[];
  minPrice: number;
  maxPrice: number;
  minYear: number;
  maxYear: number;
  seatingCapacity: number;
}

interface FiltersLeftbarProps {
  onFilterChange: (filters: Filters) => void;
  currentFilters: Filters;
}

const FiltersLeftbar = ({ onFilterChange, currentFilters }: FiltersLeftbarProps) => {
  const [showMoreSegments, setShowMoreSegments] = useState(false);

  const handleFilterChange = (filterType: keyof Filters, value: any) => {
    onFilterChange({
      ...currentFilters,
      [filterType]: value
    });
  };

  const toggleArrayFilter = (filterType: keyof Filters, value: string) => {
    const currentValues = currentFilters[filterType] as string[];
    const newValues = currentValues.includes(value)
      ? currentValues.filter(item => item !== value)
      : [...currentValues, value];
    
    handleFilterChange(filterType, newValues);
  };

  const clearAllFilters = () => {
    onFilterChange({
      carCategory: [],
      carType: [],
      fuelType: [],
      transmission: [],
      luggage: [],
      minPrice: 0,
      maxPrice: 10000,
      minYear: 2000,
      maxYear: 2023,
      seatingCapacity: 5,
    });
  };

  return (
    <div className="w-full md:w-1/4 p-6 rounded-lg border bg-background shadow-sm">
      <div className="flex items-center justify-between mb-4">
        <h2 className="font-bold flex items-center text-lg">
          <FiFilter className="mr-2" /> Filters
        </h2>
        <Button 
          variant="ghost" 
          size="sm" 
          onClick={clearAllFilters}
          className="text-primary hover:text-primary"
        >
          Clear All
        </Button>
      </div>

      {/* Car Category Filter */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Car Category</h3>
        {['SEDAN', 'HATCHBACK', 'SUV', 'MINIVAN'].map(category => (
          <div key={category} className="flex items-center space-x-2 mb-2">
            <Checkbox
              id={`category-${category}`}
              checked={currentFilters.carCategory.includes(category)}
              onCheckedChange={() => toggleArrayFilter('carCategory', category)}
            />
            <Label htmlFor={`category-${category}`}>{category}</Label>
          </div>
        ))}
      </div>

      {/* Car Type Filter */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Car Type</h3>
        {['ECONOMY', 'LUXURY', 'ELECTRIC', 'HYBRID'].map(type => (
          <div key={type} className="flex items-center space-x-2 mb-2">
            <Checkbox
              id={`type-${type}`}
              checked={currentFilters.carType.includes(type)}
              onCheckedChange={() => toggleArrayFilter('carType', type)}
            />
            <Label htmlFor={`type-${type}`}>{type}</Label>
          </div>
        ))}
      </div>

      {/* Price Range */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Price Range (₹)</h3>
        <div className="flex items-center gap-2 mb-3">
          <Input
            type="number"
            value={currentFilters.minPrice}
            onChange={(e) => handleFilterChange('minPrice', Number(e.target.value))}
            className="w-full"
          />
          <span className="text-muted-foreground">to</span>
          <Input
            type="number"
            value={currentFilters.maxPrice}
            onChange={(e) => handleFilterChange('maxPrice', Number(e.target.value))}
            className="w-full"
          />
        </div>
        <Slider
          value={[currentFilters.minPrice, currentFilters.maxPrice]}
          onValueChange={(value) => {
            handleFilterChange('minPrice', value[0]);
            handleFilterChange('maxPrice', value[1]);
          }}
          min={0}
          max={10000}
          step={100}
          minStepsBetweenThumbs={1}
        />
      </div>

      {/* Fuel Type */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Fuel Type</h3>
        {['PETROL', 'DIESEL', 'ELECTRIC', 'CNG'].map(fuel => (
          <div key={fuel} className="flex items-center space-x-2 mb-2">
            <Checkbox
              id={`fuel-${fuel}`}
              checked={currentFilters.fuelType.includes(fuel)}
              onCheckedChange={() => toggleArrayFilter('fuelType', fuel)}
            />
            <Label htmlFor={`fuel-${fuel}`}>{fuel}</Label>
          </div>
        ))}
      </div>

      {/* Transmission */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Transmission</h3>
        {['MANUAL', 'AUTOMATIC', 'IMT'].map(transmission => (
          <div key={transmission} className="flex items-center space-x-2 mb-2">
            <Checkbox
              id={`transmission-${transmission}`}
              checked={currentFilters.transmission.includes(transmission)}
              onCheckedChange={() => toggleArrayFilter('transmission', transmission)}
            />
            <Label htmlFor={`transmission-${transmission}`}>{transmission}</Label>
          </div>
        ))}
      </div>

      {/* Seating Capacity */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Seating Capacity</h3>
        <Slider
          value={[currentFilters.seatingCapacity]}
          onValueChange={(value) => handleFilterChange('seatingCapacity', value[0])}
          min={2}
          max={12}
          step={1}
        />
        <div className="flex justify-between text-sm text-muted-foreground mt-2">
          <span>2 seats</span>
          <span>{currentFilters.seatingCapacity} seats</span>
          <span>12 seats</span>
        </div>
      </div>

      {/* Model Year */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Model Year</h3>
        <div className="flex items-center gap-2 mb-3">
          <Input
            type="number"
            value={currentFilters.minYear}
            onChange={(e) => handleFilterChange('minYear', Number(e.target.value))}
            className="w-full"
          />
          <span className="text-muted-foreground">to</span>
          <Input
            type="number"
            value={currentFilters.maxYear}
            onChange={(e) => handleFilterChange('maxYear', Number(e.target.value))}
            className="w-full"
          />
        </div>
        <Slider
          value={[currentFilters.minYear, currentFilters.maxYear]}
          onValueChange={(value) => {
            handleFilterChange('minYear', value[0]);
            handleFilterChange('maxYear', value[1]);
          }}
          min={2000}
          max={2023}
          step={1}
          minStepsBetweenThumbs={1}
        />
      </div>
    </div>
  );
};

export default FiltersLeftbar;