// app/api/cars/services.ts
import { api } from "../config";

interface Car {
  host: {
    userId: number;
    fullName: string;
    email: string;
    phoneNumber: string;
    address: string;
    role: string;
    createdAt: string;
    updatedAt: string;
  };
  carId: number;
  name: string;
  make: string;
  model: string;
  year: number;
  color: string;
  registrationNumber: string;
  photos: string[]; // Array of photo URLs (strings)
  carCategory: string;
  carType: string;
  fuelType: string;
  transmissionMode: string;
  seatingCapacity: number;
  luggageCapacity: number;
  rentalPricePerDay: number;
  rentalPricePerWeek: number;
  rentalPricePerMonth: number;
  availabilityStatus: string;
  maintenanceDueDate: string;
  createdAt: string;
  updatedAt: string;
  insurance: string;
  roadSideAssistance: boolean; // Changed from string to boolean
  fuelPolicy: string;
  features: string;
  importantPoints: string;
  location: {
    locationId: number;
    city: string;
    address: string;
    latitude: number;
    longitude: number;
  };
  reviews: {
    reviewId: number;
    rating: number;
    comments: string;
    createdAt: string;
  }[];
}

// Add this type for creating/updating cars
interface CarDto
  extends Omit<
    Car,
    | "carId"
    | "createdAt"
    | "updatedAt"
    | "host"
    | "location"
    | "reviews"
    | "photos"
  > {
  photos?: File[]; // For file uploads during creation/update
}

export const carService = {
  fetchAllCars: async (): Promise<Car[]> => {
    const response = await api.get("/api/car/getAll");
    console.log("Response from fetchAllCars:", response.data);
    return response.data.data.flat();
  },

  fetchCarsByCity: async (city: string): Promise<Car[]> => {
    const response = await api.get("/api/car/getByC", {
      params: { city },
    });
    return response.data.data.flat();
  },

  fetchCarsByCityAndDate: async (
    city: string,
    startDate: string,
    endDate: string
  ): Promise<Car[]> => {
    // Convert dates to ISO string format if they're not already
    const formattedStartDate = new Date(startDate).toISOString().split("Z")[0];
    const formattedEndDate = new Date(endDate).toISOString().split("Z")[0];
    // const formattedStartDate = "2025-04-04T16:09:27.136129"
    // const formattedEndDate = "2025-05-04T16:09:27.136129";

    console.log("Formatted Start Date:", formattedStartDate);
    console.log("Formatted End Date:", formattedEndDate);

    const response = await api.get("/api/car/getByCT", {
      params: {
        city,
        startDate: formattedStartDate,
        endDate: formattedEndDate,
      },
    });

    console.log("Response from fetchCarsByCityAndDate:", response.data);
    return response.data.data.flat();
  },

  fetchCarsByMakeAndModel: async (
    make: string,
    model: string
  ): Promise<Car[]> => {
    const response = await api.get("/api/car/getByMM", {
      params: { make, model },
    });
    return response.data.data.flat();
  },

  fetchCarById: async (carId: number): Promise<Car> => {
    try {
      const response = await api.get(`/api/car/get/${carId}`);
      console.log("Response from fetchCarById:", response.data.object);
      return response.data.object;
    } catch (error) {
      throw new Error(
        error.response?.data?.message || "Failed to fetch car details"
      );
    }
  },
  fetchAllCarsOfHost: async (): Promise<Car[]> => {
    const response = await api.get("/api/car/getAllSpecific");
    console.log("Response from fetchAllCarsOfHost:", response.data.data.flat());
    return response.data.data.flat();
  },

  addNewCar: async (carData: CarDto & { location?: { city: string; address: string; latitude: number; longitude: number } }, photos: File[]): Promise<Car> => {
    const formData = new FormData();

    // Prepare car data without location (we'll handle it separately)
    const { location, ...carDataWithoutLocation } = carData;
    
    // Append car data as JSON
    formData.append(
      "carDto",
      new Blob([JSON.stringify(carDataWithoutLocation)], {
        type: "application/json",
      })
    );

    

    // Append location data if provided
    if (location) {
      formData.append(
        "locationDto",
        new Blob([JSON.stringify(location)], {
          type: "application/json",
        })
      );
    }

    // Append each photo file
    photos.forEach((file) => {
      formData.append("files", file);
    });
    const response = await api.post("/api/car/create", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    return response.data;
  },

  updateCar: async (
    carId: number, 
    carData: Partial<CarDto> & { location?: { city?: string; address?: string; latitude?: number; longitude?: number } },
    photos?: File[]
  ): Promise<Car> => {
    const formData = new FormData();

    // Prepare car data without location
    const { location, ...carDataWithoutLocation } = carData;
    
    // Append car data as JSON
    formData.append(
      "carDto",
      new Blob([JSON.stringify(carDataWithoutLocation)], {
        type: "application/json",
      })
    );

    // Append location data if provided
    if (location) {
      formData.append(
        "locationDto",
        new Blob([JSON.stringify(location)], {
          type: "application/json",
        })
      );
    }
    console.log("location", location);
    // Append photo files if provided
    if (photos) {
      photos.forEach((file) => {
        formData.append("files", file);
      });
    }

    const response = await api.put(`/api/car/update/${carId}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
console.log("Response from updateCar:", response.data);
    return response.data;
  },
};
