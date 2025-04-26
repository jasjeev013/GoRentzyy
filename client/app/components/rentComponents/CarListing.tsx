import React from 'react'
import { useState } from 'react';
import { FiGrid, FiList } from 'react-icons/fi';
import { FaGasPump, FaSuitcaseRolling, FaUserFriends } from 'react-icons/fa';
import { GiGearStickPattern } from 'react-icons/gi';
import Car from './Car'; // Adjust the path if the Car component is in a different directory

const CarListing = () => {
    const [gridView, setGridView] = useState(true);

    const cars = [
        {
            id: 1,
            name: "Maruti Swift",
            image: "https://zoomcar-assets.zoomcar.com/713948/HostCarImage/RackMultipart20240721-30-1761pgdf93bc06c-fc4d-4b96-9d75-078e095332cd.jpg",
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
        {
            id: 2,
            name: "Maruti Swift",
            image: "https://zoomcar-assets.zoomcar.com/820902/HostCarImage/RackMultipart20241117-32-m3te5y1c250dc0-1a49-416e-89bf-bfff74ace3f9.jpg",
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
        {
            id: 3,
            name: "Maruti Swift",
            image: "https://zoomcar-assets.zoomcar.com/734351/HostCarImage/mini_magick20241205-3999-yaei192a2634ad-7241-43de-bc06-5db507bd38b7.jpeg",
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
        {
            id: 4,
            name: "Maruti Swift",
            image: "https://zoomcar-assets.zoomcar.com/774243/HostCarImage/mini_magick20241205-3999-3jt9lye11e0499-5b30-4a63-b306-2b663f8aa666.jpeg",
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
                <div className=" p-4 rounded-lg shadow-sm mb-6 flex justify-between items-center">
                    <div className="flex items-center">
                        <span className="mr-2">Sort by:</span>
                        <select className="p-2 bg-black border rounded-lg">
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

                        <Car
                            key={car.id}
                            id={car.id}
                            image={car.image}
                            name={car.name}
                            segment={car.segment}
                            available={car.available}
                            plans={car.plans}
                            transmission = {car.transmission}
                            fuel = {car.fuel}
                            luggage = {car.luggage}
                            seats = {car.seats}

                        />
                    ))}
                </div>
            </div>
        </>
    )
}

export default CarListing
