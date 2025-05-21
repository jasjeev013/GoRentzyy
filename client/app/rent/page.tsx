"use client"
import React, { useEffect } from 'react'
import MainSection from '../components/rentComponents/MainSection'
import SearchBarSection from '../components/rentComponents/SearchBarSection'
import { useSearchParams } from 'next/navigation';
import { useCarStore } from '../../stores/carStore';

const page = () => {

   const searchParams = useSearchParams();
    const { cars,fetchAllCars, fetchCarsByCity, fetchCarsByCityAndDate } = useCarStore();

    const city = searchParams.get('city') || '';
    const startDate = searchParams.get('startDate') || '';
    const endDate = searchParams.get('endDate') || '';

    useEffect(() => {
        const fetchCars = async () => {
            if (city) {
                if (startDate && endDate) {
                    await fetchCarsByCityAndDate(city, startDate, endDate);
                } else {
                    await fetchCarsByCity(city);
                }
            } else {
                console.log('Fetching all cars');
                await fetchAllCars();
          
            }
        };

        fetchCars();
    }, [city, startDate, endDate]);

    return (
        <div className="w-full min-h-screen flex items-center justify-center">
            <div className="w-[calc(100%-18rem)] min-h-screen">
                <SearchBarSection 
                    cityQuery={city} 
                    startDateQuery={startDate} 
                    endDateQuery={endDate} 
                />
                <MainSection />
            </div>
        </div>
    )
}


export default page
