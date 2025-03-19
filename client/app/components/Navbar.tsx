import React from 'react'

const Navbar = ({ isScrolled }: { isScrolled: boolean }) => {
    return (
        <>
            <div className={`flex items-center justify-between animate-slide-down sticky z-50 transition-all duration-300 ${isScrolled ? 'bg-[#252A27CC] backdrop-blur-sm rounded-lg shadow-xl py-2 top-5 px-7 w-[calc(100%-40px)] mx-auto' : 'py-1 top-0 px-1 w-full'}`}>
                <img src="logo.png" alt="Logo" className="h-10" />
                <div className="space-x-16 px-10">
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
