'use client';
import CarListing from './CarListing';
import FiltersLeftbar from './FiltersLeftbar';

const MainSection = () => {
    
    

    return (
        <div className="min-h-screen ">
            {/* Search Bar Section */}
            <div className=" shadow-md mx-20 p-4  rounded-lg">
                    <div className=" flex items-center gap-4">
                        <input
                            type="text"
                            placeholder="Search by make, model, or location"
                            className="border border-gray-300 rounded-lg p-2 w-full"
                        />
                        <button className="bg-blue-500 text-white px-4 py-2 rounded-lg">Search</button>
                    </div>
                </div>
           
            {/* Main Content */}
            <div className="max-w-7xl mx-auto p-6">
                <div className="flex flex-col md:flex-row gap-6">
                    
                    <FiltersLeftbar />
                    <CarListing />
                </div>
            </div>
        </div>
    );
}

export default MainSection
