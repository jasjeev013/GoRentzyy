import React from 'react'
import { useState } from 'react';
import { FiGrid, FiList} from 'react-icons/fi';
import { FaGasPump,  FaSuitcaseRolling, FaUserFriends } from 'react-icons/fa';
import { GiGearStickPattern } from 'react-icons/gi';
const CarListing = () => {
    const [gridView, setGridView] = useState(true);

    const cars = [
        {
            id: 1,
            name: "Maruti Swift",
            image: "/cars/swift.jpg",
            segment: "Economy",
            plans: [
                { km: 120, price: 1499 },
                { km: 300, price: 1999 },
                { km: "Unlimited", price: 2499 }
            ],
            transmission: "Manual",
            fuel: "Petrol",
            seats: 5,
            luggage: 2,
            available: true
        },
        // Add more cars...
    ];

    return (
        <>
            <div className="w-full md:w-3/4">
                {/* Sorting Options */}
                <div className="bg-white p-4 rounded-lg shadow-sm mb-6 flex justify-between items-center">
                    <div className="flex items-center">
                        <span className="mr-2">Sort by:</span>
                        <select className="p-2 border rounded-lg">
                            <option>Price: Low to High</option>
                            <option>Price: High to Low</option>
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
                    {cars.map(car => (
                        <div key={car.id} className="bg-white rounded-lg shadow-sm overflow-hidden hover:shadow-md transition">
                            <div className="relative">
                                <img src={car.image} alt={car.name} className="w-full h-48 object-cover" />
                                <span className="absolute top-2 left-2 bg-purple-600 text-white text-xs px-2 py-1 rounded">
                                    {car.segment}
                                </span>
                                {!car.available && (
                                    <span className="absolute top-2 right-2 bg-red-500 text-white text-xs px-2 py-1 rounded">
                                        Sold Out
                                    </span>
                                )}
                            </div>

                            <div className="p-4">
                                <h3 className="font-bold text-lg">{car.name}</h3>

                                {/* Pricing Plans */}
                                <div className="flex justify-between my-4">
                                    {car.plans.map((plan, i) => (
                                        <div key={i} className="text-center">
                                            <p className="text-xs text-gray-500">{plan.km}km/day</p>
                                            <p className="font-bold">₹{plan.price}</p>
                                        </div>
                                    ))}
                                </div>

                                {/* Car Features */}
                                <div className="flex justify-between text-sm text-gray-600 mb-4">
                                    <div className="flex items-center">
                                        <GiGearStickPattern className="mr-1" /> {car.transmission}
                                    </div>
                                    <div className="flex items-center">
                                        <FaGasPump className="mr-1" /> {car.fuel}
                                    </div>
                                    <div className="flex items-center">
                                        <FaSuitcaseRolling className="mr-1" /> {car.luggage}
                                    </div>
                                    <div className="flex items-center">
                                        <FaUserFriends className="mr-1" /> {car.seats}
                                    </div>
                                </div>

                                <p className="text-xs text-gray-500 mb-3">Extra km charges: ₹7/km</p>

                                <button className="w-full border border-purple-600 text-purple-600 py-2 rounded-lg hover:bg-purple-50 transition">
                                    View Details
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </>
    )
}

export default CarListing
