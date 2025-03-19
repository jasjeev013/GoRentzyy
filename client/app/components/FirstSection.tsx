import React from 'react'
import FirstSectionLeftPart from './FirstSectionLeftPart'
import FirstSectionRightPart from './FirstSectionRightPart'
import { BackgroundBeamsWithCollision } from '../../components/ui/background-beams-with-collision'

const FirstSection = () => {
  return (
    <>
      <div className={`w-full  h-150 flex mt-9 pt-15 py-5 bg-[#252A27CC] rounded-lg`}>

        {/* Left Div */}
        <FirstSectionLeftPart />

        {/* Right Div */}
        <FirstSectionRightPart />
        
      </div>


    </>
  )
}

export default FirstSection
