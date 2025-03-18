import React from 'react'
import { Marquee } from '../../components/ui/marquee'
const Logos = {
  tailwindcss: () => (
    <img
      src="/companiesLogos/images.png" // Path to your image file
      alt="Vercel"
      className="h-[1rem] w-[50rem]" // Increased height and width
    />
  ),
  nextjs: () => (
    <img
      src="/companiesLogos/angellist.svg" // Path to your image file
      alt="Next.js"
      className="h-[1rem] w-[50rem]"
    />
  ),
  framer: () => (
    <img
      src="/companiesLogos/deel.svg" // Path to your image file
      alt="Framer Motion"
      className="h-[1rem] w-[50rem]"
    />
  ),
  aws: () => (
    <img
      src="/companiesLogos/lumistry.svg" // Path to your image file
      alt="AWS"
      className="h-[1rem] w-[50rem]"
    />
  ),
  aps: () => (
    <img
      src="/companiesLogos/mercury.svg" // Path to your image file
      alt="AMS"
      className="h-[1rem] w-[50rem]"
    />
  ),
};
const SecondInfiniteScrollSection = () => {
  const arr = [Logos.tailwindcss, Logos.framer, Logos.nextjs, Logos.aws, Logos.aps]



  return (
    <>
      <div className='bg-white'>
      <Marquee>
        {arr.map((Logo, index) => (
          <div
            key={index}
            className="relative h-full bg-black w-fit mx-[4rem] bg-blackflex items-center justify-start"
          >
            <Logo />
          </div>
        ))}
      </Marquee>
      </div>
    </>
  )
}

export default SecondInfiniteScrollSection
