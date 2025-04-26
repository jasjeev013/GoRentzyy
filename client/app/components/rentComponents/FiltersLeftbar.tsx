import React, { useState } from 'react';
import { FiFilter, FiChevronDown, FiChevronUp } from 'react-icons/fi';
import { Slider } from '../../../@/components/ui/slider';
import { Checkbox } from '../../../@/components/ui/checkbox';
import { Label } from '../../../@/components/ui/label';
import { Button } from '../../../@/components/ui/button';
import { Input } from '../../../@/components/ui/input';

type FilterCategory = 'segment' | 'fuelType' | 'transmission' | 'luggage' | 'rating' | 'addon';

interface SelectedFilters {
  segment: string[];
  fuelType: string[];
  transmission: string[];
  luggage: string[];
  rating: string[];
  addon: string[];
}

const FiltersLeftbar = () => {
  const [showMoreSegments, setShowMoreSegments] = useState(false);
  const [selectedFilters, setSelectedFilters] = useState<SelectedFilters>({
    segment: [],
    fuelType: [],
    transmission: [],
    luggage: [],
    rating: [],
    addon: []
  });

  const [distance, setDistance] = useState([50]);
  const [priceRange, setPriceRange] = useState([0, 10000]);
  const [seatingCapacity, setSeatingCapacity] = useState([5]);
  const [yearRange, setYearRange] = useState([2018, 2023]);

  const toggleFilter = (category: FilterCategory, value: string) => {
    setSelectedFilters(prev => ({
      ...prev,
      [category]: prev[category].includes(value)
        ? prev[category].filter(item => item !== value)
        : [...prev[category], value]
    }));
  };

  const clearAllFilters = () => {
    setSelectedFilters({
      segment: [],
      fuelType: [],
      transmission: [],
      luggage: [],
      rating: [],
      addon: []
    });
    setDistance([50]);
    setPriceRange([0, 10000]);
    setSeatingCapacity([5]);
    setYearRange([2018, 2023]);
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

      {/* Segment Filter */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Segment</h3>
        {['Subscription', 'Super Economy', 'Economy', 'Compact'].map(segment => (
          <div key={segment} className="flex items-center space-x-2 mb-2">
            <Checkbox
              id={`segment-${segment}`}
              checked={selectedFilters.segment.includes(segment)}
              onCheckedChange={() => toggleFilter('segment', segment)}
            />
            <Label htmlFor={`segment-${segment}`}>{segment}</Label>
          </div>
        ))}
        <Button
          variant="ghost"
          size="sm"
          className="text-primary hover:text-primary mt-2 pl-0"
          onClick={() => setShowMoreSegments(!showMoreSegments)}
        >
          {showMoreSegments ? (
            <FiChevronUp className="mr-1" />
          ) : (
            <FiChevronDown className="mr-1" />
          )}
          View {showMoreSegments ? 'Less' : 'More'}
        </Button>
      </div>

      {/* Distance Filter */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Distance (km)</h3>
        <Slider
          value={distance}
          onValueChange={setDistance}
          min={0}
          max={100}
          step={1}
        />
        <div className="flex justify-between text-sm text-muted-foreground mt-2">
          <span>0 km</span>
          <span>{distance[0]} km</span>
          <span>100 km</span>
        </div>
      </div>

      {/* Price Range */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Price Range (₹)</h3>
        <div className="flex items-center gap-2 mb-3">
          <Input
            type="number"
            value={priceRange[0]}
            onChange={(e) => setPriceRange([Number(e.target.value), priceRange[1]])}
            className="w-full"
          />
          <span className="text-muted-foreground">to</span>
          <Input
            type="number"
            value={priceRange[1]}
            onChange={(e) => setPriceRange([priceRange[0], Number(e.target.value)])}
            className="w-full"
          />
        </div>
        <Slider
          value={priceRange}
          onValueChange={setPriceRange}
          min={0}
          max={10000}
          step={100}
          minStepsBetweenThumbs={1}
        />
      </div>

      {/* Fuel Type */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Fuel Type</h3>
        {['CNG', 'Petrol', 'Diesel', 'Electric'].map(fuel => (
          <div key={fuel} className="flex items-center space-x-2 mb-2">
            <Checkbox
              id={`fuel-${fuel}`}
              checked={selectedFilters.fuelType.includes(fuel)}
              onCheckedChange={() => toggleFilter('fuelType', fuel)}
            />
            <Label htmlFor={`fuel-${fuel}`}>{fuel}</Label>
          </div>
        ))}
      </div>

      {/* Transmission */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Transmission</h3>
        {['Manual', 'Automatic', 'IMT'].map(transmission => (
          <div key={transmission} className="flex items-center space-x-2 mb-2">
            <Checkbox
              id={`transmission-${transmission}`}
              checked={selectedFilters.transmission.includes(transmission)}
              onCheckedChange={() => toggleFilter('transmission', transmission)}
            />
            <Label htmlFor={`transmission-${transmission}`}>{transmission}</Label>
          </div>
        ))}
      </div>

      {/* Seating Capacity */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Seating Capacity</h3>
        <Slider
          value={seatingCapacity}
          onValueChange={setSeatingCapacity}
          min={2}
          max={8}
          step={1}
        />
        <div className="flex justify-between text-sm text-muted-foreground mt-2">
          <span>2 seats</span>
          <span>{seatingCapacity[0]} seats</span>
          <span>8 seats</span>
        </div>
      </div>

      {/* Luggage Capacity */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Luggage Capacity</h3>
        {['Small (1-2 bags)', 'Medium (3-4 bags)', 'Large (5+ bags)'].map(size => (
          <div key={size} className="flex items-center space-x-2 mb-2">
            <Checkbox
              id={`luggage-${size}`}
              checked={selectedFilters.luggage.includes(size)}
              onCheckedChange={() => toggleFilter('luggage', size)}
            />
            <Label htmlFor={`luggage-${size}`}>{size}</Label>
          </div>
        ))}
      </div>

      {/* User Rating */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">User Rating</h3>
        {['4.5+', '4.0+', '3.5+', '3.0+'].map(rating => (
          <div key={rating} className="flex items-center space-x-2 mb-2">
            <Checkbox
              id={`rating-${rating}`}
              checked={selectedFilters.rating.includes(rating)}
              onCheckedChange={() => toggleFilter('rating', rating)}
            />
            <Label htmlFor={`rating-${rating}`}>{rating} ★</Label>
          </div>
        ))}
      </div>

      {/* Model Year */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Model Year</h3>
        <div className="flex items-center gap-2 mb-3">
          <Input
            type="number"
            value={yearRange[0]}
            onChange={(e) => setYearRange([Number(e.target.value), yearRange[1]])}
            className="w-full"
          />
          <span className="text-muted-foreground">to</span>
          <Input
            type="number"
            value={yearRange[1]}
            onChange={(e) => setYearRange([yearRange[0], Number(e.target.value)])}
            className="w-full"
          />
        </div>
        <Slider
          value={yearRange}
          onValueChange={setYearRange}
          min={2000}
          max={2023}
          step={1}
          minStepsBetweenThumbs={1}
        />
      </div>

      {/* Add-ons */}
      <div className="mb-6">
        <h3 className="font-semibold mb-3">Add-ons</h3>
        {['Active Fastag', 'GoRentzyy Assured', 'Guest Favourite', 'Comfy Pro Max'].map(addon => (
          <div key={addon} className="flex items-center space-x-2 mb-2">
            <Checkbox
              id={`addon-${addon}`}
              checked={selectedFilters.addon.includes(addon)}
              onCheckedChange={() => toggleFilter('addon', addon)}
            />
            <Label htmlFor={`addon-${addon}`}>{addon}</Label>
          </div>
        ))}
      </div>

      <Button className="w-full mt-4">
        Apply Filters
      </Button>
    </div>
  );
};

export default FiltersLeftbar;