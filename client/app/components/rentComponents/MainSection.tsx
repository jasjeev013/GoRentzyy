'use client';
import { useState, useMemo, useEffect } from 'react';
import CarListing from './CarListing';
import FiltersLeftbar from './FiltersLeftbar';
import { useCarStore } from '../../../stores/carStore';

const MainSection = () => {

    const [searchTerm, setSearchTerm] = useState('');
    const [sortOption, setSortOption] = useState('price-low');
    const { cars, availableCars, fetchAllCars } = useCarStore();

    // Use availableCars if they exist, otherwise fall back to all cars
    const displayCars = availableCars.length > 0 ? availableCars : cars;

    useEffect(() => {
        if (cars.length === 0) {
            // fetchAllCars();
        }
    }, [cars.length, fetchAllCars]);
    
    
    // State for filters
    const [filters, setFilters] = useState({
        carCategory: [] as string[],
        carType: [] as string[],
        fuelType: [] as string[],
        transmission: [] as string[],
        luggage: [] as string[],
        minPrice: 0,
        maxPrice: 10000,
        minYear: 2000,
        maxYear: 2023,
        seatingCapacity: 5,
    });

    // Filter and sort cars
    const filteredCars = useMemo(() => {
       let result = [...displayCars];
        
        if (searchTerm) {
            const term = searchTerm.toLowerCase();
            result = result.filter(car =>
                car.name.toLowerCase().includes(term) ||
                car.make.toLowerCase().includes(term) ||
                car.model.toLowerCase().includes(term) ||
                car.location.city.toLowerCase().includes(term)
            );
        }
        // Car Category filter
        if (filters.carCategory.length > 0) {
            result = result.filter(car =>
                filters.carCategory.includes(car.carCategory)
            );
        }

        // Car Type filter
        if (filters.carType.length > 0) {
            result = result.filter(car =>
                filters.carType.includes(car.carType)
            );
        }

        // Fuel type filter
        if (filters.fuelType.length > 0) {
            result = result.filter(car =>
                filters.fuelType.includes(car.fuelType)
            );
        }

        // Transmission filter
        if (filters.transmission.length > 0) {
            result = result.filter(car =>
                filters.transmission.includes(car.transmissionMode)
            );
        }

        // Price range filter
        result = result.filter(car =>
            car.rentalPricePerDay >= filters.minPrice &&
            car.rentalPricePerDay <= filters.maxPrice
        );

        // Year range filter
        result = result.filter(car =>
            car.year >= filters.minYear &&
            car.year <= filters.maxYear
        );

        // Seating capacity filter
        result = result.filter(car =>
            car.seatingCapacity >= filters.seatingCapacity
        );

        // Sorting
        if (sortOption === 'price-low') {
            result.sort((a, b) => a.rentalPricePerDay - b.rentalPricePerDay);
        } else if (sortOption === 'price-high') {
            result.sort((a, b) => b.rentalPricePerDay - a.rentalPricePerDay);
        } else if (sortOption === 'year-new') {
            result.sort((a, b) => b.year - a.year);
        } else if (sortOption === 'year-old') {
            result.sort((a, b) => a.year - b.year);
        }

        return result;
    }, [displayCars, searchTerm]);

    return (
        <div className="min-h-screen">
            {/* Search Bar Section */}
            <div className="shadow-md mx-20 p-4 rounded-lg">
                <div className="flex items-center gap-4">
                    <input
                        type="text"
                        placeholder="Search by make, model, or location"
                        className="border border-gray-300 rounded-lg p-2 w-full"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                    <button className="bg-blue-500 text-white px-4 py-2 rounded-lg">
                        Search
                    </button>
                </div>
            </div>

            {/* Main Content */}
            <div className="max-w-7xl mx-auto p-6">
                <div className="flex flex-col md:flex-row gap-6">
                    <FiltersLeftbar
                        onFilterChange={setFilters}
                        currentFilters={filters}
                    />
                    <CarListing
                        cars={filteredCars}
                        sortOption={sortOption}
                        onSortChange={setSortOption}
                    />
                </div>
            </div>
        </div>
    );
}

export default MainSection;