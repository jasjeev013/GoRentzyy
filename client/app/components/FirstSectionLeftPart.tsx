import React from 'react'

const FirstSectionLeftPart = () => {
    return (
        <>
            <div className="w-2/5  p-4 flex flex-col justify-center">
                <div className="justify-start">
                    {/* Small Text */}
                    <h3 className="text-3xl font-semibold text-white-700 font-sans">RENT & RIDE</h3>

                    {/* Medium Text */}
                    <h2 className="text-5xl font-medium text-white-800 font-sans">WITH YOUR</h2>

                    {/* Large Text */}
                    <h1 className="text-7xl font-bold text-[#173A3A]-900 mt-7 font-serif">FRIENDS</h1>

                    {/* Input and Button Container */}
                    <div className="flex items-center mt-8">
                        {/* Input Box */}

                        <input
                            type="text"
                            placeholder="Enter your email"
                            className="w-3/4 h-14 px-4 py-2 border border-gray-700 bg-[#282828] text-white rounded-l-xl focus:outline-none focus:ring-2 focus:ring-black-500 transition-all duration-300"
                        />
                        <button
                            className=" w-1/4 h-14 px-2 bg-[#282828] text-white font-semibold rounded-r-xl hover:bg-[#383838] focus:outline-none focus:ring-2 focus:ring-black-500 transition-all duration-300"
                        >
                            Search Now
                        </button>
                        {/* Button */}


                    </div>
                </div>
            </div>
        </>
    )
}

export default FirstSectionLeftPart
