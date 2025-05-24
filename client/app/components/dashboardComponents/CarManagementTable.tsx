"use client";
import React, { useEffect, useState, useRef } from 'react';
import { Search, Plus, Edit, Trash2, X, Check, ChevronDown, Star } from 'lucide-react';
import { Input } from '../../../components/ui/input';
import { Button } from '../../../components/ui/button';
import { Card, CardHeader, CardTitle, CardContent } from '../../../components/ui/card';
import { Badge } from '../../../components/ui/badge';
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from '../../../components/ui/select';
import { Switch } from '../../../components/ui/switch';
import { Label } from '../../../components/ui/label';
import { useCarStore } from '../../../stores/carStore';
import { Location } from '../../types';
import GoogleMapLocationPicker from './GoogleMapLocationPicker';

const carTypes = [
    "ECONOMY",
    "LUXURY",
    "SPORTS",
    "SUPERCAR",
    "ELECTRIC",
    "HYBRID",
    "OFF ROAD",
];

const carCategories = [
    "SEDAN",
    "COUPE",
    "HATCHBACK",
    "CONVERTIBLE",
    "WAGON",
    "SUV",
    "CROSSOVER",
    "PICKUP TRUCK",
    "MINIVAN",
];

const fuelTypes = [
    "PETROL",
    "DIESEL",
    "ELECTRIC",
    "CNG",
    "OTHER",
];

const availabilityStatuses = [
    "AVAILABLE",
    "RESERVED",
    "UNDER_MAINTENANCE",
];

const transmissionModes = [
    "MANUAL",
    "AUTOMATIC",
    "IMT",
];



const CarManagementTable = () => {
    const { hostCars, fetchAllCarsOfHost, addNewCar, updateCar } = useCarStore();
    const fileInputRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        fetchAllCarsOfHost();
        setCars(hostCars);
        console.log(hostCars);
    }, [fetchAllCarsOfHost]);

    const [searchTerm, setSearchTerm] = useState('');
    const [carTypeFilter, setCarTypeFilter] = useState('');
    const [statusFilter, setStatusFilter] = useState('');
    const [isGridView, setIsGridView] = useState(false);
    const [isAddCarModalOpen, setIsAddCarModalOpen] = useState(false);
    const [editingCar, setEditingCar] = useState<any>(null);
    const [cars, setCars] = useState<any[]>([]);
    const [photoPreviews, setPhotoPreviews] = useState<string[]>([]);
    const [photoFiles, setPhotoFiles] = useState<File[]>([]);
    const [editingLocation, setEditingLocation] = useState<Location | null>(null);


    const filteredCars = cars.filter(car => {
        const matchesSearch = car.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            car.registrationNumber?.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesCarType = carTypeFilter ? car.carType === carTypeFilter : true;
        const matchesStatus = statusFilter ? car.availabilityStatus === statusFilter : true;
        return matchesSearch && matchesCarType && matchesStatus;
    });

    const calculateAverageRating = (reviews: any[]) => {
        if (!reviews || reviews.length === 0) return 0;
        const sum = reviews.reduce((total, review) => total + review.rating, 0);
        return (sum / reviews.length).toFixed(1);
    };

    const handleAddCar = () => {
        setIsAddCarModalOpen(true);
        setPhotoPreviews([]);
        setPhotoFiles([]);
        setEditingCar({
            name: "",
            registrationNumber: "",
            carCategory: "SEDAN",
            carType: "ECONOMY",
            fuelType: "PETROL",
            transmissionMode: "AUTOMATIC",
            availabilityStatus: "AVAILABLE",
            make: "",
            model: "",
            year: new Date().getFullYear(),
            color: "",
            seatingCapacity: 5,
            luggageCapacity: 0,
            rentalPricePerDay: 0,
            rentalPricePerWeek: 0,
            rentalPricePerMonth: 0,
            maintenanceDueDate: "",
            insurance: "",
            roadsideAssistance: false,
            fuelPolicy: "",
            features: "",
            importantPoints: "",
        });
        setEditingLocation({
            city: "",
            address: "",
            latitude: 0,
            longitude: 0
        });
    };

    const handlePhotoUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files.length > 0) {
            const files = Array.from(e.target.files);

            // Create preview URLs
            const newPreviews = files.map(file => URL.createObjectURL(file));
            setPhotoPreviews(prev => [...prev, ...newPreviews]);

            // Store the actual files for upload
            setPhotoFiles(prev => [...prev, ...files]);
        }
    };

    const handleRemovePhoto = (index: number) => {
        // Revoke the object URL to prevent memory leaks
        URL.revokeObjectURL(photoPreviews[index]);

        // Remove from both previews and files
        const newPreviews = [...photoPreviews];
        newPreviews.splice(index, 1);
        setPhotoPreviews(newPreviews);

        const newFiles = [...photoFiles];
        newFiles.splice(index, 1);
        setPhotoFiles(newFiles);
    };

    const handleSaveCar = async () => {
        try {
            const carData = {
                ...editingCar,
                location: editingLocation
            };
            console.log("Car Data to Save:", carData);

            if (editingCar?.carId) {
                // Update existing car
                console.log("Updating existing car with ID:", editingCar.carId);
                await updateCar(
                    editingCar.carId,
                    carData,
                    photoFiles,
                );
            } else {
                // Create new car
                console.log("Creating new car");
                await addNewCar(
                    carData,
                    photoFiles,
                );
            }
            fetchAllCarsOfHost();
            setIsAddCarModalOpen(false);
            setEditingCar(null);
            setEditingLocation(null);
            setPhotoPreviews([]);
            setPhotoFiles([]);
        } catch (error) {
            console.error("Error saving car:", error);
        }
    };

    const handleDeleteCar = (id: number) => {
        setCars(cars.filter(car => car.carId !== id));
        setIsAddCarModalOpen(false);
        setEditingCar(null);
    };

    const getStatusBadgeColor = (status: string) => {
        switch (status) {
            case "AVAILABLE": return "bg-green-500";
            case "RESERVED": return "bg-yellow-500";
            case "UNDER_MAINTENANCE": return "bg-red-500";
            default: return "bg-gray-500";
        }
    };

    const handleLocationChange = (field: keyof Location, value: string | number) => {
        setEditingLocation(prev => ({
            ...(prev || { city: "", address: "", latitude: 0, longitude: 0 }),
            [field]: value
        }));
    };

    const handleMapLocationSelect = (lat: number, lng: number) => {
        handleLocationChange('latitude', lat);
        handleLocationChange('longitude', lng);
    };

    // Clean up object URLs when component unmounts
    useEffect(() => {
        return () => {
            photoPreviews.forEach(preview => URL.revokeObjectURL(preview));
        };
    }, [photoPreviews]);

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
                        <Card key={car.carId} className="hover:shadow-lg transition-shadow">
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
                                        <span>10</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-gray-400">Location:</span>
                                        <span>{car.location?.city || 'N/A'}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-gray-400">Rating:</span>
                                        <span className="flex items-center">
                                            <Star className="h-4 w-4 fill-yellow-400 text-yellow-400 mr-1" />
                                            {calculateAverageRating(car.reviews)}/5
                                        </span>
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
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Location</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Status</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Rating</th>
                            </tr>
                        </thead>
                        <tbody className="bg-gray-800 divide-y divide-gray-700">
                            {filteredCars.map((car, index) => (
                                <tr
                                    key={car.carId}
                                    className="hover:bg-gray-700 cursor-pointer"
                                    onClick={() => {
                                        setIsAddCarModalOpen(true);
                                        setEditingCar(car);
                                        setPhotoPreviews(car.photos || []);
                                        setEditingLocation(car.location || {
                                            city: "",
                                            address: "",
                                            latitude: 0,
                                            longitude: 0
                                        });
                                    }}
                                >
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{index + 1}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-white">{car.name}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{car.registrationNumber}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{car.carCategory}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{car.carType}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{car.fuelType}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{car.transmissionMode}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">10</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">
                                        {car.location?.city || 'N/A'}
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <Badge className={getStatusBadgeColor(car.availabilityStatus)}>
                                            {car.availabilityStatus}
                                        </Badge>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300 flex items-center">
                                        <Star className="h-4 w-4 fill-yellow-400 text-yellow-400 mr-1" />
                                        {calculateAverageRating(car.reviews)}/5
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* Add/Edit Car Modal */}
            {isAddCarModalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
                    <Card className="w-full max-w-6xl max-h-[90vh] overflow-y-auto">
                        <CardHeader className="flex flex-row items-center justify-between pb-4 border-b">
                            <CardTitle>
                                {editingCar?.carId ? 'Edit Car Details' : 'Add New Car'}
                            </CardTitle>
                            <div className="flex gap-2">
                                {editingCar?.carId && (
                                    <Button
                                        variant="destructive"
                                        size="sm"
                                        onClick={() => handleDeleteCar(editingCar.carId)}
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
                            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                                {/* Photos Column */}
                                <div className="lg:col-span-1">
                                    <h3 className="text-lg font-medium mb-4">Car photos</h3>
                                    <div className="grid grid-cols-2 gap-2">
                                        {photoPreviews.map((preview, index) => (
                                            <div key={index} className="relative group">
                                                <img
                                                    src={preview}
                                                    alt={`Preview ${index}`}
                                                    className="w-full h-32 rounded-lg object-cover"
                                                />
                                                <Button
                                                    variant="destructive"
                                                    size="sm"
                                                    className="absolute top-1 right-1 opacity-0 group-hover:opacity-100 transition-opacity"
                                                    onClick={() => handleRemovePhoto(index)}
                                                >
                                                    <Trash2 className="h-3 w-3" />
                                                </Button>
                                            </div>
                                        ))}
                                        <div
                                            className="border-2 border-dashed border-gray-300 rounded-lg flex items-center justify-center h-32 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700"
                                            onClick={() => fileInputRef.current?.click()}
                                        >
                                            <Plus className="h-8 w-8 text-gray-400" />
                                            <input
                                                type="file"
                                                ref={fileInputRef}
                                                className="hidden"
                                                multiple
                                                accept="image/*"
                                                onChange={handlePhotoUpload}
                                            />
                                        </div>
                                    </div>
                                    {/* Location Column */}
                                    {/* Location Column */}
                                    <div className="lg:col-span-1">
                                        <h3 className="text-lg font-medium mb-4">Car Location</h3>
                                        <div className="space-y-4">
                                            <div>
                                                <Label className='my-1'>City</Label>
                                                <Input
                                                    value={editingLocation?.city || ''}
                                                    onChange={(e) => handleLocationChange('city', e.target.value)}
                                                    placeholder="e.g., New Delhi"
                                                />
                                            </div>

                                            <div>
                                                <Label className='my-1'>Address</Label>
                                                <textarea
                                                    className="flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 min-h-[80px]"
                                                    value={editingLocation?.address || ''}
                                                    onChange={(e) => handleLocationChange('address', e.target.value)}
                                                    placeholder="e.g., 123 Main Street, Connaught Place"
                                                />
                                            </div>

                                            <div className="grid grid-cols-2 gap-2">
                                                <div>
                                                    <Label className='my-1'>Latitude</Label>
                                                    <Input
                                                        type="number"
                                                        value={editingLocation?.latitude || 0}
                                                        onChange={(e) => handleLocationChange('latitude', parseFloat(e.target.value))}
                                                        step="0.000001"
                                                    />
                                                </div>
                                                <div>
                                                    <Label className='my-1'>Longitude</Label>
                                                    <Input
                                                        type="number"
                                                        value={editingLocation?.longitude || 0}
                                                        onChange={(e) => handleLocationChange('longitude', parseFloat(e.target.value))}
                                                        step="0.000001"
                                                    />
                                                </div>
                                            </div>

                                            <div>
                                                <Label className='my-1'>Select on Map</Label>
                                                <GoogleMapLocationPicker
                                                    onLocationSelect={handleMapLocationSelect}
                                                    initialLocation={
                                                        editingLocation?.latitude && editingLocation?.longitude
                                                            ? { lat: editingLocation.latitude, lng: editingLocation.longitude }
                                                            : undefined
                                                    }
                                                />
                                                <p className="text-sm text-muted-foreground mt-1">
                                                    Click on the map to set the exact location
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>


                                {/* Right Column - Details */}
                                <div className="lg:col-span-2 space-y-4">
                                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                        <div>
                                            <Label className='my-1'>Name</Label>
                                            <Input
                                                value={editingCar.name}
                                                onChange={(e) => setEditingCar({ ...editingCar, name: e.target.value })}
                                                placeholder="e.g., Toyota Camry 2023"
                                            />
                                        </div>
                                        <div>
                                            <Label className='my-1'>Registration Number</Label>
                                            <Input
                                                value={editingCar.registrationNumber}
                                                onChange={(e) => setEditingCar({ ...editingCar, registrationNumber: e.target.value })}
                                                placeholder="e.g., DL1CAB1234"
                                            />
                                        </div>
                                        <div>
                                            <Label className='my-1'>Make</Label>
                                            <Input
                                                value={editingCar.make}
                                                onChange={(e) => setEditingCar({ ...editingCar, make: e.target.value })}
                                                placeholder="e.g., Toyota"
                                            />
                                        </div>
                                        <div>
                                            <Label className='my-1'>Model</Label>
                                            <Input
                                                value={editingCar.model}
                                                onChange={(e) => setEditingCar({ ...editingCar, model: e.target.value })}
                                                placeholder="e.g., Camry"
                                            />
                                        </div>
                                        <div>
                                            <Label className='my-1'>Year</Label>
                                            <Input
                                                type="number"
                                                value={editingCar.year}
                                                onChange={(e) => setEditingCar({ ...editingCar, year: parseInt(e.target.value) })}
                                            />
                                        </div>
                                        <div>
                                            <Label className='my-1'>Color</Label>
                                            <Input
                                                value={editingCar.color}
                                                onChange={(e) => setEditingCar({ ...editingCar, color: e.target.value })}
                                                placeholder="e.g., Pearl White"
                                            />
                                        </div>
                                    </div>

                                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                        <div>
                                            <Label className='my-1'>Category</Label>
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
                                            <Label className='my-1'>Type</Label>
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
                                            <Label className='my-1'>Fuel Type</Label>
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
                                            <Label className='my-1'>Transmission</Label>
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
                                            <Label className='my-1'>Seating Capacity</Label>
                                            <Input
                                                type="number"
                                                value={editingCar.seatingCapacity}
                                                onChange={(e) => setEditingCar({ ...editingCar, seatingCapacity: parseInt(e.target.value) })}
                                            />
                                        </div>
                                        <div>
                                            <Label className='my-1'>Luggage Capacity (L)</Label>
                                            <Input
                                                type="number"
                                                value={editingCar.luggageCapacity}
                                                onChange={(e) => setEditingCar({ ...editingCar, luggageCapacity: parseInt(e.target.value) })}
                                            />
                                        </div>
                                        <div>
                                            <Label className='my-1'>Status</Label>
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
                                            <Label className='my-1'>Price Per Day (₹)</Label>
                                            <Input
                                                type="number"
                                                value={editingCar.rentalPricePerDay}
                                                onChange={(e) => setEditingCar({ ...editingCar, rentalPricePerDay: parseInt(e.target.value) })}
                                            />
                                        </div>
                                        <div>
                                            <Label className='my-1'>Price Per Week (₹)</Label>
                                            <Input
                                                type="number"
                                                value={editingCar.rentalPricePerWeek}
                                                onChange={(e) => setEditingCar({ ...editingCar, rentalPricePerWeek: parseInt(e.target.value) })}
                                            />
                                        </div>
                                        <div>
                                            <Label className='my-1'>Price Per Month (₹)</Label>
                                            <Input
                                                type="number"
                                                value={editingCar.rentalPricePerMonth}
                                                onChange={(e) => setEditingCar({ ...editingCar, rentalPricePerMonth: parseInt(e.target.value) })}
                                            />
                                        </div>
                                    </div>

                                    <div>
                                        <Label className='my-1'>Maintenance Due Date</Label>
                                        <Input
                                            type="datetime-local"
                                            value={editingCar.maintenanceDueDate}
                                            onChange={(e) => setEditingCar({ ...editingCar, maintenanceDueDate: e.target.value })}
                                        />
                                    </div>

                                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                        <div>
                                            <Label className='my-1'>Insurance</Label>
                                            <Input
                                                value={editingCar.insurance}
                                                onChange={(e) => setEditingCar({ ...editingCar, insurance: e.target.value })}
                                                placeholder="e.g., Comprehensive insurance valid until Dec 2025"
                                            />
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <Label className='my-1'>Roadside Assistance</Label>
                                            <Switch
                                                checked={editingCar.roadsideAssistance}
                                                onCheckedChange={(checked) => setEditingCar({ ...editingCar, roadsideAssistance: checked })}
                                            />
                                        </div>
                                    </div>

                                    <div>
                                        <Label className='my-1'>Fuel Policy</Label>
                                        <Input
                                            value={editingCar.fuelPolicy}
                                            onChange={(e) => setEditingCar({ ...editingCar, fuelPolicy: e.target.value })}
                                            placeholder="e.g., Full-to-full policy, customer returns with same fuel level"
                                        />
                                    </div>

                                    <div>
                                        <Label className='my-1'>Features (comma separated)</Label>
                                        <Input
                                            value={editingCar.features}
                                            onChange={(e) => setEditingCar({ ...editingCar, features: e.target.value })}
                                            placeholder="e.g., GPS, Bluetooth, Sunroof, Heated Seats"
                                        />
                                    </div>

                                    <div>
                                        <Label className='my-1'>Important Points</Label>
                                        <textarea
                                            className="flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 min-h-[100px]"
                                            value={editingCar.importantPoints}
                                            onChange={(e) => setEditingCar({ ...editingCar, importantPoints: e.target.value })}
                                            placeholder="e.g., No smoking allowed, Pets allowed with fee, Must be 25+ to rent"
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