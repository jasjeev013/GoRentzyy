import React from 'react'

import FirstSection from '../components/homeComponents/FirstSection'
import SecondInfiniteScrollSection from '../components/homeComponents/SecondInfiniteScrollSection'
import ThirdSectionCard from '../components/homeComponents/ThirdSectionCard'
import FifthFindCarSection from '../components/homeComponents/FifthFindCarSection'
import FourCarDetailsSection from '../components/homeComponents/FourCarDetailsSection'
import SixthReviewSection from '../components/homeComponents/SixthReviewSection'
import SevenFAQSection from '../components/homeComponents/SevenFAQSection'
import EightFooterSection from '../components/homeComponents/EightFooterSection'


const page = () => {
  return (
    <>

      <div className="w-full min-h-screen flex items-center justify-center">
        <div className="w-[calc(100%-18rem)] min-h-screen  ">
          {/* <div className="bg-black p-4 h-full"></div>
          <p className="text-white">This is a centered div with 3rem space on both sides and a white border on left and right with 10% transparency.</p> */}


          <FirstSection />
          <SecondInfiniteScrollSection />
          <ThirdSectionCard />
          <FourCarDetailsSection />
          <FifthFindCarSection />
          <SixthReviewSection />
          <SevenFAQSection />
          <EightFooterSection />
        </div>
      </div>



    </>
  )
}

export default page
