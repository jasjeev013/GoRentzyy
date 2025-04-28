import React from 'react'
import MainSection from '../components/rentComponents/MainSection'
import SearchBarSection from '../components/rentComponents/SearchBarSection'

const page = () => {
    return (
        <>
            <div className="w-full min-h-screen flex items-center justify-center">
                <div className="w-[calc(100%-18rem)] min-h-screen  ">
                    <SearchBarSection />
                    <MainSection />

                    {/* luggague capacity
                    distance User Rating addons */}
                </div>
            </div>
        </>
    )
}


export default page
