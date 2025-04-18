import React from 'react'
import FirstSectionLeftPart from './FirstSectionLeftPart'
import FirstSectionRightPart from './FirstSectionRightPart'

const FirstSection = () => {
  return (
    <>
      <div className={`w-[calc(100%-3rem)] mx-6  h-150 flex mt-9 pt-15 py-5 bg-[#DDC9C9] dark:bg-[#252A27CC] rounded-lg`}>

        {/* Left Div */}
        <FirstSectionLeftPart />

        {/* Right Div */}
        <FirstSectionRightPart />
        
      </div>


    </>
  )
}

export default FirstSection
