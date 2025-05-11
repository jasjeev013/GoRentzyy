'use client';
import {useRouter } from 'next/navigation';
import React, { useState } from 'react'
import { FaGasPump, FaSuitcaseRolling, FaUserFriends } from 'react-icons/fa';
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
    gridView
}: CarProps) => {
    const [isNavigating, setIsNavigating] = useState(false);
    const router = useRouter();

    const handleViewDetails = () => {
        setIsNavigating(true);
        router.push(`/rent/${carId}`);
        setIsNavigating(false);
    };

    return (
        <div key={carId} className={`${gridView ? '' : 'flex flex-row'} bg-[#180D0D] rounded-lg shadow-sm overflow-hidden hover:shadow-md transition`}>
            <div className={`relative ${gridView ? '' : 'w-110'}`}>
                <img 
                    src={photos[0]} 
                    alt={name} 
                    className={`w-full ${gridView ? 'h-48' : 'h-60'} object-cover`} 
                    loading="lazy" // Add lazy loading for better performance
                />
                <span className="absolute top-2 left-2 bg-purple-600 text-white text-xs px-2 py-1 rounded">
                    {carType}
                </span>
                <span className="absolute top-2 right-2 bg-blue-600 text-white text-xs px-2 py-1 rounded">
                    {carCategory}
                </span>
                {availabilityStatus !== 'AVAILABLE' && (
                    <span className="absolute top-9 right-2 bg-red-500 text-white text-xs px-2 py-1 rounded">
                        {availabilityStatus === 'RESERVED' ? 'Reserved' : 'Under Maintenance'}
                    </span>
                )}
            </div>

            <div className={`p-4 ${gridView ? '' : 'w-full flex flex-col justify-between'}`}>
                <h3 className="font-bold text-lg">{name}</h3>

                {/* Pricing Plans */}
                <div className={`flex ${gridView ? 'justify-between' : 'gap-13'} my-4`}>
                    <div className="text-center">
                        <p className="font-bold">₹{rentalPricePerDay}</p>
                        <p className="text-xs text-gray-500">Per Day</p>
                    </div>
                    <div className="text-center">
                        <p className="font-bold">₹{rentalPricePerWeek}</p>
                        <p className="text-xs text-gray-500">Per Week</p>
                    </div>
                    <div className="text-center">
                        <p className="font-bold">₹{rentalPricePerMonth}</p>
                        <p className="text-xs text-gray-500">Per Month</p>
                    </div>
                </div>

                {/* Car Features */}
                <div className="flex justify-between text-sm text-gray-600 mb-4">
                    <div className="flex items-center">
                        <GiGearStickPattern className="mr-1" /> {transmissionMode}
                    </div>
                    <div className="flex items-center">
                        <FaGasPump className="mr-1" /> {fuelType}
                    </div>
                    <div className="flex items-center">
                        <FaSuitcaseRolling className="mr-1" /> {luggageCapacity}
                    </div>
                    <div className="flex items-center">
                        <FaUserFriends className="mr-1" /> {seatingCapacity}
                    </div>
                </div>

                <p className="text-xs text-gray-500 mb-3">Extra km charges: ₹7/km</p>

                <button 
                    onClick={handleViewDetails}
                    disabled={isNavigating}
                    className={`w-full border border-purple-600 text-purple-600 py-2 rounded-lg hover:bg-purple-50 transition flex items-center justify-center ${
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
    );
};



export default Car;