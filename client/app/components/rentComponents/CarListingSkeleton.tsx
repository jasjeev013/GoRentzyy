"use client"
import React from 'react'

const CarListingSkeleton = () => {
  return (
    <div className="min-h-screen">
      {/* Search Bar Skeleton */}
      <div className="shadow-md mx-20 p-4 rounded-lg mb-6 animate-pulse">
        <div className="h-10 bg-gray-200 dark:bg-gray-700 rounded-lg"></div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto p-6">
        <div className="flex flex-col md:flex-row gap-6">
          {/* Filters Sidebar Skeleton */}
          <div className="w-full md:w-1/4 p-6 rounded-lg border bg-gray-100 dark:bg-gray-800 shadow-sm space-y-6">
            {/* Filter sections */}
            {[...Array(7)].map((_, i) => (
              <div key={i} className="space-y-3">
                <div className="h-6 bg-gray-300 dark:bg-gray-700 rounded w-3/4"></div>
                <div className="space-y-2">
                  {[...Array(4)].map((_, j) => (
                    <div key={j} className="flex items-center gap-2">
                      <div className="h-5 w-5 bg-gray-300 dark:bg-gray-700 rounded"></div>
                      <div className="h-4 bg-gray-300 dark:bg-gray-700 rounded w-3/4"></div>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>

          {/* Car Listing Skeleton */}
          <div className="w-full md:w-3/4 space-y-4">
            {/* Sorting Controls */}
            <div className="p-4 rounded-lg shadow-sm mb-6 flex justify-between items-center bg-gray-100 dark:bg-gray-800">
              <div className="flex items-center gap-2">
                <div className="h-4 bg-gray-300 dark:bg-gray-700 rounded w-16"></div>
                <div className="h-10 bg-gray-300 dark:bg-gray-700 rounded w-32"></div>
              </div>
              <div className="flex gap-2">
                <div className="h-10 w-10 bg-gray-300 dark:bg-gray-700 rounded"></div>
                <div className="h-10 w-10 bg-gray-300 dark:bg-gray-700 rounded"></div>
              </div>
            </div>

            {/* Car Grid Skeleton */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[...Array(6)].map((_, i) => (
                <div key={i} className="bg-white dark:bg-gray-800 rounded-lg shadow-sm overflow-hidden border border-gray-200 dark:border-gray-700">
                  {/* Image */}
                  <div className="h-48 bg-gray-200 dark:bg-gray-700 animate-pulse"></div>
                  
                  {/* Content */}
                  <div className="p-4 space-y-3">
                    <div className="flex justify-between">
                      <div className="h-6 bg-gray-200 dark:bg-gray-700 rounded w-3/4"></div>
                      <div className="h-5 bg-gray-200 dark:bg-gray-700 rounded w-20"></div>
                    </div>
                    
                    <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-1/2"></div>
                    
                    {/* Price Tiers */}
                    <div className="flex justify-between">
                      {[...Array(3)].map((_, j) => (
                        <div key={j} className="text-center">
                          <div className="h-5 bg-gray-200 dark:bg-gray-700 rounded w-12 mx-auto"></div>
                          <div className="h-3 bg-gray-200 dark:bg-gray-700 rounded w-16 mx-auto mt-1"></div>
                        </div>
                      ))}
                    </div>
                    
                    {/* Features */}
                    <div className="grid grid-cols-2 gap-2">
                      {[...Array(4)].map((_, j) => (
                        <div key={j} className="flex items-center">
                          <div className="h-4 w-4 bg-gray-200 dark:bg-gray-700 rounded-full mr-1"></div>
                          <div className="h-3 bg-gray-200 dark:bg-gray-700 rounded w-16"></div>
                        </div>
                      ))}
                    </div>
                    
                    {/* Button */}
                    <div className="h-10 bg-gray-200 dark:bg-gray-700 rounded-lg mt-4"></div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default CarListingSkeleton