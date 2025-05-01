import React from 'react'
import { CarCategory, CarType, FuelType, TransmissionMode, AvailabilityStatus } from '../../types/index';
import CarImageGallery from '../../components/carPageComponents/CarImageGallery';
import CarSpecifications from '../../components/carPageComponents/CarSpecifications';
import BookingDetails from '../../components/carPageComponents/BookingDetails';
import PickupOptions from '../../components/carPageComponents/PickupOptions';
import BookingSummary from '../../components/carPageComponents/BookingSummary';

const page = () => {

  const car = {
    "carId": 1,
    "name": "Hyundai i20 2021",
    "make": "Hyundai",
    "model": "i20",
    "year": 2021,
    "color": "White",
    "registrationNumber": "KA02CD4567",
    "photos": [
      "https://gaadiwaadi.com/wp-content/uploads/2020/10/2020-Hyundai-i20-N-Line-3.jpg",
      "https://www.hyundai.com/content/dam/hyundai/template_en/en/images/find-a-car/pip/i20-2021/interior/i20-bc3-design-sporty-4-spoke-steering-wheel-original.jpg",
      "https://assets.autochase.in/cars/37693cfc748049e45d87b8c7d8b9aacd/73c2357eeaf24f.jpg",
      "https://images.hindustantimes.com/auto/img/2021/08/26/414x233/Screenshot_2021-08-26_at_10.57.26_AM_1629955798890_1629955806869.png"
    ],
    "carCategory": CarCategory.HATCHBACK,
    "carType": CarType.ECONOMY,
    "fuelType": FuelType.PETROL,
    "transmissionMode": TransmissionMode.MANUAL,
    "seatingCapacity": 5,
    "luggageCapacity": 2,
    "rentalPricePerDay": 1100.0,
    "rentalPricePerWeek": 6700.0,
    "rentalPricePerMonth": 24000.0,
    "availabilityStatus": AvailabilityStatus.AVAILABLE,
    "maintenanceDueDate": "2025-05-10T09:00:00",
    "location": "Bangalore, Karnataka",
    "insurance": "Comprehensive coverage included",
    "roadsideAssistance": "24/7 available",
    "fuelPolicy": "Full-to-full",
    "importantPoints": [
      "Minimum rental period: 24 hours",
      "Maximum mileage: 300 km/day (extra charges apply beyond this)",
      "Driver must be 21+ with valid license",
      "No smoking policy",
      "Toll charges to be paid by renter"
    ]
  };

  return (
    <div className="w-full min-h-screen bg-gray-50 dark:bg-gray-900 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-6">{car.name}</h1>

        <div className="flex flex-col lg:flex-row gap-8">
          {/* Left Section (3/5 width) */}
          <div className="lg:w-3/5">
            <CarImageGallery photos={car.photos} />
            <CarSpecifications car={car} />
          </div>

          {/* Right Section (2/5 width) */}
          <div className="lg:w-2/5 space-y-6">
            <BookingDetails
              pricePerDay={car.rentalPricePerDay}
              carType={car.carType}
              location={car.location}
            />
            <PickupOptions />
            <BookingSummary
              basePrice={car.rentalPricePerDay}
              luggageCapacity={car.luggageCapacity}
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default page
