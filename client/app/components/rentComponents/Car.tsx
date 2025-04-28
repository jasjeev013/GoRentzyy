import React from 'react'
import { FaGasPump, FaSuitcaseRolling, FaUserFriends } from 'react-icons/fa';
import { GiGearStickPattern } from 'react-icons/gi';
const Car = ({ carId, photos, name, category, availabilityStatus,rentalPricePerDay,rentalPricePerWeek,rentalPricePerMonth, transmissionMode, fuelType, luggageCapacity, seatingCapacity }) => {
    console.log(photos[0])
    return (
        <>
            <div key={carId} className="bg-[#180D0D] rounded-lg shadow-sm overflow-hidden hover:shadow-md transition">
                <div className="relative">
                   
                    <img src={photos[0]} alt={name} className="w-full h-48 object-cover" />
                    <span className="absolute top-2 left-2 bg-purple-600 text-white text-xs px-2 py-1 rounded">
                        {category}
                    </span>
                    {!availabilityStatus && (
                        <span className="absolute top-2 right-2 bg-red-500 text-white text-xs px-2 py-1 rounded">
                            Sold Out
                        </span>
                    )}
                </div>

                <div className="p-4">
                    <h3 className="font-bold text-lg">{name}</h3>

                    {/* Pricing Plans */}
                    <div className="flex justify-between my-4">

                        <div  className="text-center">
                            <p className="font-bold">₹{rentalPricePerDay}</p>
                            <p className="text-xs text-gray-500">Per Day</p>
                        </div>
                        <div  className="text-center">
                            <p className="font-bold">₹{rentalPricePerWeek}</p>
                            <p className="text-xs text-gray-500">Per Week</p>
                        </div>
                        <div  className="text-center">
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

                    <button className="w-full border border-purple-600 text-purple-600 py-2 rounded-lg hover:bg-purple-50 transition">
                        View Details
                    </button>
                </div>
            </div>
        </>
    )
}

export default Car
