export enum CarCategory {
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

export enum CarType {
  ECONOMY = "ECONOMY",
  LUXURY = "LUXURY",
  SPORTS = "SPORTS",
  SUPERCAR = "SUPERCAR",
  ELECTRIC = "ELECTRIC",
  HYBRID = "HYBRID",
  OFF_ROAD = "OFF_ROAD"
}

export enum FuelType {
  PETROL = "PETROL",
  DIESEL = "DIESEL",
  ELECTRIC = "ELECTRIC",
  CNG = "CNG",
  OTHER = "OTHER"
}

export enum AvailabilityStatus {
  AVAILABLE = "AVAILABLE",
  RESERVED = "RESERVED",
  UNDER_MAINTENANCE = "UNDER_MAINTENANCE"
}

export enum TransmissionMode {
  MANUAL = "MANUAL",
  AUTOMATIC = "AUTOMATIC",
  IMT = "IMT"
}

export interface CarListingProps {
    cars: any[];
    sortOption: string;
    onSortChange: (option: string) => void;
}

export interface CarProps {
    carId: number;
    photos: string[];
    name: string;
    carCategory: string;
    carType: string;
    availabilityStatus: string;
    rentalPricePerDay: number;
    rentalPricePerWeek: number;
    rentalPricePerMonth: number;
    transmissionMode: string;
    fuelType: string;
    luggageCapacity: number;
    seatingCapacity: number;
    gridView: boolean; // Added gridView prop
}
export interface Car {
    carId: number;
    photos: string[];
    name: string;
    carCategory: string;
    carType: string;
    availabilityStatus: string;
    rentalPricePerDay: number;
    rentalPricePerWeek: number;
    rentalPricePerMonth: number;
    transmissionMode: string;
    fuelType: string;
    luggageCapacity: number;
    seatingCapacity: number;
    
}

export interface BookingDetailsProps {
  pricePerDay: number;
  carType: CarType;
  location: string;
}

export interface CarSpecificationsProps {
  car: Car;
}