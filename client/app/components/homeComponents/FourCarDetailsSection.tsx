'use client';
import React from 'react';
import { motion } from 'framer-motion';
import Image from 'next/image';

const FourCarDetailsSection = () => {
    const stats = [
        { number: "145+", label: "countries", image: '/carDetails/globe.png', width: 56, height: 48 },
        { number: "10k+", label: "locations", image: '/carDetails/buildings.png', width: 56, height: 48 },
        { number: "700+", label: "partners", image: '/carDetails/car.png', width: 72, height: 48 },
        { number: "31", label: "languages", image: '/carDetails/language.png', width: 52, height: 40 }
    ];

    const container = {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: {
                staggerChildren: 0.2,
                delayChildren: 0.3
            }
        }
    };

    const item = {
        hidden: { y: 20, opacity: 0 },
        visible: {
            y: 0,
            opacity: 1,
            transition: {
                duration: 0.5,
                ease: "easeOut"
            }
        }
    };

    return (
        <section className="w-full py-20 px-4 sm:px-6 lg:px-8 ">
            <div className="max-w-7xl mx-auto">
                <motion.div 
                    className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8"
                    variants={container}
                    initial="hidden"
                    whileInView="visible"
                    viewport={{ once: true, margin: "-100px" }}
                >
                    {stats.map((stat, index) => (
                        <motion.div 
                            key={index}
                            variants={item}
                            className="group flex flex-col items-center justify-center p-8 bg-gray-900 rounded-xl shadow-sm hover:shadow-lg transition-all duration-300 hover:-translate-y-2"
                        >
                            <div className="relative w-14 h-14 mb-6 flex items-center justify-center">
                                <Image
                                    src={stat.image}
                                    alt={stat.label}
                                    width={stat.width}
                                    height={stat.height}
                                    className="object-contain group-hover:scale-110 transition-transform duration-300"
                                />
                                <div className="absolute inset-0 bg-blue-100 dark:bg-blue-900/30 rounded-full opacity-0 group-hover:opacity-100 blur-md transition-opacity duration-300 -z-10"></div>
                            </div>
                            <div className="text-center">
                                <p className="text-4xl font-bold text-blue-600 dark:text-blue-400 mb-2">
                                    {stat.number}
                                </p>
                                <p className="text-lg font-medium text-black-900/30 capitalize">
                                    {stat.label}
                                </p>
                            </div>
                        </motion.div>
                    ))}
                </motion.div>
            </div>
        </section>
    )
}

export default FourCarDetailsSection;