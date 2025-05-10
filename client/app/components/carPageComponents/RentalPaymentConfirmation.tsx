"use client";
import React from 'react';
import { CheckCircle2, CalendarDays, MapPin, ChevronRight, X } from 'lucide-react';
import { Button } from '../../../components/ui/button';
import { Card, CardHeader, CardTitle, CardContent } from '../../../components/ui/card';
import { Separator } from '../../../components/ui/separator';

const RentalPaymentConfirmation = ({ onClose }: { onClose: () => void }) => {
  // Sample data - replace with actual props or API data
  const bookingDetails = {
    reservationNumber: 'RES-2023-45678',
    confirmationCode: 'GOCAR2023XYZ',
    rentalDays: 5,
    pickup: {
      date: '2023-12-15',
      time: '10:00 AM',
      location: 'GoRentzyy Downtown Hub, 123 Main St'
    },
    dropoff: {
      date: '2023-12-20',
      time: '04:00 PM',
      location: 'GoRentzyy Airport Hub, Terminal 1'
    },
    payment: {
      subtotal: 35000,
      discount: 3000,
      total: 32000
    }
  };

  return (
    <div className="fixed inset-0 backdrop-blur-sm z-50 flex items-center justify-center p-4">
      <Card className="w-full max-w-4xl bg-white dark:bg-gray-800 rounded-xl shadow-lg">
        <CardHeader className="pb-4">
          <div className="flex justify-between items-start">
            <div>
              <h2 className="text-2xl font-bold text-gray-900 dark:text-white">GoRentzyy</h2>
            </div>
            <Button 
              variant="ghost" 
              size="icon" 
              onClick={onClose}
              className="text-gray-500 hover:text-gray-700 dark:hover:text-gray-300"
            >
              <X className="h-5 w-5" />
            </Button>
          </div>
        </CardHeader>

        {/* Success Message Section */}
        <div className="px-6 pb-6">
          <div className="flex flex-col items-center text-center py-2">
            <CheckCircle2 className="h-10 w-10 text-green-500 mb-2" />
            <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-1">Payment Confirmed</h3>
            <p className="text-sm text-gray-600 dark:text-gray-400">Thank you for Choosing GoRentzyy!</p>
          </div>

          {/* Rental Timeline */}
          <div className="relative mb-4">
            <div className="absolute left-5 top-0 h-full w-0.5 bg-gray-200 dark:bg-gray-700"></div>
            
            {/* Pick-Up */}
            <div className="relative pl-8 mb-4">
              <div className="absolute left-4 top-1 h-2.5 w-2.5 rounded-full bg-blue-500 border-4 border-blue-100 dark:border-gray-800"></div>
              <div className="flex items-center gap-2 text-gray-500 dark:text-gray-400">
                <CalendarDays className="h-3.5 w-3.5" />
                <span className="text-xs">Pick-Up</span>
              </div>
              <h4 className="text-base font-semibold text-gray-900 dark:text-white">
                {bookingDetails.pickup.date} • {bookingDetails.pickup.time}
              </h4>
              <p className="flex items-center gap-1 text-sm text-gray-600 dark:text-gray-400">
                <MapPin className="h-3.5 w-3.5" />
                {bookingDetails.pickup.location}
              </p>
            </div>
            
            {/* Drop-Off */}
            <div className="relative pl-8">
              <div className="absolute left-4 top-1 h-2.5 w-2.5 rounded-full bg-blue-500 border-4 border-blue-100 dark:border-gray-800"></div>
              <div className="flex items-center gap-2 text-gray-500 dark:text-gray-400">
                <CalendarDays className="h-3.5 w-3.5" />
                <span className="text-xs">Drop-Off</span>
              </div>
              <h4 className="text-base font-semibold text-gray-900 dark:text-white">
                {bookingDetails.dropoff.date} • {bookingDetails.dropoff.time}
              </h4>
              <p className="flex items-center gap-1 text-sm text-gray-600 dark:text-gray-400">
                <MapPin className="h-3.5 w-3.5" />
                {bookingDetails.dropoff.location}
              </p>
            </div>
          </div>

          {/* Rental Summary Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mb-4">
            {/* Booking Details Card */}
            <Card className="border-gray-200 dark:border-gray-700">
              <CardHeader className="pb-1">
                <CardTitle className="text-base">Booking Details</CardTitle>
              </CardHeader>
              <CardContent className="py-1">
                <div className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600 dark:text-gray-400">Reservation No.</span>
                    <span className="font-medium">{bookingDetails.reservationNumber}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600 dark:text-gray-400">Confirmation Code</span>
                    <span className="font-medium">{bookingDetails.confirmationCode}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600 dark:text-gray-400">Rental Days</span>
                    <span className="font-medium">{bookingDetails.rentalDays} days</span>
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* Payment Summary Card */}
            <Card className="border-gray-200 dark:border-gray-700">
              <CardHeader className="pb-1">
                <CardTitle className="text-base">Payment Summary</CardTitle>
              </CardHeader>
              <CardContent className="py-1">
                <div className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600 dark:text-gray-400">Subtotal</span>
                    <span>₹{bookingDetails.payment.subtotal.toLocaleString()}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600 dark:text-gray-400">Discount</span>
                    <span className="text-green-500">-₹{bookingDetails.payment.discount.toLocaleString()}</span>
                  </div>
                  <Separator className="my-1" />
                  <div className="flex justify-between text-sm">
                    <span className="font-semibold">Total Amount</span>
                    <span className="font-bold">₹{bookingDetails.payment.total.toLocaleString()}</span>
                  </div>
                  <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                    Overall price includes rental discount.
                  </p>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Action Button */}
          <div className="flex justify-center pt-2">
            <Button 
              onClick={onClose}
              className="bg-blue-600 hover:bg-blue-700 px-6 py-1 text-sm"
            >
              Done
            </Button>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default RentalPaymentConfirmation;