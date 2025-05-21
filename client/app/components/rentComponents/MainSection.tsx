'use client';
import { useState, useMemo } from 'react';
import CarListing from './CarListing';
import FiltersLeftbar from './FiltersLeftbar';
import { useCarStore } from '../../../stores/carStore';
import { Loader2 } from 'lucide-react';

const MainSection = () => {
    const [searchTerm, setSearchTerm] = useState('');
    const [sortOption, setSortOption] = useState('price-low');
    const { cars, availableCars, loading } = useCarStore();

    // Determine which cars to display
    const displayCars = availableCars.length > 0 ? availableCars : cars;


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
        console.log('Filtered Cars:', result);

        if (searchTerm) {
            const term = searchTerm.toLowerCase();
            result = result.filter(car =>
                car.name.toLowerCase().includes(term) ||
                car.make.toLowerCase().includes(term) ||
                car.model.toLowerCase().includes(term) 
            );
        }
    


        // Apply filters
        if (filters.carCategory.length > 0) {
            result = result.filter(car => filters.carCategory.includes(car.carCategory));
        }

        if (filters.carType.length > 0) {
            result = result.filter(car => filters.carType.includes(car.carType));
        }

        if (filters.fuelType.length > 0) {
            result = result.filter(car => filters.fuelType.includes(car.fuelType));
        }

        if (filters.transmission.length > 0) {
            result = result.filter(car => filters.transmission.includes(car.transmissionMode));
        }

        result = result.filter(car =>
            car.rentalPricePerDay >= filters.minPrice &&
            car.rentalPricePerDay <= filters.maxPrice
        );

        result = result.filter(car =>
            car.year >= filters.minYear &&
            car.year <= filters.maxYear
        );

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
    }, [displayCars, searchTerm, filters, sortOption]);

    return (
        <div className="min-h-screen">
            {loading ? (
                <div className="flex justify-center items-center h-64">
                    <Loader2 className="h-12 w-12 animate-spin" />
                </div>
            ) : (
                <>
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
                </>
            )}
        </div>
    );
}

export default MainSection;