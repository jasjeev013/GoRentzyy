import React from 'react'
import FirstSectionLeftPart from './FirstSectionLeftPart'
import FirstSectionRightPart from './FirstSectionRightPart'

const FirstSection = () => {
  return (
    <>
     <div className={`w-full  h-120 flex mt-5 py-5 bg-[#1A1A1A]]-200`}>
      {/* Left Div */}
      
      <FirstSectionLeftPart/>
  

      {/* Right Div */}
      <FirstSectionRightPart/>
    </div>
    </>
  )
}

export default FirstSection
