import Image from 'next/image'
import React from 'react'
import { BackgroundGradient } from '../../components/ui/background-gradient'

const ThirdSectionCard = () => {
    return (
        <>
            <div className="text-center mt-30">
                <h1 className="text-6xl font-bold mb-4">With Us, Car Rentals Are Easy</h1>
                <h4 className="text-lg text-white-600 mb-8">Seamless car sharing for hosts and renters, powerful solutions for modern mobility needs.</h4>
                <div className="flex flex-wrap justify-center gap-13 mt-12">
                    {/* Card 1 */}
                    <div className="flex-1 min-w-[300px] max-w-[350px]">
                        <BackgroundGradient animate={true} className="rounded-[22px] p-4 sm:p-5 bg-zinc-900">
                            <Image
                                src="/cardImages/host.png"
                                alt="Car Hosting"
                                height="400"
                                width="400"
                                className="object-contain rounded-[22px]"
                            />
                            <p className="text-base sm:text-xl  mt-4 mb-2 text-neutral-200">
                                For Car Owners
                            </p>
                            <p className="text-sm  dark:text-neutral-400">
                                Turn your idle car into a source of income. List your vehicle, set your terms, and let renters find you effortlessly.
                            </p>

                        </BackgroundGradient>
                    </div>

                    {/* Card 2 */}
                    <div className="flex-1 min-w-[300px] max-w-[350px]">
                        <BackgroundGradient className="rounded-[22px] p-4 sm:p-5 bg-zinc-900">
                            <Image
                                src="/cardImages/renter.png"
                                alt="Car Renting"
                                height="400"
                                width="400"
                                className="object-contain rounded-[22px]"
                            />
                            <p className="text-base sm:text-xl  mt-4 mb-2 text-neutral-200">
                                For Renters
                            </p>
                            <p className="text-sm  dark:text-neutral-400">
                                Discover a wide range of vehicles near you. Whether it’s a daily commute or a weekend getaway, we’ve got you covered.
                            </p>

                        </BackgroundGradient>
                    </div>

                    {/* Card 3 */}
                    <div className="flex-1 min-w-[300px] max-w-[350px]">
                        <BackgroundGradient className="rounded-[22px] p-4 sm:p-5  bg-zinc-900">
                            <Image
                                src="/cardImages/everyone.png"
                                alt="Trusted Platform"
                                height="400"
                                width="400"
                                className="object-contain rounded-[22px]"
                            />
                            <p className="text-base sm:text-xl  mt-4 mb-2 text-neutral-200">
                                For Everyone
                            </p>
                            <p className="text-sm  dark:text-neutral-400">
                                Our platform ensures safe transactions, verified users, and 24/7 support, making car sharing simple and reliable.
                            </p>

                        </BackgroundGradient>
                    </div>
                </div>
            </div>
        </>
    )
}

export default ThirdSectionCard
