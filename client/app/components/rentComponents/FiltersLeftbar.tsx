import React from 'react'
import { useState } from 'react';
import {  FiFilter, FiChevronDown, FiChevronUp } from 'react-icons/fi';
const FiltersLeftbar = () => {
    const [showMoreSegments, setShowMoreSegments] = useState(false);
        const [selectedFilters, setSelectedFilters] = useState({
            segment: [],
            models: [],
            fuelType: [],
            transmission: [],
            seats: [],
            luggage: []
        });

        const toggleFilter = (category, value) => {
            setSelectedFilters(prev => ({
                ...prev,
                [category]: prev[category].includes(value)
                    ? prev[category].filter(item => item !== value)
                    : [...prev[category], value]
            }));
        };
    return (
        <>
            {/* Filter Sidebar */}
            <div className="w-full md:w-1/4 bg-white p-6 rounded-lg shadow-sm">
                <div className="flex items-center justify-between mb-4">
                    <h2 className="font-bold flex items-center">
                        <FiFilter className="mr-2" /> Filters
                    </h2>
                    <button className="text-purple-600 text-sm">Clear All</button>
                </div>

                {/* Segment Filter */}
                <div className="mb-6">
                    <h3 className="font-semibold mb-2">Segment</h3>
                    {['Subscription', 'Super Economy', 'Economy', 'Compact'].map(segment => (
                        <div key={segment} className="flex items-center mb-2">
                            <input
                                type="checkbox"
                                id={`segment-${segment}`}
                                className="mr-2"
                                onChange={() => toggleFilter('segment', segment)}
                            />
                            <label htmlFor={`segment-${segment}`}>{segment}</label>
                        </div>
                    ))}
                    {showMoreSegments && (
                        <>
                            {/* Additional segments */}
                        </>
                    )}
                    <button
                        className="text-purple-600 text-sm mt-2 flex items-center"
                        onClick={() => setShowMoreSegments(!showMoreSegments)}
                    >
                        {showMoreSegments ? <FiChevronUp className="mr-1" /> : <FiChevronDown className="mr-1" />}
                        View {showMoreSegments ? 'Less' : 'More'}
                    </button>
                </div>

                {/* Other filters (Models, Fuel Type, etc.) */}
                {/* ... Similar structure as Segment filter ... */}

                <button className="w-full bg-purple-600 text-white py-2 rounded-lg mt-4 hover:bg-purple-700 transition">
                    Apply Filters
                </button>
            </div>
        </>
    )
}

export default FiltersLeftbar
