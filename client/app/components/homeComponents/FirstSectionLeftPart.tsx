import React from 'react'

const FirstSectionLeftPart = () => {
    return (
        <>
            <div className="w-2/5 p-4 flex flex-col justify-center text-black dark:text-white animate-slide-in-left ">
                <div className="justify-start">

                    <h3 className="text-3xl font-semibold  font-sans">RENT & RIDE</h3>


                    <h2 className="text-5xl font-medium  font-sans">WITH YOUR</h2>


                    <h1 className="text-7xl font-bold text-[#2A2D2C] dark:text-[#10A0A0] mt-7 font-serif">FRIENDS</h1>


                    <div className="flex items-center mt-8 rounded-xl shadow-2xl ">
                        <div className="w-full bg-[#2A2433] rounded-lg p-4 space-y-4">
                            {/* Quick Search Title */}
                            <h3 className="text-left text-lg font-semibold">Quick Search</h3>

                            {/* Location Dropdown */}
                            <div className="w-full">
                                <select className="w-full p-2 rounded  dark:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500">
                                    <option value="">Select Location</option>
                                    <option value="new-york">New York</option>
                                    <option value="los-angeles">Los Angeles</option>
                                    <option value="chicago">Chicago</option>
                                </select>
                            </div>

                            {/* Date Range - Two boxes in one row */}
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium mb-1">Start Date</label>
                                    <input
                                        type="date"
                                        className="w-full p-2 rounded dark:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium mb-1">End Date</label>
                                    <input
                                        type="date"
                                        className="w-full p-2 rounded dark:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    />
                                </div>
                            </div>

                            {/* Submit Button */}
                            <button className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded transition duration-200">
                                Search
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}

export default FirstSectionLeftPart
