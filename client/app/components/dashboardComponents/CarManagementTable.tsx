"use client";
import React, { useEffect, useState } from 'react';
import { Search, Plus, Edit, Trash2, X, Check, ChevronDown } from 'lucide-react';
import { Input } from '../../../components/ui/input';
import { Button } from '../../../components/ui/button';
import { Card, CardHeader, CardTitle, CardContent } from '../../../components/ui/card';
import { Badge } from '../../../components/ui/badge';
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from '../../../components/ui/select';
import { Switch } from '../../../components/ui/switch';
import { Label } from '../../../components/ui/label';
import { Avatar, AvatarImage, AvatarFallback } from '../../../components/ui/avatar';
import { useCarStore } from '../../../stores/carStore';


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
  photos: string[];
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
  roadSideAssistance: string;
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
const CarManagementTable = () => {
    const { hostCars,fetchAllCarsOfHost } = useCarStore();

    useEffect(() => {
        fetchAllCarsOfHost();
        setCars(hostCars);
    }, [fetchAllCarsOfHost,hostCars]);


    const [searchTerm, setSearchTerm] = useState('');
    const [carTypeFilter, setCarTypeFilter] = useState('');
    const [statusFilter, setStatusFilter] = useState('');
    const [isGridView, setIsGridView] = useState(false);
    const [isAddCarModalOpen, setIsAddCarModalOpen] = useState(false);
    const [editingCar, setEditingCar] = useState(null);
    const [cars, setCars] = useState([
        /*{
            id: 1,
            name: "Honda City",
            registrationNumber: "DL2CAF1234",
            carCategory: "SEDAN",
            carType: "ECONOMY",
            fuelType: "PETROL",
            transmissionMode: "AUTOMATIC",
            totalBookings: 24,
            availabilityStatus: "AVAILABLE",
            review: 4.2,
            make: "Honda",
            model: "City ZX",
            year: 2020,
            color: "White",
            seatingCapacity: 5,
            luggageCapacity: 500,
            pricePerDay: 2000,
            pricePerWeek: 12000,
            pricePerMonth: 50000,
            maintenanceDueDate: "2025-06-15",
            insurance: "Comprehensive",
            roadsideAssistance: true,
            fuelPolicy: "Full to Full",
            features: "Air conditioning",
            importantPoints: "Good for city commute, fuel-efficient",
            photos: ["/honda-city-1.jpg", "/honda-city-2.jpg"]
        },*/
        // Add more sample cars as needed
    ]);

    const filteredCars = cars.filter(car => {
        const matchesSearch = car.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            car.registrationNumber.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesCarType = carTypeFilter ? car.carType === carTypeFilter : true;
        const matchesStatus = statusFilter ? car.availabilityStatus === statusFilter : true;
        return matchesSearch && matchesCarType && matchesStatus;
    });

    const handleAddCar = () => {
        setIsAddCarModalOpen(true);
        setEditingCar({
            id: cars.length + 1,
            name: "",
            registrationNumber: "",
            carCategory: "SEDAN",
            carType: "ECONOMY",
            fuelType: "PETROL",
            transmissionMode: "AUTOMATIC",
            totalBookings: 0,
            availabilityStatus: "AVAILABLE",
            review: 0,
            make: "",
            model: "",
            year: new Date().getFullYear(),
            color: "",
            seatingCapacity: 5,
            luggageCapacity: 0,
            pricePerDay: 0,
            pricePerWeek: 0,
            pricePerMonth: 0,
            maintenanceDueDate: "",
            insurance: "",
            roadsideAssistance: false,
            fuelPolicy: "",
            features: [],
            importantPoints: "",
            photos: []
        });
    };

    const handleSaveCar = () => {
        if (editingCar.id > cars.length) {
            // Add new car
            setCars([...cars, editingCar]);
        } else {
            // Update existing car
            setCars(cars.map(car => car.id === editingCar.id ? editingCar : car));
        }
        setIsAddCarModalOpen(false);
        setEditingCar(null);
    };

    const handleDeleteCar = (id) => {
        setCars(cars.filter(car => car.id !== id));
        setIsAddCarModalOpen(false);
        setEditingCar(null);
    };

    const getStatusBadgeColor = (status) => {
        switch (status) {
            case "AVAILABLE": return "bg-green-500";
            case "RESERVED": return "bg-yellow-500";
            case "UNDER_MAINTENANCE": return "bg-red-500";
            default: return "bg-gray-500";
        }
    };

    return (
        <div className="relative">
            {/* Filter/Search Row */}
            <div className="bg-gray-800 rounded-lg p-6 mb-6">
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div className="flex flex-col sm:flex-row gap-4 w-full md:w-auto">
                        <div className="relative flex-1">
                            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                            <Input
                                placeholder="Search cars..."
                                className="pl-10 w-full"
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                        </div>

                        <Select value={carTypeFilter} onValueChange={setCarTypeFilter}>
                            <SelectTrigger className="w-full sm:w-[180px]">
                                <SelectValue placeholder="Car Type" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value=" ">All Types</SelectItem>
                                {carTypes.map(type => (
                                    <SelectItem key={type} value={type}>{type}</SelectItem>
                                ))}
                            </SelectContent>
                        </Select>

                        <Select value={statusFilter} onValueChange={setStatusFilter}>
                            <SelectTrigger className="w-full sm:w-[180px]">
                                <SelectValue placeholder="Status" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value=" ">All Statuses</SelectItem>
                                {availabilityStatuses.map(status => (
                                    <SelectItem key={status} value={status}>{status}</SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>

                    <div className="flex items-center gap-4">
                        <div className="flex items-center space-x-2">
                            <Switch
                                id="view-mode"
                                checked={isGridView}
                                onCheckedChange={setIsGridView}
                            />
                            <Label htmlFor="view-mode">
                                {isGridView ? 'Grid' : 'List'} View
                            </Label>
                        </div>

                        <Button
                            variant="outline"
                            className="border-gray-600 hover:bg-gray-700"
                            onClick={handleAddCar}
                        >
                            <Plus className="mr-2 h-4 w-4" />
                            Add Car
                        </Button>
                    </div>
                </div>
            </div>

            {/* Cars Table */}
            {isGridView ? (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                    {filteredCars.map((car) => (
                        <Card key={car.id} className="hover:shadow-lg transition-shadow">
                            <CardHeader className="flex flex-row items-center justify-between pb-2">
                                <CardTitle className="text-lg">{car.name}</CardTitle>
                                <Badge className={getStatusBadgeColor(car.availabilityStatus)}>
                                    {car.availabilityStatus}
                                </Badge>
                            </CardHeader>
                            <CardContent>
                                <div className="space-y-2">
                                    <div className="flex justify-between">
                                        <span className="text-gray-400">Reg No:</span>
                                        <span>{car.registrationNumber}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-gray-400">Category:</span>
                                        <span>{car.carCategory}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-gray-400">Type:</span>
                                        <span>{car.carType}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-gray-400">Bookings:</span>
                                        <span>{car.totalBookings}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-gray-400">Rating:</span>
                                        <span>{car.review}/5</span>
                                    </div>
                                </div>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            ) : (
                <div className="bg-gray-800 rounded-lg overflow-hidden">
                    <table className="min-w-full divide-y divide-gray-700">
                        <thead className="bg-gray-700">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">S.No</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Name</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Reg No</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Category</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Type</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Fuel</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Transmission</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Bookings</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Status</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Rating</th>
                            </tr>
                        </thead>
                        <tbody className="bg-gray-800 divide-y divide-gray-700">
                            {filteredCars.map((car, index) => (
                                <tr
                                    key={car.id}
                                    className="hover:bg-gray-700 cursor-pointer"
                                    onClick={() => {
                                        setIsAddCarModalOpen(true);
                                        setEditingCar(car);
                                    }}
                                >
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{index + 1}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-white">{car.name}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{car.registrationNumber}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{car.carCategory}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{car.carType}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{car.fuelType}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{car.transmissionMode}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{car.totalBookings}</td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <Badge className={getStatusBadgeColor(car.availabilityStatus)}>
                                            {car.availabilityStatus}
                                        </Badge>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{car.review}/5</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* Add/Edit Car Modal */}
            {isAddCarModalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
                    <Card className="w-full max-w-4xl max-h-[90vh] overflow-y-auto">
                        <CardHeader className="flex flex-row items-center justify-between pb-4 border-b">
                            <CardTitle>
                                {editingCar.id > cars.length ? 'Add New Car' : 'Edit Car Details'}
                            </CardTitle>
                            <div className="flex gap-2">
                                {editingCar.id <= cars.length && (
                                    <Button
                                        variant="destructive"
                                        size="sm"
                                        onClick={() => handleDeleteCar(editingCar.id)}
                                    >
                                        <Trash2 className="h-4 w-4 mr-2" />
                                        Delete
                                    </Button>
                                )}
                                <Button
                                    variant="outline"
                                    size="sm"
                                    onClick={() => setIsAddCarModalOpen(false)}
                                >
                                    <X className="h-4 w-4 mr-2" />
                                    Close
                                </Button>
                                <Button
                                    size="sm"
                                    onClick={handleSaveCar}
                                >
                                    <Check className="h-4 w-4 mr-2" />
                                    Save
                                </Button>
                            </div>
                        </CardHeader>
                        <CardContent className="pt-6">
                            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                                {/* Left Column - photos */}
                                <div>
                                    <h3 className="text-lg font-medium mb-4">Car photos</h3>
                                    <div className="grid grid-cols-3 gap-2">
                                        {editingCar.photos.map((img, index) => (
                                            <div key={index} className="relative group">
                                                <Avatar className="w-full h-32 rounded-lg">
                                                    <AvatarImage src={img} />
                                                    <AvatarFallback>Car</AvatarFallback>
                                                </Avatar>
                                                <Button
                                                    variant="destructive"
                                                    size="sm"
                                                    className="absolute top-1 right-1 opacity-0 group-hover:opacity-100 transition-opacity"
                                                >
                                                    <Trash2 className="h-3 w-3" />
                                                </Button>
                                            </div>
                                        ))}
                                        <div className="border-2 border-dashed border-gray-300 rounded-lg flex items-center justify-center h-32 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700">
                                            <Plus className="h-8 w-8 text-gray-400" />
                                        </div>
                                    </div>
                                </div>

                                {/* Right Column - Details */}
                                <div className="space-y-4">
                                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                        <div>
                                            <Label>Name</Label>
                                            <Input value={editingCar.name} onChange={(e) => setEditingCar({ ...editingCar, name: e.target.value })} />
                                        </div>
                                        <div>
                                            <Label>Registration Number</Label>
                                            <Input value={editingCar.registrationNumber} onChange={(e) => setEditingCar({ ...editingCar, registrationNumber: e.target.value })} />
                                        </div>
                                        <div>
                                            <Label>Make</Label>
                                            <Input value={editingCar.make} onChange={(e) => setEditingCar({ ...editingCar, make: e.target.value })} />
                                        </div>
                                        <div>
                                            <Label>Model</Label>
                                            <Input value={editingCar.model} onChange={(e) => setEditingCar({ ...editingCar, model: e.target.value })} />
                                        </div>
                                        <div>
                                            <Label>Year</Label>
                                            <Input type="number" value={editingCar.year} onChange={(e) => setEditingCar({ ...editingCar, year: parseInt(e.target.value) })} />
                                        </div>
                                        <div>
                                            <Label>Color</Label>
                                            <Input value={editingCar.color} onChange={(e) => setEditingCar({ ...editingCar, color: e.target.value })} />
                                        </div>
                                    </div>

                                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                        <div>
                                            <Label>Category</Label>
                                            <Select value={editingCar.carCategory} onValueChange={(value) => setEditingCar({ ...editingCar, carCategory: value })}>
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Select category" />
                                                </SelectTrigger>
                                                <SelectContent>
                                                    {carCategories.map(category => (
                                                        <SelectItem key={category} value={category}>{category}</SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        </div>
                                        <div>
                                            <Label>Type</Label>
                                            <Select value={editingCar.carType} onValueChange={(value) => setEditingCar({ ...editingCar, carType: value })}>
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Select type" />
                                                </SelectTrigger>
                                                <SelectContent>
                                                    {carTypes.map(type => (
                                                        <SelectItem key={type} value={type}>{type}</SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        </div>
                                        <div>
                                            <Label>Fuel Type</Label>
                                            <Select value={editingCar.fuelType} onValueChange={(value) => setEditingCar({ ...editingCar, fuelType: value })}>
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Select fuel type" />
                                                </SelectTrigger>
                                                <SelectContent>
                                                    {fuelTypes.map(fuel => (
                                                        <SelectItem key={fuel} value={fuel}>{fuel}</SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        </div>
                                        <div>
                                            <Label>Transmission</Label>
                                            <Select value={editingCar.transmissionMode} onValueChange={(value) => setEditingCar({ ...editingCar, transmissionMode: value })}>
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Select transmission" />
                                                </SelectTrigger>
                                                <SelectContent>
                                                    {transmissionModes.map(transmission => (
                                                        <SelectItem key={transmission} value={transmission}>{transmission}</SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        </div>
                                    </div>

                                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                                        <div>
                                            <Label>Seating Capacity</Label>
                                            <Input type="number" value={editingCar.seatingCapacity} onChange={(e) => setEditingCar({ ...editingCar, seatingCapacity: parseInt(e.target.value) })} />
                                        </div>
                                        <div>
                                            <Label>Luggage Capacity (L)</Label>
                                            <Input type="number" value={editingCar.luggageCapacity} onChange={(e) => setEditingCar({ ...editingCar, luggageCapacity: parseInt(e.target.value) })} />
                                        </div>
                                        <div>
                                            <Label>Status</Label>
                                            <Select value={editingCar.availabilityStatus} onValueChange={(value) => setEditingCar({ ...editingCar, availabilityStatus: value })}>
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Select status" />
                                                </SelectTrigger>
                                                <SelectContent>
                                                    {availabilityStatuses.map(status => (
                                                        <SelectItem key={status} value={status}>{status}</SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        </div>
                                    </div>

                                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                                        <div>
                                            <Label>Price Per Day (₹)</Label>
                                            <Input type="number" value={editingCar.pricePerDay} onChange={(e) => setEditingCar({ ...editingCar, pricePerDay: parseInt(e.target.value) })} />
                                        </div>
                                        <div>
                                            <Label>Price Per Week (₹)</Label>
                                            <Input type="number" value={editingCar.pricePerWeek} onChange={(e) => setEditingCar({ ...editingCar, pricePerWeek: parseInt(e.target.value) })} />
                                        </div>
                                        <div>
                                            <Label>Price Per Month (₹)</Label>
                                            <Input type="number" value={editingCar.pricePerMonth} onChange={(e) => setEditingCar({ ...editingCar, pricePerMonth: parseInt(e.target.value) })} />
                                        </div>
                                    </div>

                                    <div>
                                        <Label>Maintenance Due Date</Label>
                                        <Input type="date" value={editingCar.maintenanceDueDate} onChange={(e) => setEditingCar({ ...editingCar, maintenanceDueDate: e.target.value })} />
                                    </div>

                                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                        <div>
                                            <Label>Insurance</Label>
                                            <Input value={editingCar.insurance} onChange={(e) => setEditingCar({ ...editingCar, insurance: e.target.value })} />
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <Label>Roadside Assistance</Label>
                                            <Switch
                                                checked={editingCar.roadsideAssistance}
                                                onCheckedChange={(checked) => setEditingCar({ ...editingCar, roadsideAssistance: checked })}
                                            />
                                        </div>
                                    </div>

                                    <div>
                                        <Label>Fuel Policy</Label>
                                        <Input value={editingCar.fuelPolicy} onChange={(e) => setEditingCar({ ...editingCar, fuelPolicy: e.target.value })} />
                                    </div>

                                    <div>
                                        <Label>Features (comma separated)</Label>
                                        <Input value={editingCar.features.join(', ')} onChange={(e) => setEditingCar({ ...editingCar, features: e.target.value.split(', ') })} />
                                    </div>

                                    <div>
                                        <Label>Important Points</Label>
                                        <textarea
                                            className="flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 min-h-[100px]"
                                            value={editingCar.importantPoints}
                                            onChange={(e) => setEditingCar({ ...editingCar, importantPoints: e.target.value })}
                                        />
                                    </div>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                </div>
            )}
        </div>
    );
};

export default CarManagementTable;