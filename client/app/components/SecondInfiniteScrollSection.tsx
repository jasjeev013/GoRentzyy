'use client';
import React from 'react'
import { useEffect, useState } from 'react';
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
const darkImages = [
  { src: "/companiesLogos/darkVercel.png", alt: "darkVercel" },
  { src: "/companiesLogos/darkBun.png", alt: "darkBun" , width:'900px'},
  { src: "/companiesLogos/darkRedis.png", alt: "darkRedis" },
  { src: "/companiesLogos/spring.png", alt: "spring" },
  { src: "/companiesLogos/darkAcertinity.png", alt: "darkAcertinity", height:'300px' },
  { src: "/companiesLogos/java.png", alt: "java" },
  { src: "/companiesLogos/darkNext.png", alt: "darkNext" },
  { src: "/companiesLogos/darkShadcn.png", alt: "darkShadcn" },
  { src: "/companiesLogos/darkBun.png", alt: "darkBun" },
  { src: "/companiesLogos/darkRedis.png", alt: "darkRedis" },
  { src: "/companiesLogos/spring.png", alt: "spring" },
  { src: "/companiesLogos/darkAcertinity.png", alt: "darkAcertinity" },
  { src: "/companiesLogos/java.png", alt: "java" },
  { src: "/companiesLogos/darkNext.png", alt: "darkNext" },
  { src: "/companiesLogos/darkShadcn.png", alt: "darkShadcn" },
];
const SecondInfiniteScrollSection = () => {
  

  return (
    <>
      <div className="mt-24 h-[6rem] rounded-md flex flex-col antialiased items-center justify-center relative overflow-hidden border-t border-b border-white/1 animate-slide-in-four ">
        <div className="dark:hidden">
          <InfiniteMovingCards
            items={darkImages}
            direction="left"
            speed="normal"
          />
        </div>

        {/* Dark mode images */}
        <div className="hidden dark:block">
          <InfiniteMovingCards
            items={images}
            direction="left"
            speed="normal"
          />
        </div>
      </div>
    </>
  )
}

export default SecondInfiniteScrollSection
