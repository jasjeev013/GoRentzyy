'use client';
import React from 'react'
import { FaFacebook, FaTwitter, FaInstagram, FaLinkedin, FaPaperPlane, FaArrowUp } from 'react-icons/fa';
import Link from 'next/link';

const EightFooterSection = () => {
    const scrollToTop = () => {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    };

    return (
        <footer className=" text-white w-full ">
            <div className="container mx-auto px-4 ">
                <div className="pt-12 border-t border-gray-700">
                    <div className="flex flex-col md:flex-row justify-between gap-8">
                        {/* Brand Section (Left) */}
                        <div className="flex-1">
                            <div className="flex items-center mb-4">
                                <div className="bg-white p-2 rounded-full mr-3">
                                    {/* Placeholder for car icon - replace with actual Image component if you have a logo */}
                                    <svg
                                        xmlns="http://www.w3.org/2000/svg"
                                        className="h-8 w-8 text-black"
                                        fill="none"
                                        viewBox="0 0 24 24"
                                        stroke="currentColor"
                                    >
                                        <path
                                            strokeLinecap="round"
                                            strokeLinejoin="round"
                                            strokeWidth={2}
                                            d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"
                                        />
                                    </svg>
                                </div>
                                <h2 className="text-2xl font-bold">GoRentzyy</h2>
                            </div>
                            <p className="mb-4 text-gray-300">
                                GoRentzyy offers premium vehicle rental services with a wide selection of cars for every need and budget.
                            </p>
                            <div className="flex space-x-3">
                                <a href="#" className=" bg-opacity-10 hover:bg-opacity-20 rounded-full p-2 transition-all duration-300">
                                    <FaFacebook className="h-5 w-5" />
                                </a>
                                <a href="#" className=" bg-opacity-10 hover:bg-opacity-20 rounded-full p-2 transition-all duration-300">
                                    <FaTwitter className="h-5 w-5" />
                                </a>
                                <a href="#" className="e bg-opacity-10 hover:bg-opacity-20 rounded-full p-2 transition-all duration-300">
                                    <FaInstagram className="h-5 w-5" />
                                </a>
                                <a href="#" className=" bg-opacity-10 hover:bg-opacity-20 rounded-full p-2 transition-all duration-300">
                                    <FaLinkedin className="h-5 w-5" />
                                </a>
                            </div>
                        </div>

                        {/* Center Section */}
                        <div className="flex-1 md:text-center">
                            <h3 className="text-lg font-semibold mb-4">Useful Links</h3>
                            <ul className="space-y-2">
                                <li>
                                    <Link href="/privacy-policy" className="text-gray-300 hover:text-white transition-colors duration-300">
                                        Privacy Policy
                                    </Link>
                                </li>
                            </ul>
                        </div>

                        {/* Right Section */}
                        <div className="flex-1">
                            <h3 className="text-lg font-semibold mb-4">Subscribe</h3>
                            <p className="text-gray-300 mb-4">
                                Stay connected with us for regular updates by providing your email address
                            </p>
                            <form className="flex flex-col sm:flex-row gap-2">
                                <input
                                    type="email"
                                    placeholder="Email Address"
                                    className="px-4 py-2 rounded-full text-white bg-transparent border-1 border-teal-300 ring-1 ring-teal-300 focus:outline-none flex-grow"
                                />
                                <button
                                    type="submit"
                                    className="bg-teal-600 hover:bg-teal-700 px-4 py-2 rounded-full flex items-center justify-center gap-2 transition-colors duration-300"
                                >
                                    <span>Send</span>
                                    <FaPaperPlane />
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
                {/* Bottom Bar */}

                <p className="text-center text-gray-400 py-8">
                    Copyright © 2025, All Right Reserved GoRentzyy
                </p>

            </div>

            {/* Back to Top Button */}
            <button
                onClick={scrollToTop}
                className="fixed bottom-8 right-6 hover:bg-teal-700 text-white p-3 rounded-full shadow-lg flex items-center gap-1 transition-all duration-300"
            >
                <FaArrowUp />

            </button>
        </footer>
    );
}

export default EightFooterSection
