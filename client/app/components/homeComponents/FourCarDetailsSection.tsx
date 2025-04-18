import React from 'react'

const FourCarDetailsSection = () => {
    const stats = [
        { number: "145+", label: "countries", image: '/carDetails/globe.png', width: 14, height: 12 },
        { number: "10k+", label: "locations", image: '/carDetails/buildings.png', width: 14, height: 12 },
        { number: "700+", label: "partners", image: '/carDetails/car.png', width: 18, height: 12 },
        { number: "31", label: "languages", image: '/carDetails/language.png', width: 13, height: 10 }
    ];
    return (
        <>
            <div className="w-full bg-black dark:bg-[#252A27CC]  py-20 px-4 sm:px-6 lg:px-8 mt-20">
                <div className="max-w-9xl justify-center align-center ">

                    {/* Stats Grid */}
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-13">
                        {stats.map((stat, index) => (
                            <div className='flex flex-row items-center justify-center' key={index}>
                                <img
                                    src={stat.image}
                                    alt={stat.label}
                                    className={`w-${stat.width} h-${stat.height} mx-4 mb-4`}
                                />
                                <div className="text-center md:text-left">
                                    <p className="text-4xl font-bold text-blue-600 dark:text-blue-400">
                                        {stat.number}
                                    </p>
                                    <p className="ml-1 mt-1 text-lg text-gray-600 dark:text-gray-300">
                                        {stat.label}
                                    </p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </>
    )
}

export default FourCarDetailsSection
