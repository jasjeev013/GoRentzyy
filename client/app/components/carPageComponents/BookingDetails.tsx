'use client';
import { BookingDetailsProps } from '../../../app/types';
import { useState } from 'react';

const BookingDetails = ({ pricePerDay, carType, location }: BookingDetailsProps) => {
  const [startDate, setStartDate] = useState<string>(() => {
    const date = new Date();
    return date.toISOString().split('T')[0];
  });

  const [endDate, setEndDate] = useState<string>(() => {
    const date = new Date();
    date.setDate(date.getDate() + 3);
    return date.toISOString().split('T')[0];
  });

  const [startTime, setStartTime] = useState<string>('10');
  const [endTime, setEndTime] = useState<string>('10');

  const calculateDuration = () => {
    if (!startDate || !endDate) return 'Select dates';

    const start = new Date(`${startDate}T${startTime.padStart(2, '0')}:00:00`);
    const end = new Date(`${endDate}T${endTime.padStart(2, '0')}:00:00`);

    const diffTime = Math.abs(end.getTime() - start.getTime());
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
    const diffHours = Math.floor((diffTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));

    return `${diffDays} days ${diffHours} hours`;
  };

  return (
    <div className="bg-[#DDC9C9] dark:bg-[#252A27CC] rounded-lg shadow-md p-6">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold text-gray-900 dark:text-white">₹{pricePerDay.toFixed(2)}/day</h2>
        <span className="px-3 py-1 bg-blue-100 dark:bg-blue-900 text-blue-800 dark:text-blue-200 rounded-full text-sm">
          {carType}
        </span>
      </div>

      <p className="text-gray-600 dark:text-gray-300 mb-4">
        <span className="font-medium">Location:</span> {location}
      </p>

      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Start Date & Time
          </label>
          <div className="flex gap-2">
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              min={new Date().toISOString().split('T')[0]}
              className="border rounded-md px-3 py-2 w-full"
            />
            <select
              value={startTime}
              onChange={(e) => setStartTime(e.target.value)}
              className="border rounded-md px-3 py-2 w-full "
            >
              {Array.from({ length: 24 }).map((_, i) => (
                <option key={i} value={i}>{i.toString().padStart(2, '0')}:00</option>
              ))}
            </select>
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            End Date & Time
          </label>
          <div className="flex gap-2">
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              min={startDate}
              className="border rounded-md px-3 py-2 w-full"
            />
            <select
              value={endTime}
              onChange={(e) => setEndTime(e.target.value)}
              className="border rounded-md px-3 py-2 w-full"
            >
              {Array.from({ length: 24 }).map((_, i) => (
                <option key={i} value={i}>{i.toString().padStart(2, '0')}:00</option>
              ))}
            </select>
          </div>
        </div>

        <p className="text-sm text-gray-600 dark:text-gray-400">
          <span className="font-medium">Duration:</span> {calculateDuration()}
        </p>

        <p className="text-sm text-gray-600 dark:text-gray-400">
          <span className="font-medium">Extra Charges:</span> ₹500/day beyond 300km
        </p>
      </div>
    </div>
  );
};

export default BookingDetails;