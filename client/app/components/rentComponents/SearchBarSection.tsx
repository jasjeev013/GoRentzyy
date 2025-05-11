"use client";
import React, { useState } from 'react'
import { FiMapPin } from 'react-icons/fi';

import { Button } from '../../../components/ui/button';
import { Input } from '../../../components/ui/input';
import { Loader2 } from 'lucide-react';
import { useRouter } from 'next/navigation';



const SearchBarSection = ({ cityQuery, startDateQuery, endDateQuery }) => {
    const router = useRouter();
    const [city, setCity] = useState(cityQuery);
    const [startDate, setStartDate] = useState(startDateQuery);
    const [endDate, setEndDate] = useState(endDateQuery);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async () => {
        setIsLoading(true);
        setError('');

        try {
            let query = '/rent';
            const params = new URLSearchParams();
            
            if (city) params.append('city', city);
            if (startDate) params.append('startDate', startDate);
            if (endDate) params.append('endDate', endDate);
            
            query += params.toString() ? `?${params.toString()}` : '';
            router.push(query);
            
        } catch (err) {
            setError('Failed to fetch cars. Please try again.');
            console.error('Search error:', err);
        } finally {
            setIsLoading(false);
        }
    };

    const handleClear = () => {
        setCity('');
        setStartDate('');
        setEndDate('');
        router.push('/rent');
    };

    return (
        <div className="text-white p-6">
            <div className="max-w-7xl mx-auto">
                <h1 className="text-2xl font-bold mb-6">Book Self-Drive Cars</h1>
                <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
                    <div className="relative">
                        <FiMapPin className="absolute left-3 top-3 text-purple-300" />
                        <Input
                            placeholder="Enter City"
                            value={city}
                            onChange={(e) => setCity(e.target.value)}
                            className="w-full pl-10 dark:bg-gray-700"
                        />
                    </div>

                    <div className="relative">
                        <Input
                            type="datetime-local"
                            placeholder="Start Date"
                            value={startDate}
                            onChange={(e) => setStartDate(e.target.value)}
                            className="w-full dark:bg-gray-700"
                        />
                    </div>

                    <div className="relative">
                        <Input
                            type="datetime-local"
                            placeholder="End Date"
                            value={endDate}
                            onChange={(e) => setEndDate(e.target.value)}
                            className="w-full dark:bg-gray-700"
                        />
                    </div>

                    <Button 
                        onClick={handleSubmit} 
                        disabled={isLoading}
                        className="bg-white text-purple-600 font-bold py-3 px-6 rounded-lg hover:bg-purple-100 transition"
                    >
                        {isLoading ? (
                            <Loader2 className="h-5 w-5 animate-spin" />
                        ) : (
                            'Modify Search'
                        )}
                    </Button>

                    <Button 
                        onClick={handleClear}
                        variant="outline"
                        className="text-white font-bold py-3 px-6 rounded-lg hover:bg-gray-600 transition"
                    >
                        Clear Filters
                    </Button>
                </div>
                {error && <p className="text-red-500 text-sm mt-2">{error}</p>}
            </div>
        </div>
    );
};

export default SearchBarSection;