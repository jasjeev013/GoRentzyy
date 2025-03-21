
import * as React from "react"

import { Card, CardContent } from "../../components/ui/card"
import {
    Carousel,
    CarouselContent,
    CarouselItem,
    CarouselNext,
    CarouselPrevious,
} from "../../components/ui/carousel"

import FourthSectionDropdownSection from "./FourthSectionDropdownSection";
const arrOfCars = [
    {
        img: "/discoverCarImages/car1.png",
        heading: "Toyota Supra",
        subheading: "2nd Generation Toyota Celica Supra Model",
    },
    {
        img: "/discoverCarImages/car2.png",
        heading: "Toyota Corolla",
        subheading: "2nd Generation Toyota Celica Supra Model",
    },
    {
        img: "/discoverCarImages/car3.png",
        heading: "Toyota Prius",
        subheading: "2nd Generation Toyota Celica Supra Model",
    },
    {
        img: "/discoverCarImages/car4.png",
        heading: "Toyota Camry",
        subheading: "2nd Generation Toyota Celica Supra Model",
    },
]

const FourthFindCarSection = () => {

    
    return (
        <>
            <div className=" mt-38">
                {/* Centered Heading */}
                <h1 className="text-6xl font-bold text-center mt-12">
                    Discover Wherever You Want
                </h1>

                {/* Centered Subheading */}
                <p className="text-lg text-center mt-6 max-w-2xl mx-auto">
                    Explore a wide range of modern, well-maintained vehicles ready for your next adventure or commute.
                </p>

                {/* Two Dropdowns in One Row */}
                <FourthSectionDropdownSection/>

                {/* Carousel */}
                <div className="flex items-start justify-center mt-12">
                    <Carousel
                        opts={{
                            align: "start",
                        }}
                        className="w-full max-w-5xl"
                    >
                        <CarouselContent>
                            {arrOfCars.map(({ img, heading, subheading }, index) => (
                                <CarouselItem key={index} className="md:basis-1/2 lg:basis-1/3">
                                    <div className="px-1">
                                        <Card className="bg-[#252121]">
                                            <CardContent className="flex flex-col aspect-square px-2">
                                                {/* Image */}
                                                <img
                                                    src={img}
                                                    alt="Sample Image"
                                                    className="w-full h-56 object-cover rounded-lg bg-zinc-900"
                                                />

                                                {/* Heading */}
                                                <h3 className="text-2xl text-white font-semibold mt-4 text-left">
                                                    {heading}
                                                </h3>

                                                {/* Subheading */}
                                                <p className="text-sm text-white mt-2 text-left">
                                                    {subheading}
                                                </p>
                                            </CardContent>
                                        </Card>
                                    </div>
                                </CarouselItem>
                            ))}
                        </CarouselContent>
                        <CarouselPrevious />
                        <CarouselNext />
                    </Carousel>
                </div>
            </div>

        </>
    )
}

export default FourthFindCarSection
