import Image from 'next/image';
import React, { useEffect } from 'react'
import { useAuth } from '../../../hooks/useAuth';

const Navbar = ({ isScrolled }: { isScrolled: boolean }) => {
    const { isAuthenticated, role, userData } = useAuth();



    return (
        <>
            <div className={`flex  items-center justify-self-center justify-between text-black dark:text-white animate-slide-down sticky z-50 transition-all duration-300 ${isScrolled ? 'bg-[#C8FFE1] dark:bg-[#252A27CC] dark:backdrop-blur-sm rounded-lg shadow-xl py-2 top-5 px-7 w-[calc(100%-170px)] ' : 'py-4 mt-4 top-0 px-7 w-full'}`}>
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
                    <a href="/fleet" className=" hover:text-gray-400">Fleet</a>
                    {!isAuthenticated && <a href="/login" className=" hover:text-gray-400" >Login</a>}
                    {isAuthenticated && <a href={`/dashboard/${role === 'ROLE_RENTER' ? 'renter' : 'host'}/${userData.userId}`}
                        className=" hover:text-gray-400" >Dashboard</a>}
                </div>
            </div>
        </>
    )
}

export default Navbar;
