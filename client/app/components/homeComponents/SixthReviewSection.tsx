import React from 'react';
import Image from 'next/image';

const SixthReviewSection = () => {
    const features = [
        {
            heading: "4.0",
            image: "/customerReview/trustpilot.png",
            alt: "Trustpilot Rating",
            stars: 4.0
        },
        {
            heading: "4.2",
            image: "/customerReview/google.png",
            alt: "Google Rating",
            stars: 4.2
        },
        {
            heading: "4.8",
            image: "/customerReview/reviewCenter.png",
            alt: "Review Center Rating",
            stars: 4.2
        },
        {
            heading: "4.4",
            image: "/customerReview/reviewio.png",
            alt: "Review.io Rating",
            stars: 4.4
        }
    ];

    const renderStars = (rating) => {
        const stars = [];
        const fullStars = Math.floor(rating);
        const hasHalfStar = rating % 1 >= 0.5;

        for (let i = 0; i < fullStars; i++) {
            stars.push(<span key={`full-${i}`} className="text-yellow-400">★</span>);
        }

        if (hasHalfStar) {
            stars.push(<span key="half" className="text-yellow-400">½</span>);
        }

        const emptyStars = 5 - stars.length;
        for (let i = 0; i < emptyStars; i++) {
            stars.push(<span key={`empty-${i}`} className="text-gray-300">★</span>);
        }

        return stars;
    };

    return (
        <section className="w-full py-16 px-4 sm:px-6 lg:px-8 mt-10 ">
            <div className="max-w-7xl mx-auto">
                <div className="grid grid-cols-1 md:grid-cols-6 gap-8 items-center">
                    {/* First Column (Larger) */}
                    <div className="md:col-span-2 space-y-6">
                        <h2 className="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white leading-tight">
                            Trusted by Thousands of Happy Customers
                        </h2>
                        <p className="text-lg text-gray-600 dark:text-gray-300">
                            We provide the best car rental experience with premium vehicles and exceptional customer service.
                            Don't just take our word for it - see what our customers say.
                        </p>
                    </div>

                    {/* Review Cards */}
                    {features.map((feature, index) => (
                        <div
                            key={index}
                            className="flex flex-col items-center justify-center  space-y-4 rounded-xl border-3 border-white  bg-white shadow-sm hover:shadow-md transition-all duration-300 ease-in-out text-center h-[70%]"
                        >
                            <div className="text-4xl font-bold text-black dark:text-blue-400">
                                {feature.heading}
                            </div>
                            <div className="flex space-x-1">
                                {renderStars(feature.stars)}
                            </div>
                            <div className="w-full h-px bg-gray-100 dark:bg-gray-700"></div>
                            <div className="w-40 h-10 flex items-center justify-center">
                                <Image
                                    src={feature.image}
                                    alt={feature.alt}
                                    width={160}
                                    height={40}
                                    className="object-contain"
                                />
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </section>
    );
};

export default SixthReviewSection;