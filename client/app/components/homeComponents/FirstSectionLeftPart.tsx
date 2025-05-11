"use client"
import React, { useState } from 'react'
import { useRouter } from 'next/navigation';
import { Input } from '../../../components/ui/input';
import { Button } from '../../../components/ui/button';
import { Loader2 } from 'lucide-react';

const FirstSectionLeftPart = () => {
    const router = useRouter();
    const [city, setCity] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSearch = async (e: React.FormEvent) => {
        e.preventDefault();
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

    return (
        <div className="w-2/5 p-4 flex flex-col justify-center text-black dark:text-white animate-slide-in-left">
            <div className="justify-start">
                <h3 className="text-3xl font-semibold font-sans">RENT & RIDE</h3>
                <h2 className="text-5xl font-medium font-sans">WITH YOUR</h2>
                <h1 className="text-7xl font-bold text-[#2A2D2C] dark:text-[#10A0A0] mt-7 font-serif">FRIENDS</h1>

                <div className="flex items-center mt-8 rounded-xl shadow-2xl">
                    <div className="w-full bg-[#2A2433] rounded-lg p-4 space-y-4">
                        <h3 className="text-left text-lg font-semibold">Quick Search</h3>

                        <div className="w-full">
                            <Input
                                placeholder="Search City"
                                value={city}
                                onChange={(e) => setCity(e.target.value)}
                                className="w-full dark:bg-gray-600"
                            />
                        </div>

                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium mb-1">Start Date</label>
                                <Input
                                    type="datetime-local"
                                    value={startDate}
                                    onChange={(e) => setStartDate(e.target.value)}
                                    className="w-full dark:bg-gray-600"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium mb-1">End Date</label>
                                <Input
                                    type="datetime-local"
                                    value={endDate}
                                    onChange={(e) => setEndDate(e.target.value)}
                                    className="w-full dark:bg-gray-600"
                                />
                            </div>
                        </div>

                        {error && <p className="text-red-500 text-sm">{error}</p>}

                        <Button
                            onClick={handleSearch}
                            disabled={isLoading}
                            className="w-full bg-blue-600 hover:bg-blue-700"
                        >
                            {isLoading ? (
                                <>
                                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                    Searching...
                                </>
                            ) : (
                                'Search'
                            )}
                        </Button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default FirstSectionLeftPart;