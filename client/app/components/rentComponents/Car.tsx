'use client';
import { useRouter } from 'next/navigation';
import React, { useState } from 'react';
import { FaGasPump, FaSuitcaseRolling, FaUserFriends, FaStar, FaRegStar } from 'react-icons/fa';
import { GiGearStickPattern } from 'react-icons/gi';
import { CarProps } from '../../types';

const Car = ({
    carId,
    photos,
    name,
    carCategory,
    carType,
    availabilityStatus,
    rentalPricePerDay,
    rentalPricePerWeek,
    rentalPricePerMonth,
    transmissionMode,
    fuelType,
    luggageCapacity,
    seatingCapacity,
    gridView,
    host,
    reviews,
    location
}: CarProps) => {
    const [isNavigating, setIsNavigating] = useState(false);
    const router = useRouter();

    // Calculate average rating
    const averageRating = reviews.length > 0 
        ? (reviews.reduce((sum, review) => sum + review.rating, 0) / reviews.length)
        : 0;

    const handleViewDetails = () => {
        setIsNavigating(true);
        router.push(`/rent/${carId}`);
        setIsNavigating(false);
    };

    // Render star ratings
    const renderStars = () => {
        const stars = [];
        const fullStars = Math.floor(averageRating);
        const hasHalfStar = averageRating % 1 >= 0.5;
        
        for (let i = 1; i <= 5; i++) {
            if (i <= fullStars) {
                stars.push(<FaStar key={i} className="text-yellow-400" />);
            } else if (i === fullStars + 1 && hasHalfStar) {
                stars.push(<FaStar key={i} className="text-yellow-400 opacity-70" />);
            } else {
                stars.push(<FaRegStar key={i} className="text-yellow-400" />);
            }
        }
        
        return (
            <div className="flex items-center">
                {stars}
                <span className="ml-1 text-sm text-gray-600">
                    ({reviews.length})
                </span>
            </div>
        );
    };

    return (
        <div key={carId} className={`${gridView ? '' : 'flex flex-row'} bg-white dark:bg-gray-800 rounded-lg shadow-sm overflow-hidden hover:shadow-md transition border border-gray-200 dark:border-gray-700`}>
            <div className={`relative ${gridView ? '' : 'w-110'}`}>
                <img 
                    src={photos[0]} 
                    alt={name} 
                    className={`w-full ${gridView ? 'h-48' : 'h-60'} object-cover`} 
                    loading="lazy"
                />
                <div className="absolute top-2 left-2 flex flex-col gap-1">
                    <span className="bg-purple-600 text-white text-xs px-2 py-1 rounded">
                        {carType}
                    </span>
                    <span className="bg-blue-600 text-white text-xs px-2 py-1 rounded">
                        {carCategory}
                    </span>
                </div>
                {availabilityStatus !== 'AVAILABLE' && (
                    <span className="absolute top-2 right-2 bg-red-500 text-white text-xs px-2 py-1 rounded">
                        {availabilityStatus === 'RESERVED' ? 'Reserved' : 'Under Maintenance'}
                    </span>
                )}
            </div>

            <div className={`p-4 ${gridView ? '' : 'w-full flex flex-col justify-between'}`}>
                <div>
                    <div className="flex justify-between items-start">
                        <h3 className="font-bold text-lg text-gray-900 dark:text-white">{name}</h3>
                        {reviews.length > 0 && renderStars()}
                    </div>
                    
                    <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                        {location?.city} • {host.fullName}
                    </p>

                    {/* Pricing Plans */}
                    <div className={`flex ${gridView ? 'justify-between' : 'gap-13'} my-4`}>
                        <div className="text-center">
                            <p className="font-bold text-gray-900 dark:text-white">₹{rentalPricePerDay}</p>
                            <p className="text-xs text-gray-500">Per Day</p>
                        </div>
                        <div className="text-center">
                            <p className="font-bold text-gray-900 dark:text-white">₹{rentalPricePerWeek}</p>
                            <p className="text-xs text-gray-500">Per Week</p>
                        </div>
                        <div className="text-center">
                            <p className="font-bold text-gray-900 dark:text-white">₹{rentalPricePerMonth}</p>
                            <p className="text-xs text-gray-500">Per Month</p>
                        </div>
                    </div>

                    {/* Car Features */}
                    <div className="grid grid-cols-2 gap-2 text-sm text-gray-600 dark:text-gray-400 mb-4">
                        <div className="flex items-center">
                            <GiGearStickPattern className="mr-1" /> {transmissionMode}
                        </div>
                        <div className="flex items-center">
                            <FaGasPump className="mr-1" /> {fuelType}
                        </div>
                        <div className="flex items-center">
                            <FaSuitcaseRolling className="mr-1" /> {luggageCapacity} bags
                        </div>
                        <div className="flex items-center">
                            <FaUserFriends className="mr-1" /> {seatingCapacity} seats
                        </div>
                    </div>
                </div>

                <div className="flex flex-col gap-2">
                    <p className="text-xs text-gray-500">Extra km charges: ₹7/km</p>
                    <button 
                        onClick={handleViewDetails}
                        disabled={isNavigating}
                        className={`w-full border border-purple-600 text-purple-600 dark:text-purple-400 dark:border-purple-400 py-2 rounded-lg hover:bg-purple-50 dark:hover:bg-purple-900/20 transition flex items-center justify-center ${
                            isNavigating ? 'opacity-75 cursor-not-allowed' : ''
                        }`}
                    >
                        {isNavigating ? (
                            <>
                                <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-purple-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                                Loading...
                            </>
                        ) : (
                            'View Details'
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Car;