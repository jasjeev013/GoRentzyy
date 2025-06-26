'use client';
import { useBookingStore } from '../../../stores/bookingStore';
import { useEffect, useState } from 'react';
import { CheckCircle2, XCircle } from 'lucide-react';
import { Button } from '../../../components/ui/button';

interface RentalPaymentConfirmationProps {
  onClose: () => void;
  bookingId?: number;
}

const RentalPaymentConfirmation = ({ onClose, bookingId }: RentalPaymentConfirmationProps) => {
  const [paymentStatus, setPaymentStatus] = useState<'pending' | 'success' | 'failed'>('pending');
  const { renterBookings } = useBookingStore();

  // Find the current booking
  const currentBooking = bookingId 
    ? renterBookings.find(booking => booking.bookingId === bookingId)
    : renterBookings[renterBookings.length - 1];

  useEffect(() => {
    // You might want to poll the backend for payment status if needed
    // Or rely on the handler in initiateRazorpayPayment
    // For now, we'll assume success if the modal is shown
    setPaymentStatus('success');
  }, []);

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white dark:bg-gray-800 rounded-lg p-6 max-w-md w-full">
        {paymentStatus === 'pending' && (
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto mb-4"></div>
            <h3 className="text-lg font-medium mb-2">Processing Payment</h3>
            <p className="text-gray-600 dark:text-gray-300">
              Please wait while we process your payment...
            </p>
          </div>
        )}
        
        {paymentStatus === 'success' && currentBooking && (
          <div className="text-center">
            <CheckCircle2 className="h-12 w-12 text-green-500 mx-auto mb-4" />
            <h3 className="text-lg font-medium mb-2">Payment Successful!</h3>
            <p className="text-gray-600 dark:text-gray-300 mb-4">
              Your booking for {currentBooking.car.name} is confirmed.
            </p>
            <div className="grid grid-cols-2 gap-4">
              <Button variant="outline" onClick={onClose}>
                View Booking
              </Button>
              <Button onClick={onClose}>Done</Button>
            </div>
          </div>
        )}
        
        {paymentStatus === 'failed' && (
          <div className="text-center">
            <XCircle className="h-12 w-12 text-red-500 mx-auto mb-4" />
            <h3 className="text-lg font-medium mb-2">Payment Failed</h3>
            <p className="text-gray-600 dark:text-gray-300 mb-4">
              We couldn't process your payment. Please try again.
            </p>
            <div className="grid grid-cols-2 gap-4">
              <Button variant="outline" onClick={onClose}>
                Cancel
              </Button>
              <Button onClick={() => setPaymentStatus('pending')}>
                Try Again
              </Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default RentalPaymentConfirmation;