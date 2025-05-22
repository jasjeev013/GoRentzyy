"use client"
import React, { useEffect, useState } from 'react'
import CarImageGallery from '../../components/carPageComponents/CarImageGallery';
import CarSpecifications from '../../components/carPageComponents/CarSpecifications';
import BookingDetails from '../../components/carPageComponents/BookingDetails';
import PickupOptions from '../../components/carPageComponents/PickupOptions';
import BookingSummary from '../../components/carPageComponents/BookingSummary';
import { useCarStore } from '../../../stores/carStore';
import { Loader2 } from 'lucide-react';
import { useParams } from 'next/navigation';
import RentalPaymentConfirmation from '../../components/carPageComponents/RentalPaymentConfirmation';
import { useBookingStore } from '../../../stores/bookingStore';

const page = () => {
  const { id } = useParams();
  const { createBooking } = useBookingStore();
  const [bookingPeriod, setBookingPeriod] = useState<{
    start: Date | null;
    end: Date | null;
  }>({
    start: null,
    end: null,
  });

  const [showConfirmation, setShowConfirmation] = useState(false);



  const { currentCar, fetchCarById, loading, error } = useCarStore();
  useEffect(() => {
    console.log(currentCar)
    if (id) {
      fetchCarById(Number(id));
    }
  }, [id, fetchCarById]);

  const doBookingCar = async (total: number,) => {
    await createBooking(Number(id), {
      startDate: bookingPeriod.start?.toISOString().split('Z')[0],
      endDate: bookingPeriod.end?.toISOString().split('Z')[0],
      totalPrice: total,
      status: 'CONFIRMED',
    });

    setShowConfirmation(true);

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
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-6">
          {currentCar.name}
        </h1>

        <div className="flex flex-col lg:flex-row gap-8">
          {/* Left Section (3/5 width) */}
          <div className="lg:w-3/5">
            {currentCar.photos && <CarImageGallery photos={currentCar.photos} />}
            <CarSpecifications car={currentCar} />
          </div>

          {/* Right Section (2/5 width) */}
          <div className="lg:w-2/5 space-y-6">
            <BookingDetails
              pricePerDay={currentCar.rentalPricePerDay}
              carType={currentCar.carType}
              location={''}
              onDateTimeChange={(start: Date, end: Date) => setBookingPeriod({ start, end })}

            />
            <PickupOptions />
            <BookingSummary
              basePrice={currentCar.rentalPricePerDay}
              luggageCapacity={''}
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
