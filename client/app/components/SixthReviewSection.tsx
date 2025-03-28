import React from 'react'

const SixthReviewSection = () => {

    const features = [
        {
            heading: "4.5",
            image: "/customerReview/trustpilot.png",
            alt: "24/7 Support"
        },
        {
            heading: "4.2",
            image: "/customerReview/google.png",
            alt: "Vehicle Fleet"
        },
        {
            heading: "4.7",
            image: "/customerReview/reviewCenter.png",
            alt: "Countries"
        },
        {
            heading: "4.4",
            image: "/customerReview/reviewio.png",
            alt: "Locations"
        }
    ];
    return (
        <>
            <div className="w-full py-12 px-4 sm:px-6 lg:px-8 mt-20  ">
                <div className="max-w-7xl ">
                    <div className="grid grid-cols-1 md:grid-cols-6 gap-12 items-center">
                        {/* First Column (Larger) */}
                        <div className="md:col-span-2 space-y-4">
                            <h2 className="text-md md:text-5xl font-bold text-gray-900 dark:text-white">
                                Customer Recommendation
                            </h2>
                            <p className="text-lg text-gray-600 dark:text-gray-300">
                                We provide the best car rental experience with premium vehicles and exceptional customer service.
                            </p>
                        </div>

                        {/* Feature Columns (4 equal columns) */}
                        {features.map((feature, index) => (
                            <div key={index} className="flex flex-col items-center md:items-center space-y-3 rounded-xl border border-white-300 h-40 bg-rounded-lg bg-zinc dark:bg-white align-center justify-center shadow-md hover:shadow-lg transition duration-300 ease-in-out  text-center">
                                <h3 className="text-5xl font-bold text-blue-600 dark:text-blue-400">
                                    {feature.heading}
                                </h3>
                                <div className="w-40 h-12 bg-white flex items-center justify-center">
                                    <img
                                        src={feature.image}
                                        alt={feature.alt}
                                        className="w-38 h-10 object-contain"
                                    />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </>
    )
}

export default SixthReviewSection
