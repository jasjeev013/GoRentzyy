import React from 'react'
import Navbar from '../components/Navbar'
import NavbarWrapper from '../components/NavbarWrapper'
import FirstSection from '../components/FirstSection'

const page = () => {
  return (
    <>
     {/* bg-[#252A27] rounded-lg shadow-lg */}
      <div className="w-full min-h-screen flex items-center justify-center">
        <div className="w-[calc(100%-18rem)] min-h-screen border-l-[1px] border-r-[1px] border-white/30 p-4">
          {/* <div className="bg-black p-4 h-full"></div>
          <p className="text-white">This is a centered div with 3rem space on both sides and a white border on left and right with 10% transparency.</p> */}

          <NavbarWrapper />
          <FirstSection/>


          
        </div>
      </div>



    </>
  )
}

export default page
