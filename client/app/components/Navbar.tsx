import React from 'react'

const Navbar = () => {
    return (
        <>
            <div className="w-full shadow-lg shadow-black/50 flex items-center justify-between px-7 py-2 bg-[#252A27] rounded-lg">

                <img src="logo.png" alt="Logo" className="h-10 " />


                <div className="space-x-16  px-10 ">
                    <a href="/home" className="text-white hover:text-gray-400">Home</a>
                    <a href="/rent" className="text-white hover:text-gray-400">Rent a Car</a>
                    <a href="/services" className="text-white hover:text-gray-400">Services</a>
                    <a href="/works" className="text-white hover:text-gray-400">How it works</a>
                    <a href="/login" className="text-white hover:text-gray-400">Login</a>
                </div>

            </div>
        </>
    )
}

export default Navbar;
