import React from 'react'

const FirstSectionRightPart = () => {
    return (
        <>
            <div className="w-3/5 p-4 mx-5 flex justify-center relative animate-slide-in-right">
            <div className="relative h-[517px] w-[517px]">
                    {/* Car 4 */}
                    <img
                        src="/carImages/car4.png"
                        alt="Car 4"
                        width="384"
                        height="216"
                        className="absolute bottom-63 left-65 z-10 animate-slide-in-right-four "
                    />

                    {/* Car 3 */}
                    <img
                        src="/carImages/car3.png"
                        alt="Car 3"
                        width="430"
                        height="286"
                        className="absolute bottom-47 left-35 z-20 animate-slide-in-right-three"
                    />

                    {/* Car 2 */}
                    <img
                        src="/carImages/car2.png"
                        alt="Car 2"
                        width="517"
                        height="517"
                        className="absolute bottom-19 right-9 z-30 animate-slide-in-right-two"
                    />

                    {/* Car 1 */}
                    <img
                        src="/carImages/car1.png"
                        alt="Car 1"
                        width="496"
                        height="496"
                        className="absolute bottom-15 right-35 z-40 animate-slide-in-right-one"
                    />
                </div>
            </div>
        </>
    )
}

export default FirstSectionRightPart
