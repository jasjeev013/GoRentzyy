"use client"
import React from 'react'
import { Loader2 } from 'lucide-react'

const CarPageSkeleton = () => {
  return (
    <div className="w-full min-h-screen py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Left Section (3/5 width) */}
          <div className="lg:w-3/5 space-y-8">
            {/* Car title and info skeleton */}
            <div className="space-y-4">
              <div className="h-8 bg-gray-200 dark:bg-gray-700 rounded w-3/4 animate-pulse"></div>
              <div className="flex gap-4">
                {[...Array(4)].map((_, i) => (
                  <div key={i} className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-16 animate-pulse"></div>
                ))}
              </div>
            </div>
            
            {/* Image gallery skeleton */}
            <div className="aspect-video bg-gray-200 dark:bg-gray-700 rounded-lg animate-pulse"></div>
            
            {/* Tabs skeleton */}
            <div className="flex gap-8 border-b border-gray-200 dark:border-gray-700 pb-4">
              {['Details', 'Reviews', 'Location'].map((tab) => (
                <div key={tab} className="h-8 bg-gray-200 dark:bg-gray-700 rounded w-20 animate-pulse"></div>
              ))}
            </div>
            
            {/* Tab content skeleton */}
            <div className="space-y-6">
              {[...Array(5)].map((_, i) => (
                <div key={i} className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-full animate-pulse"></div>
              ))}
              <div className="grid grid-cols-2 gap-4 mt-4">
                {[...Array(8)].map((_, i) => (
                  <div key={i} className="h-4 bg-gray-200 dark:bg-gray-700 rounded animate-pulse"></div>
                ))}
              </div>
            </div>
          </div>
          
          {/* Right Section (2/5 width) */}
          <div className="lg:w-2/5 space-y-6">
            {/* Booking details skeleton */}
            <div className="bg-gray-200 dark:bg-gray-700 rounded-lg p-6 space-y-4 animate-pulse">
              <div className="flex justify-between">
                <div className="h-6 bg-gray-300 dark:bg-gray-600 rounded w-1/3"></div>
                <div className="h-6 bg-gray-300 dark:bg-gray-600 rounded w-1/4"></div>
              </div>
              <div className="h-4 bg-gray-300 dark:bg-gray-600 rounded w-3/4"></div>
              <div className="space-y-2">
                {[...Array(4)].map((_, i) => (
                  <div key={i} className="h-10 bg-gray-300 dark:bg-gray-600 rounded"></div>
                ))}
              </div>
            </div>
            
            {/* Pickup options skeleton */}
            <div className="bg-gray-200 dark:bg-gray-700 rounded-lg p-6 animate-pulse">
              <div className="h-6 bg-gray-300 dark:bg-gray-600 rounded w-1/2 mb-4"></div>
              <div className="space-y-2">
                {[...Array(3)].map((_, i) => (
                  <div key={i} className="h-4 bg-gray-300 dark:bg-gray-600 rounded"></div>
                ))}
              </div>
            </div>
            
            {/* Booking summary skeleton */}
            <div className="bg-gray-200 dark:bg-gray-700 rounded-lg p-6 animate-pulse">
              <div className="h-6 bg-gray-300 dark:bg-gray-600 rounded w-1/2 mb-4"></div>
              <div className="space-y-3">
                {[...Array(5)].map((_, i) => (
                  <div key={i} className="flex justify-between">
                    <div className="h-4 bg-gray-300 dark:bg-gray-600 rounded w-1/3"></div>
                    <div className="h-4 bg-gray-300 dark:bg-gray-600 rounded w-1/4"></div>
                  </div>
                ))}
              </div>
              <div className="h-10 bg-gray-300 dark:bg-gray-600 rounded-full mt-6"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default CarPageSkeleton