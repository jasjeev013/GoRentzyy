'use client';
import CarListing from './CarListing';
import FiltersLeftbar from './FiltersLeftbar';

const MainSection = () => {
    
    

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Search Bar Section */}
            

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
