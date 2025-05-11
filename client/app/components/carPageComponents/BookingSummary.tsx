'use client';
import { useState } from 'react';
import { Checkbox } from '../../../components/ui/checkbox';
import { Button } from '../../../components/ui/button';
import RentalPaymentConfirmation from './RentalPaymentConfirmation';

interface BookingSummaryProps {
  basePrice: number;
  luggageCapacity: string;
}

const BookingSummary = ({ basePrice, luggageCapacity }: BookingSummaryProps) => {
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [includeLuggage, setIncludeLuggage] = useState(false);
  const [includeProtection, setIncludeProtection] = useState(false);
  const [agreeTerms, setAgreeTerms] = useState(false);

  const gst = basePrice * 0.18;
  const deposit = basePrice * 2;
  const luggageFee = includeLuggage ? 500 : 0;
  const protectionFee = includeProtection ? 300 : 0;

  const subtotal = basePrice + luggageFee + protectionFee;
  const total = subtotal + gst;

  return (
    <div className="bg-[#DDC9C9] dark:bg-[#252A27CC] rounded-lg shadow-md p-6">
      <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">Booking Summary</h2>

      <div className="space-y-3 mb-4">
        <div className="flex justify-between">
          <span className="text-gray-600 dark:text-gray-300">Rental Charges</span>
          <span>₹{basePrice}</span>
        </div>

        <div className="flex justify-between">
          <span className="text-gray-600 dark:text-gray-300">GST (18%)</span>
          <span>₹{gst}</span>
        </div>

        <div className="flex justify-between">
          <span className="text-gray-600 dark:text-gray-300">Refundable Deposit</span>
          <span>₹{deposit}</span>
        </div>

        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Checkbox
              id="luggage"
              checked={includeLuggage}
              onCheckedChange={(checked) => setIncludeLuggage(!!checked)}
            />
            <label htmlFor="luggage" className="text-sm font-medium leading-none">
              Luggage Carrier ({luggageCapacity} bags)
            </label>
          </div>
          <span>₹500.00</span>
        </div>

        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Checkbox
              id="protection"
              checked={includeProtection}
              onCheckedChange={(checked) => setIncludeProtection(!!checked)}
            />
            <label htmlFor="protection" className="text-sm font-medium leading-none">
              Trip Protection Plan
            </label>
          </div>
          <span>₹300.00</span>
        </div>
      </div>

      <div className="mb-4">
        <label htmlFor="promo" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
          Promo Code
        </label>
        <div className="flex">
          <input
            type="text"
            id="promo"
            className="flex-1 border rounded-l-md px-3 py-2"
            placeholder="Enter promo code"
          />
          <button className="bg-blue-600 text-white px-4 py-2 rounded-r-md hover:bg-blue-700">
            Apply
          </button>
        </div>
      </div>

      <div className="flex items-center space-x-2 mb-6">
        <Checkbox
          id="terms"
          checked={agreeTerms}
          onCheckedChange={(checked) => setAgreeTerms(!!checked)}
        />
        <label htmlFor="terms" className="text-sm font-medium leading-none">
          I agree to the <a href="#" className="text-blue-600 hover:underline">Terms & Conditions</a>
        </label>
      </div>

      <div className="border-t pt-4">
        <div className="flex justify-between font-semibold text-lg">
          <span>Total Amount</span>
          <span>₹{total}</span>
        </div>
      </div>

      <Button
        className="w-full mt-6 bg-blue-600 hover:bg-blue-700"
        disabled={!agreeTerms}
        onClick={() => setShowConfirmation(true)}
      >
        Proceed to Payment
      </Button>

      {showConfirmation && (
        <RentalPaymentConfirmation onClose={() => setShowConfirmation(false)} />
      )}
    </div>
  );
};

export default BookingSummary;