import Image from 'next/image';
import React from 'react'

const Navbar = ({ isScrolled }: { isScrolled: boolean }) => {
    return (
        <>
            <div className={`flex items-center justify-between text-black dark:text-white animate-slide-down sticky z-50 transition-all duration-300 ${isScrolled ? 'bg-[#C8FFE1] dark:bg-[#252A27CC] dark:backdrop-blur-sm rounded-lg shadow-xl py-2 top-5 px-7 w-[calc(100%-40px)] mx-auto' : 'py-4 top-0 px-5 w-full'}`}>
                {/* <img src="logo.png" alt="Logo" className="h-10" /> */}
                <Image
                    src="/darkLogo.png"
                    alt="Dark Logo"
                    width={230}
                    height={50}
                    className="block dark:hidden h-10"
                />
                <Image
                    src="/logo.png"
                    alt="Light Logo"
                    width={230}
                    height={50}
                    className="hidden dark:block h-10"
                />
                <div className="space-x-16 px-10">
                    <a href="/home" className=" hover:text-gray-400">Home</a>
                    <a href="/rent" className=" hover:text-gray-400">Rent a Car</a>
                    <a href="/services" className=" hover:text-gray-400">Services</a>
                    <a href="/works" className=" hover:text-gray-400">How it works</a>
                    <a href="/login" className=" hover:text-gray-400">Login</a>
                </div>
            </div>
        </>
    )
}

export default Navbar;
