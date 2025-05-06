"use client";
import React from 'react'
import { FiMapPin } from 'react-icons/fi';
import DatePicker from './DatePicker';
import DropdownMenuC from './DropdownMenuC';
import { Button } from '../../../components/ui/button';
import { useCarStore } from '../../../stores/carStore';

const SearchBarSection = () => {
const { fetchCarsByCity } = useCarStore();
    const handleSubmit = () => {
        // Handle the submit action here
        console.log("Submit button clicked!");
        fetchCarsByCity("YOrk"); 
        
    }

    return (

        <>
            <div className=" text-white p-6">
                <div className="max-w-7xl mx-auto">
                    <h1 className="text-2xl font-bold mb-6">Book Self-Drive Cars</h1>
                    <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                        <div className="relative">
                            <FiMapPin className="absolute left-3 top-3 text-purple-300" />
                            
                            <DropdownMenuC />
                        </div>

                        <div className="relative">
                           
                            <DatePicker whichDate="start" />
                        </div>

                        <div className="relative">
                            <DatePicker whichDate="end" />
                        </div>

                        <Button onClick={handleSubmit} className="bg-white text-purple-600 font-bold py-3 px-6 rounded-lg hover:bg-purple-100 transition">
                            Modify Search
                        </Button>
                    </div>
                </div>
            </div>
        </>
    )
}

export default SearchBarSection
