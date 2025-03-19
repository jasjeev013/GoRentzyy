import React, { use } from 'react'
import { InfiniteMovingCards } from '../../components/ui/infinite-moving-cards';


const images = [
  { src: "/companiesLogos/vercel.png", alt: "vercel" },
  { src: "/companiesLogos/bun.png", alt: "bun" },
  { src: "/companiesLogos/redis.png", alt: "redis" },
  { src: "/companiesLogos/spring.png", alt: "spring" },
  { src: "/companiesLogos/acertinity.png", alt: "acertinity" },
  { src: "/companiesLogos/java.png", alt: "java" },
  { src: "/companiesLogos/next.png", alt: "next" },
  { src: "/companiesLogos/shadcn.png", alt: "shadcn" },
  { src: "/companiesLogos/bun.png", alt: "bun" },
  { src: "/companiesLogos/redis.png", alt: "redis" },
  { src: "/companiesLogos/spring.png", alt: "spring" },
  { src: "/companiesLogos/acertinity.png", alt: "acertinity" },
  { src: "/companiesLogos/java.png", alt: "java" },
  { src: "/companiesLogos/next.png", alt: "next" },
  { src: "/companiesLogos/shadcn.png", alt: "shadcn" },
];
const SecondInfiniteScrollSection = () => {

  return (
    <>
      <div className="mt-12 h-[6rem] rounded-md flex flex-col antialiased dark:bg-black dark:bg-grid-white/[0.05] items-center justify-center relative overflow-hidden border-t border-b border-white/1 animate-slide-in-four">
        <InfiniteMovingCards
          items={images}
          direction="left"
          speed="normal"
        />
      </div>
    </>
  )
}

export default SecondInfiniteScrollSection
