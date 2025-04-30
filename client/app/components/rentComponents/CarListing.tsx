import React from 'react'
import { useState } from 'react';
import { FiGrid, FiList } from 'react-icons/fi';
import Car from './Car';
import { CarListingProps } from '../../types';



const CarListing = ({ cars, sortOption, onSortChange }: CarListingProps) => {
    const [gridView, setGridView] = useState(true);


    return (
        <div className="w-full md:w-3/4">
            {/* Sorting Options */}
            <div className="p-4 rounded-lg shadow-sm mb-6 flex justify-between items-center">
                <div className="flex items-center">
                    <span className="mr-2">Sort by:</span>
                    <select
                        className="p-2 bg-black border rounded-lg"
                        value={sortOption}
                        onChange={(e) => onSortChange(e.target.value)}
                    >
                        <option value="price-low">Price: Low to High</option>
                        <option value="price-high">Price: High to Low</option>
                        <option value="year-new">Year: Newest First</option>
                        <option value="year-old">Year: Oldest First</option>
                    </select>
                </div>
                <div className="flex">
                    <button
                        className={`p-2 rounded-lg mr-2 ${gridView ? 'bg-purple-100 text-purple-600' : 'bg-gray-100'}`}
                        onClick={() => setGridView(true)}
                    >
                        <FiGrid />
                    </button>
                    <button
                        className={`p-2 rounded-lg ${!gridView ? 'bg-purple-100 text-purple-600' : 'bg-gray-100'}`}
                        onClick={() => setGridView(false)}
                    >
                        <FiList />
                    </button>
                </div>
            </div>

            {/* Car Cards */}
            <div className={`${gridView ? 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3' : 'flex flex-col'} gap-6`}>
                {cars && cars.map(car => (
                    <Car
                        key={car.carId}
                        {...car}
                        gridView={gridView}
                    />
                ))}
            </div>
        </div>
    )
}

export default CarListing