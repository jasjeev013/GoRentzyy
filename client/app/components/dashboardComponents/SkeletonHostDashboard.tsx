import React from 'react'

const SkeletonHostDashboard = () => {
  return (
    <div className="w-full min-h-screen flex items-center justify-center">
        <div className="w-[calc(100%-18rem)] min-h-screen p-8">
          <div className="flex flex-col space-y-4">
            {/* Header Loading */}
            <div className="flex justify-between items-center mb-8">
              <div className="h-10 w-64 bg-gray-700 rounded animate-pulse"></div>
              <div className="flex space-x-4">
                <div className="h-10 w-10 bg-gray-700 rounded-full animate-pulse"></div>
                <div className="h-10 w-10 bg-gray-700 rounded-full animate-pulse"></div>
              </div>
            </div>

            {/* Tabs Loading */}
            <div className="flex space-x-4 mb-6">
              {[...Array(4)].map((_, i) => (
                <div key={i} className="h-10 w-24 bg-gray-700 rounded-md animate-pulse"></div>
              ))}
            </div>

            {/* Metrics Loading */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
              {[...Array(4)].map((_, i) => (
                <div key={i} className="h-32 bg-gray-800 rounded-lg animate-pulse"></div>
              ))}
            </div>

            {/* Financial Cards Loading */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 mb-6">
              {[...Array(2)].map((_, i) => (
                <div key={i} className="h-32 bg-gray-800 rounded-lg animate-pulse"></div>
              ))}
            </div>

            {/* Chart Loading */}
            <div className="h-80 bg-gray-800 rounded-lg animate-pulse mb-6"></div>

            {/* Popular Cars Loading */}
            <div className="mb-6">
              <div className="h-6 w-48 bg-gray-700 rounded mb-4 animate-pulse"></div>
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                {[...Array(4)].map((_, i) => (
                  <div key={i} className="h-64 bg-gray-800 rounded-lg animate-pulse"></div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
  )
}

export default SkeletonHostDashboard
