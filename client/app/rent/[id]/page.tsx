"use client"
import React, { useEffect, useState } from 'react'
import CarImageGallery from '../../components/carPageComponents/CarImageGallery';
import CarSpecifications from '../../components/carPageComponents/CarSpecifications';
import BookingDetails from '../../components/carPageComponents/BookingDetails';
import PickupOptions from '../../components/carPageComponents/PickupOptions';
import BookingSummary from '../../components/carPageComponents/BookingSummary';
import { useCarStore } from '../../../stores/carStore';
import { Loader2 } from 'lucide-react';
import { useParams, useRouter } from 'next/navigation';
import RentalPaymentConfirmation from '../../components/carPageComponents/RentalPaymentConfirmation';
import { useBookingStore } from '../../../stores/bookingStore';

import { useAuthStore } from '../../../stores/authStore';
import HostInfo from '../../components/rentComponents/HostInfo';
import ReviewsSection from '../../components/rentComponents/ReviewsSection';
import LocationMap from '../../components/rentComponents/LocationMap';

const page = () => {
  const { id } = useParams();
  const router = useRouter();
  const { createBooking } = useBookingStore();
  const { currentCar, fetchCarById, loading, error } = useCarStore();
  const { isAuthenticated } = useAuthStore();

  const [bookingPeriod, setBookingPeriod] = useState<{
    start: Date | null;
    end: Date | null;
  }>({
    start: null,
    end: null,
  });


  const [showConfirmation, setShowConfirmation] = useState(false);
  const [activeTab, setActiveTab] = useState('details');


  useEffect(() => {
    console.log(currentCar)
    if (id) {
      fetchCarById(Number(id));
    }
  }, [id, fetchCarById]);

  const doBookingCar = async (total: number,) => {
    const newBooking = await createBooking(Number(id), {
      startDate: bookingPeriod.start?.toISOString().split('Z')[0],
      endDate: bookingPeriod.end?.toISOString().split('Z')[0],
      totalPrice: total,
      status: 'CONFIRMED',
    });
    // setShowConfirmation(true);
    return newBooking
  };
  if (loading) {
    return (
      <div className="w-full min-h-screen flex items-center justify-center">
        <Loader2 className="h-12 w-12 animate-spin" />
      </div>
    );
  }
  if (error) {
    return (
      <div className="w-full min-h-screen flex items-center justify-center">
        <p className="text-red-500">{error}</p>
      </div>
    );
  }
  if (!currentCar) {
    return (
      <div className="w-full min-h-screen flex items-center justify-center">
        <p>Car not found</p>
      </div>
    );
  }
  return (
     <div className="w-full min-h-screen py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Left Section (3/5 width) */}
          <div className="lg:w-3/5">
            <div className="mb-8">
              <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
                {currentCar.name}
              </h1>
              <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400 mb-6">
                <span>{currentCar.location?.city}</span>
                <span>•</span>
                <span>{currentCar.year}</span>
                <span>•</span>
                <span>{currentCar.transmissionMode}</span>
                <span>•</span>
                <span>{currentCar.fuelType}</span>
              </div>
              
              <CarImageGallery photos={currentCar.photos} />
            </div>

            {/* Tabs */}
            <div className="border-b border-gray-200 dark:border-gray-700 mb-6">
              <nav className="flex space-x-8">
                <button
                  onClick={() => setActiveTab('details')}
                  className={`py-4 px-1 border-b-2 font-medium text-sm ${activeTab === 'details' ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'}`}
                >
                  Details
                </button>
                <button
                  onClick={() => setActiveTab('reviews')}
                  className={`py-4 px-1 border-b-2 font-medium text-sm ${activeTab === 'reviews' ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'}`}
                >
                  Reviews ({currentCar.reviews.length})
                </button>
                <button
                  onClick={() => setActiveTab('location')}
                  className={`py-4 px-1 border-b-2 font-medium text-sm ${activeTab === 'location' ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'}`}
                >
                  Location
                </button>
              </nav>
            </div>

            {/* Tab Content */}
            {activeTab === 'details' && (
              <>
                <CarSpecifications car={currentCar} />
                <HostInfo host={currentCar.host} />
              </>
            )}

            {activeTab === 'reviews' && (
              <ReviewsSection 
                reviews={currentCar.reviews} 
                carId={currentCar.carId} 
              />
            )}

            {activeTab === 'location' && currentCar.location && (
              <LocationMap 
                latitude={currentCar.location.latitude} 
                longitude={currentCar.location.longitude} 
                address={currentCar.location.address}
              />
            )}
          </div>

          {/* Right Section (2/5 width) */}
          <div className="lg:w-2/5 space-y-6">
            <BookingDetails
              pricePerDay={currentCar.rentalPricePerDay}
              carType={currentCar.carType}
              location={currentCar.location?.address || ''}
              onDateTimeChange={(start: Date, end: Date) => setBookingPeriod({ start, end })}
            />
            <PickupOptions />
            <BookingSummary
              basePrice={currentCar.rentalPricePerDay}
              luggageCapacity={currentCar.luggageCapacity}
              doBookingCar={doBookingCar}
            />
          </div>
        </div>

        {showConfirmation && (
          <RentalPaymentConfirmation onClose={() => setShowConfirmation(false)} />
        )}
      </div>
    </div>
  );
}
export default page
