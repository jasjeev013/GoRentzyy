"use client"
import React from 'react'
import MainSection from '../components/rentComponents/MainSection'
import SearchBarSection from '../components/rentComponents/SearchBarSection'
import { useSearchParams } from 'next/navigation';

const page = () => {

    const searchParams = useSearchParams();

    const city = searchParams.get('city');
    const startDate = searchParams.get('startDate');
    const endDate = searchParams.get('endDate');
    return (
        <>
            <div className="w-full min-h-screen  flex items-center justify-center">
                <div className="w-[calc(100%-18rem)] min-h-screen  ">
                    <SearchBarSection cityQuery={city} startDateQuery={startDate} endDateQuery = {endDate} />
                    <MainSection />

                    {/* luggague capacity
                    distance User Rating addons */}
                </div>
            </div>
        </>
    )
}


export default page
