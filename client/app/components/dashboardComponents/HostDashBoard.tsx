import React from 'react'
import { CarIcon, UsersIcon, GaugeIcon, CalendarCheckIcon, TrendingUpIcon, TrendingDownIcon } from 'lucide-react'
import { Avatar, AvatarFallback, AvatarImage } from '../../../components/ui/avatar'
import { Badge } from '../../../components/ui/badge'
import { Card, CardHeader, CardTitle, CardContent } from '../../../components/ui/card'
import { Button } from '../../../components/ui/button'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'

const HostDashBoard = ({ hostCars, hostBookings }) => {
    // Calculate metrics from the data
    const totalCars = hostCars?.length || 0;
    const totalBookings = hostBookings?.length || 0;
    const availableCars = hostCars?.filter(car => car.availabilityStatus === 'AVAILABLE').length || 0;

    // Calculate monthly income
    const monthlyIncome = hostBookings?.reduce((sum, booking) => sum + (booking.totalPrice || 0), 0) || 0;

    // Prepare chart data from bookings
    const prepareChartData = () => {
        const last7Days = [...Array(7)].map((_, i) => {
            const date = new Date();
            date.setDate(date.getDate() - i);
            return date.toISOString().split('T')[0];
        }).reverse();

        return last7Days.map(date => {
            const dayBookings = hostBookings?.filter(booking =>
                booking.startDate?.includes(date)
            ) || [];

            return {
                name: new Date(date).toLocaleDateString('en-US', { weekday: 'short' }),
                bookings: dayBookings.length,
                income: dayBookings.reduce((sum, b) => sum + (b.totalPrice || 0), 0)
            };
        });
    };

    // Get top 4 cars by bookings
    const getPopularCars = () => {
        if (!hostCars || !hostBookings) return [];

        const carBookingsCount = {};
        hostBookings.forEach(booking => {
            const carId = booking.car?.carId;
            if (carId) {
                carBookingsCount[carId] = (carBookingsCount[carId] || 0) + 1;
            }
        });

        return hostCars
            .map(car => ({
                ...car,
                bookingCount: carBookingsCount[car.carId] || 0
            }))
            .sort((a, b) => b.bookingCount - a.bookingCount)
            .slice(0, 4);
    };

    const chartData = prepareChartData();
    const popularCars = getPopularCars();

    return (
        <>
            {/* Metrics Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
                <MetricCard
                    title="Total Cars"
                    value={`${totalCars}`}
                    icon={<CarIcon className="h-6 w-6" />}
                    color="bg-blue-500"
                />
                <MetricCard
                    title="Available Cars"
                    value={`${availableCars}`}
                    icon={<CarIcon className="h-6 w-6" />}
                    color="bg-green-500"
                />
                <MetricCard
                    title="Total Bookings"
                    value={`${totalBookings}`}
                    icon={<CalendarCheckIcon className="h-6 w-6" />}
                    color="bg-purple-500"
                />
                <MetricCard
                    title="Monthly Income"
                    value={`$${(monthlyIncome / 100).toLocaleString()}`}
                    icon={<GaugeIcon className="h-6 w-6" />}
                    color="bg-yellow-500"
                />
            </div>

            {/* Financial Summary */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 mb-6">
                <FinancialCard
                    title="Income"
                    value={`$${(monthlyIncome / 100).toLocaleString()}`}
                    change="+2.5%"
                    isPositive={true}
                    comparison="vs last month"
                />
                <FinancialCard
                    title="Occupancy Rate"
                    value={`${totalBookings > 0 ? Math.round((totalBookings / totalCars) * 100) : 0}%`}
                    change={totalBookings > 5 ? "+1.5%" : "-0.5%"}
                    isPositive={totalBookings > 5}
                    comparison="vs last month"
                />
            </div>

      // In the Chart Section of HostDashBoard.tsx
            <div className="bg-gray-800 rounded-lg p-4 mb-6">
                <h2 className="text-xl font-bold mb-4">Bookings last 7 days</h2>
                <div className="h-80">
                    <ResponsiveContainer width="100%" height="100%">
                        <LineChart data={chartData}>
                            <CartesianGrid strokeDasharray="3 3" stroke="#4b5563" />
                            <XAxis dataKey="name" stroke="#9ca3af" />
                            <YAxis yAxisId="left" stroke="#9ca3af" />
                            <YAxis yAxisId="right" orientation="right" stroke="#9ca3af" />
                            <Tooltip contentStyle={{ backgroundColor: '#1f2937', borderColor: '#374151' }} />
                            <Legend />
                            <Line
                                type="monotone"
                                dataKey="bookings"
                                name="Bookings"
                                stroke="#a855f7"
                                strokeWidth={2}
                                activeDot={{ r: 8 }}
                                yAxisId="left"
                            />
                            <Line
                                type="monotone"
                                dataKey="income"
                                name="Income ($)"
                                stroke="#10b981"
                                strokeWidth={2}
                                yAxisId="right"
                            />
                        </LineChart>
                    </ResponsiveContainer>
                </div>
            </div>

            {/* Most Popular Cars */}
            <div className="mb-6">
                <h2 className="text-xl font-bold mb-4">Most Popular Cars</h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                    {popularCars.map((car) => (
                        <CarCard key={car.carId} car={car} />
                    ))}
                </div>
            </div>
        </>
    )
}

// Metric Card Component (unchanged)
const MetricCard = ({ title, value, icon, color }) => {
    return (
        <Card className="bg-gray-800 border-gray-700">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
                <CardTitle className="text-sm font-medium text-gray-400">{title}</CardTitle>
                <div className={`p-2 rounded-full ${color}`}>{icon}</div>
            </CardHeader>
            <CardContent>
                <div className="text-2xl font-bold">{value}</div>
            </CardContent>
        </Card>
    )
}

// Financial Card Component (unchanged)
const FinancialCard = ({ title, value, change, isPositive, comparison }) => {
    return (
        <Card className="bg-gray-800 border-gray-700">
            <CardHeader className="pb-2">
                <CardTitle className="text-sm font-medium text-gray-400">{title}</CardTitle>
            </CardHeader>
            <CardContent>
                <div className="text-3xl font-bold mb-2">{value}</div>
                <div className="flex items-center">
                    {isPositive ? (
                        <TrendingUpIcon className="h-4 w-4 text-green-500 mr-1" />
                    ) : (
                        <TrendingDownIcon className="h-4 w-4 text-red-500 mr-1" />
                    )}
                    <span className={`text-sm ${isPositive ? 'text-green-500' : 'text-red-500'}`}>
                        {change}
                    </span>
                    <span className="text-sm text-gray-400 ml-2">{comparison}</span>
                </div>
            </CardContent>
        </Card>
    )
}

// Updated Car Card Component
const CarCard = ({ car }) => {
    return (
        <Card className="bg-gray-800 border-gray-700 hover:border-gray-600 transition-colors">
            <CardContent className="p-4">
                <div className="flex flex-col items-center">
                    <Avatar className="w-24 h-24 mb-3">
                        <AvatarImage src={car.photos?.[0]} />
                        <AvatarFallback>{car.name?.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <h3 className="font-bold text-lg mb-1">{car.name}</h3>
                    <Badge
                        variant={car.availabilityStatus === 'AVAILABLE' ? 'default' : 'secondary'}
                        className="mb-2"
                    >
                        {car.availabilityStatus || 'N/A'}
                    </Badge>
                    <p className="text-gray-400 text-sm">
                        ${car.rentalPricePerDay} per day
                    </p>
                    <p className="text-gray-400 text-sm">
                        {car.bookingCount || 0} bookings
                    </p>
                    <Button variant="outline" className="mt-3 w-full">
                        View Details
                    </Button>
                </div>
            </CardContent>
        </Card>
    )
}

export default HostDashBoard