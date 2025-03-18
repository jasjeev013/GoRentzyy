import React from 'react'

const FirstSectionLeftPart = () => {
    return (
        <>
            <div className="w-2/5 p-4 flex flex-col justify-center animate-slide-in-left ">
                <div className="justify-start">
                
                    <h3 className="text-3xl font-semibold text-white font-sans">RENT & RIDE</h3>

             
                    <h2 className="text-5xl font-medium text-white font-sans">WITH YOUR</h2>

         
                    <h1 className="text-7xl font-bold text-[#10A0A0] mt-7 font-serif">FRIENDS</h1>

       
                    <div className="flex items-center mt-8 border-2 border-black rounded-xl shadow-2xl">
    
                        <input
                            type="text"
                            placeholder="Enter your email"
                            className="w-3/4 h-14 px-4 py-2 border-gray-700 bg-[#282828] text-white rounded-l-xl focus:outline-none transition-all duration-300"
                        />

          
                        <button
                            className="w-1/4 h-14 px-2 bg-[#282828] text-white font-semibold rounded-r-xl hover:bg-[#383838] focus:outline-none transition-all duration-300"
                        >
                            Search Now
                        </button>
                    </div>
                </div>
            </div>
        </>
    )
}

export default FirstSectionLeftPart
