'use client';
import { useState, useMemo } from 'react';
import CarListing from './CarListing';
import FiltersLeftbar from './FiltersLeftbar';

enum CarCategory {
  SEDAN = "SEDAN",
  COUPE = "COUPE",
  HATCHBACK = "HATCHBACK",
  CONVERTIBLE = "CONVERTIBLE",
  WAGON = "WAGON",
  SUV = "SUV",
  CROSSOVER = "CROSSOVER",
  PICKUP_TRUCK = "PICKUP_TRUCK",
  MINIVAN = "MINIVAN"
}

enum CarType {
  ECONOMY = "ECONOMY",
  LUXURY = "LUXURY",
  SPORTS = "SPORTS",
  SUPERCAR = "SUPERCAR",
  ELECTRIC = "ELECTRIC",
  HYBRID = "HYBRID",
  OFF_ROAD = "OFF_ROAD"
}

enum FuelType {
  PETROL = "PETROL",
  DIESEL = "DIESEL",
  ELECTRIC = "ELECTRIC",
  CNG = "CNG",
  OTHER = "OTHER"
}

enum AvailabilityStatus {
  AVAILABLE = "AVAILABLE",
  RESERVED = "RESERVED",
  UNDER_MAINTENANCE = "UNDER_MAINTENANCE"
}

enum TransmissionMode {
  MANUAL = "MANUAL",
  AUTOMATIC = "AUTOMATIC",
  IMT = "IMT"
}

const MainSection = () => {
    const [searchTerm, setSearchTerm] = useState('');
    const [sortOption, setSortOption] = useState('price-low');
    let cars = [
        {
            "carId": 1,
            "name": "Hyundai i20 2021",
            "make": "Hyundai",
            "model": "i20",
            "year": 2021,
            "color": "White",
            "registrationNumber": "KA02CD4567",
            "photos": [
                "https://gaadiwaadi.com/wp-content/uploads/2020/10/2020-Hyundai-i20-N-Line-3.jpg",
                "https://www.hyundai.com/content/dam/hyundai/template_en/en/images/find-a-car/pip/i20-2021/interior/i20-bc3-design-sporty-4-spoke-steering-wheel-original.jpg",
                "https://assets.autochase.in/cars/37693cfc748049e45d87b8c7d8b9aacd/73c2357eeaf24f.jpg",
                "https://images.hindustantimes.com/auto/img/2021/08/26/414x233/Screenshot_2021-08-26_at_10.57.26_AM_1629955798890_1629955806869.png"
            ],
            "carCategory": CarCategory.HATCHBACK,
            "carType": CarType.ECONOMY,
            "fuelType": FuelType.PETROL,
            "transmissionMode": TransmissionMode.MANUAL,
            "seatingCapacity": 5,
            "luggageCapacity": 2,
            "rentalPricePerDay": 1100.0,
            "rentalPricePerWeek": 6700.0,
            "rentalPricePerMonth": 24000.0,
            "availabilityStatus": AvailabilityStatus.AVAILABLE,
            "maintenanceDueDate": "2025-05-10T09:00:00"
        },
        {
            "carId": 2,
            "name": "Honda City 2023",
            "make": "Honda",
            "model": "City",
            "year": 2023,
            "color": "Blue",
            "registrationNumber": "MH12EF7890",
            "photos": [
                "https://www.rushlane.com/wp-content/uploads/2023/08/2023-honda-city-rs-facelift-launch-price-2-1200x900.jpg",
                "https://imgd.aeplcdn.com/1920x1080/n/cw/ec/143275/city-hybrid-ehev-interior-dashboard.jpeg?isig=0&q=80&q=80",
                "https://images.news18.com/ibnlive/uploads/2023/03/honda-city-1.jpg"
            ],
            "carCategory": CarCategory.SEDAN,
            "carType": CarType.ECONOMY,
            "fuelType": FuelType.PETROL,
            "transmissionMode": TransmissionMode.AUTOMATIC,
            "seatingCapacity": 5,
            "luggageCapacity": 3,
            "rentalPricePerDay": 1800.0,
            "rentalPricePerWeek": 11000.0,
            "rentalPricePerMonth": 39000.0,
            "availabilityStatus": AvailabilityStatus.RESERVED,
            "maintenanceDueDate": "2025-06-01T14:00:00"
        },
        {
            "carId": 3,
            "name": "Toyota Innova 2020",
            "make": "Toyota",
            "model": "Innova",
            "year": 2020,
            "color": "Silver",
            "registrationNumber": "DL01GH1234",
            "photos": [
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/68/2022_Toyota_Kijang_Innova_2.4_G_GUN142R_%2820220302%29.jpg/960px-2022_Toyota_Kijang_Innova_2.4_G_GUN142R_%2820220302%29.jpg",
                "https://akm-img-a-in.tosshub.com/indiatoday/images/bodyeditor/202011/2020_Toyota_Innova_Crysta_face-x675.jpg?tGxZhtdWl.3YvbBDPRThL4l4eirdqQNg?size=750:*",
                "https://stimg.cardekho.com/images/carinteriorimages/930x620/Toyota/Innova-Crysta/8105/1606215327927/interior-image-211.jpg?impolicy=resize&imwidth=420"
            ],
            "carCategory": CarCategory.MINIVAN,
            "carType": CarType.ECONOMY,
            "fuelType": FuelType.DIESEL,
            "transmissionMode": TransmissionMode.MANUAL,
            "seatingCapacity": 7,
            "luggageCapacity": 4,
            "rentalPricePerDay": 2500.0,
            "rentalPricePerWeek": 15000.0,
            "rentalPricePerMonth": 52000.0,
            "availabilityStatus": AvailabilityStatus.AVAILABLE,
            "maintenanceDueDate": "2025-04-20T11:30:00"
        },
        {
            "carId": 4,
            "name": "Tata Tiago 2022",
            "make": "Tata",
            "model": "Tiago",
            "year": 2022,
            "color": "Grey",
            "registrationNumber": "TN10JK1122",
            "photos": [
                "https://www.rushlane.com/wp-content/uploads/2022/05/tata-tiago-prices-may-2022-new-1200x900.jpg",
                "https://www.mahindrafirstchoice.com/_next/image?url=https%3A%2F%2Fmedia.mahindrafirstchoice.com%2Flive_web_images%2Fusedcarsimg%2Fmfc%2F598%2F593019%2Fcover_image-20231121165905.jpg&w=1200&q=75",
                "https://www.team-bhp.com/carpics/2022-tata-tiago-cng/l/exterior/2022-tata-tiago-cng-03.jpg"
            ],
            "carCategory": CarCategory.HATCHBACK,
            "carType": CarType.ECONOMY,
            "fuelType": FuelType.CNG,
            "transmissionMode": TransmissionMode.MANUAL,
            "seatingCapacity": 5,
            "luggageCapacity": 2,
            "rentalPricePerDay": 1000.0,
            "rentalPricePerWeek": 6000.0,
            "rentalPricePerMonth": 22000.0,
            "availabilityStatus": AvailabilityStatus.AVAILABLE,
            "maintenanceDueDate": "2025-07-01T10:00:00"
        },
        {
            "carId": 5,
            "name": "Mahindra XUV300 2021",
            "make": "Mahindra",
            "model": "XUV300",
            "year": 2021,
            "color": "Black",
            "registrationNumber": "UP14LM9988",
            "photos": [
                "http://images.overdrive.in/wp-content/odgallery/2021/02/58409_2021_Mahindra_XUV300_1_468x263.jpg",
                "https://stimg.cardekho.com/images/carexteriorimages/930x620/Mahindra/XUV300/7239/1669701927953/headlight-43.jpg",
                "https://media.cars24.com/hello-ar/dev/uploads/6700c0297be5411b5b67a85c/d8f2e45a-d9d5-4872-bdd6-fdee860bfa22/slot/Dashboard.jpg?w=794&format=auto"
            ],
            "carCategory": CarCategory.SUV,
            "carType": CarType.ECONOMY,
            "fuelType": FuelType.DIESEL,
            "transmissionMode": TransmissionMode.AUTOMATIC,
            "seatingCapacity": 5,
            "luggageCapacity": 3,
            "rentalPricePerDay": 2100.0,
            "rentalPricePerWeek": 12500.0,
            "rentalPricePerMonth": 45000.0,
            "availabilityStatus": AvailabilityStatus.AVAILABLE,
            "maintenanceDueDate": "2025-06-15T08:30:00"
        },
        {
            "carId": 6,
            "name": "Kia Seltos 2022",
            "make": "Kia",
            "model": "Seltos",
            "year": 2022,
            "color": "White",
            "registrationNumber": "RJ45XY1234",
            "photos": [
                "https://imgd.aeplcdn.com/664x374/n/cw/ec/115501/seltos-exterior-left-front-three-quarter.jpeg?isig=0&q=80",
                "https://rushlane.com/wp-content/uploads/2021/09/2022-kia-seltos-facelift-sportage-inspired-4-600x338.jpg",
                "https://images.hindustantimes.com/auto/img/2021/05/22/1600x900/1_1618378393206_1621668189975.jpg"
            ],
            "carCategory": CarCategory.SUV,
            "carType": CarType.ECONOMY,
            "fuelType": FuelType.PETROL,
            "transmissionMode": TransmissionMode.MANUAL,
            "seatingCapacity": 5,
            "luggageCapacity": 4,
            "rentalPricePerDay": 2300.0,
            "rentalPricePerWeek": 14000.0,
            "rentalPricePerMonth": 48000.0,
            "availabilityStatus": AvailabilityStatus.RESERVED,
            "maintenanceDueDate": "2025-04-30T12:00:00"
        }
    ]
    
    // State for filters
    const [filters, setFilters] = useState({
        carCategory: [] as string[],
        carType: [] as string[],
        fuelType: [] as string[],
        transmission: [] as string[],
        luggage: [] as string[],
        minPrice: 0,
        maxPrice: 10000,
        minYear: 2000,
        maxYear: 2023,
        seatingCapacity: 5,
    });

    // Filter and sort cars
    const filteredCars = useMemo(() => {
        let result = [...cars];
        // Search filter
        if (searchTerm) {
            const term = searchTerm.toLowerCase();
            result = result.filter(car =>
                car.name.toLowerCase().includes(term) ||
                car.make.toLowerCase().includes(term) ||
                car.model.toLowerCase().includes(term)
            );
        }
        // Car Category filter
        if (filters.carCategory.length > 0) {
            result = result.filter(car =>
                filters.carCategory.includes(car.carCategory)
            );
        }

        // Car Type filter
        if (filters.carType.length > 0) {
            result = result.filter(car =>
                filters.carType.includes(car.carType)
            );
        }

        // Fuel type filter
        if (filters.fuelType.length > 0) {
            result = result.filter(car =>
                filters.fuelType.includes(car.fuelType)
            );
        }

        // Transmission filter
        if (filters.transmission.length > 0) {
            result = result.filter(car =>
                filters.transmission.includes(car.transmissionMode)
            );
        }

        // Price range filter
        result = result.filter(car =>
            car.rentalPricePerDay >= filters.minPrice &&
            car.rentalPricePerDay <= filters.maxPrice
        );

        // Year range filter
        result = result.filter(car =>
            car.year >= filters.minYear &&
            car.year <= filters.maxYear
        );

        // Seating capacity filter
        result = result.filter(car =>
            car.seatingCapacity >= filters.seatingCapacity
        );

        // Sorting
        if (sortOption === 'price-low') {
            result.sort((a, b) => a.rentalPricePerDay - b.rentalPricePerDay);
        } else if (sortOption === 'price-high') {
            result.sort((a, b) => b.rentalPricePerDay - a.rentalPricePerDay);
        } else if (sortOption === 'year-new') {
            result.sort((a, b) => b.year - a.year);
        } else if (sortOption === 'year-old') {
            result.sort((a, b) => a.year - b.year);
        }

        return result;
    }, [cars, searchTerm, filters, sortOption]);

    return (
        <div className="min-h-screen">
            {/* Search Bar Section */}
            <div className="shadow-md mx-20 p-4 rounded-lg">
                <div className="flex items-center gap-4">
                    <input
                        type="text"
                        placeholder="Search by make, model, or location"
                        className="border border-gray-300 rounded-lg p-2 w-full"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                    <button className="bg-blue-500 text-white px-4 py-2 rounded-lg">
                        Search
                    </button>
                </div>
            </div>

            {/* Main Content */}
            <div className="max-w-7xl mx-auto p-6">
                <div className="flex flex-col md:flex-row gap-6">
                    <FiltersLeftbar
                        onFilterChange={setFilters}
                        currentFilters={filters}
                    />
                    <CarListing
                        cars={filteredCars}
                        sortOption={sortOption}
                        onSortChange={setSortOption}
                    />
                </div>
            </div>
        </div>
    );
}

export default MainSection;