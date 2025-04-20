"use client";
import React from 'react'
import { FiCalendar, FiClock, FiMapPin } from 'react-icons/fi';
import DatePicker from './DatePicker';
import DropdownMenuC from './DropdownMenuC';

const SearchBarSection = () => {

    return (


        <>
            <div className=" text-white p-6">
                <div className="max-w-7xl mx-auto">
                    <h1 className="text-2xl font-bold mb-6">Book Self-Drive Cars</h1>
                    <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                        <div className="relative">
                            <FiMapPin className="absolute left-3 top-3 text-purple-300" />
                            {/* <select className="w-full p-3 pl-10 rounded-lg bg-purple-700 text-white">
                                <option>Delhi</option>
                                <option>Mumbai</option>
                                <option>Bangalore</option>
                            </select> */}
                            <DropdownMenuC/>
                        </div>

                        <div className="relative">
                            {/* <FiCalendar className="absolute left-3 top-3 text-purple-300" /> */}
                            {/* <input
                                type="date"
                                className="w-full p-3 pl-10 rounded-lg bg-purple-700 text-white"
                                placeholder="Pick-Up Date"
                            /> */}
                             <DatePicker />
                        </div>

                        <div className="relative">
                            <FiClock className="absolute left-3 top-3 text-purple-300" />
                            <input
                                type="time"
                                className="w-full p-3 pl-10 rounded-lg bg-purple-700 text-white"
                                placeholder="Time"
                            />
                        </div>

                        <button className="bg-white text-purple-600 font-bold py-3 px-6 rounded-lg hover:bg-purple-100 transition">
                            Modify Search
                        </button>
                    </div>
                </div>
            </div>
        </>
    )
}

export default SearchBarSection
