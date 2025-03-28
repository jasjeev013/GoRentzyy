import React from 'react'
import Navbar from '../components/Navbar'
import NavbarWrapper from '../components/NavbarWrapper'
import FirstSection from '../components/FirstSection'
import SecondInfiniteScrollSection from '../components/SecondInfiniteScrollSection'
import ThirdSectionCard from '../components/ThirdSectionCard'
import FifthFindCarSection from '../components/FifthFindCarSection'
import FourCarDetailsSection from '../components/FourCarDetailsSection'
import SixthReviewSection from '../components/SixthReviewSection'
import SevenFAQSection from '../components/SevenFAQSection'
import EightFooterSection from '../components/EightFooterSection'


const page = () => {
  return (
    <>

      <div className="w-full min-h-screen flex items-center justify-center">
        <div className="w-[calc(100%-18rem)] min-h-screen border-l-[1px] border-r-[1px] border-white/30 ">
          {/* <div className="bg-black p-4 h-full"></div>
          <p className="text-white">This is a centered div with 3rem space on both sides and a white border on left and right with 10% transparency.</p> */}


          <NavbarWrapper />
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
