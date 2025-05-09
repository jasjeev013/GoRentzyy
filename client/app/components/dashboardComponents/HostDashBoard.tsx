import React from 'react'
import {
    CarIcon,
    UsersIcon,
    GaugeIcon,
    CalendarCheckIcon,

} from 'lucide-react'
import { Avatar, AvatarFallback, AvatarImage } from '../../../components/ui/avatar'
import { Badge } from '../../../components/ui/badge'
import { Card, CardHeader, CardTitle, CardContent } from '../../../components/ui/card'
import { Button } from '../../../components/ui/button'
import {

    TrendingUpIcon,
    TrendingDownIcon
} from 'lucide-react'
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer
} from 'recharts'
const HostDashBoard = ({ data, popularCars }) => {
    return (
        <>
            {/* Metrics Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4 mb-6">
                <MetricCard
                    title="Total Cars"
                    value="750+"
                    icon={<CarIcon className="h-6 w-6" />}
                    color="bg-blue-500"
                />
                <MetricCard
                    title="Daily Trips"
                    value="1697+"
                    icon={<CalendarCheckIcon className="h-6 w-6" />}
                    color="bg-green-500"
                />
                <MetricCard
                    title="Clients Annually"
                    value="85k+"
                    icon={<UsersIcon className="h-6 w-6" />}
                    color="bg-purple-500"
                />
                <MetricCard
                    title="Kilometers Daily"
                    value="2167+"
                    icon={<GaugeIcon className="h-6 w-6" />}
                    color="bg-yellow-500"
                />
                <MetricCard
                    title="Total Cars"
                    value="750+"
                    icon={<CarIcon className="h-6 w-6" />}
                    color="bg-red-500"
                />
            </div>

            {/* Financial Summary */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 mb-6">
                <FinancialCard
                    title="Income"
                    value="$9460.00"
                    change="-1.5%"
                    isPositive={false}
                    comparison="vs yesterday"
                />
                <FinancialCard
                    title="Expenses"
                    value="$5660.00"
                    change="+2.5%"
                    isPositive={true}
                    comparison="vs last week"
                />
            </div>

            {/* Chart Section */}
            <div className="bg-gray-800 rounded-lg p-4 mb-6">
                <h2 className="text-xl font-bold mb-4">Rentals last week</h2>
                <div className="h-80">
                    <ResponsiveContainer width="100%" height="100%">
                        <LineChart data={data}>
                            <CartesianGrid strokeDasharray="3 3" stroke="#4b5563" />
                            <XAxis dataKey="name" stroke="#9ca3af" />
                            <YAxis stroke="#9ca3af" />
                            <Tooltip
                                contentStyle={{ backgroundColor: '#1f2937', borderColor: '#374151' }}
                            />
                            <Legend />
                            <Line
                                type="monotone"
                                dataKey="reserved"
                                stroke="#a855f7"
                                strokeWidth={2}
                                activeDot={{ r: 8 }}
                            />
                            <Line
                                type="monotone"
                                dataKey="rental"
                                stroke="#9ca3af"
                                strokeWidth={2}
                            />
                            <Line
                                type="monotone"
                                dataKey="done"
                                stroke="#10b981"
                                strokeWidth={2}
                            />
                        </LineChart>
                    </ResponsiveContainer>
                </div>
            </div>

            {/* Most Demanded Cars */}
            <div className="mb-6">
                <h2 className="text-xl font-bold mb-4">Most Demanded Cars</h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                    {popularCars.map((car) => (
                        <CarCard key={car.id} car={car} />
                    ))}
                </div>
            </div>
        </>
    )
}

// Metric Card Component
const MetricCard = ({ title, value, icon, color }: { title: string, value: string, icon: React.ReactNode, color: string }) => {
    return (
        <Card className="bg-gray-800 border-gray-700">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
                <CardTitle className="text-sm font-medium text-gray-400">{title}</CardTitle>
                <div className={`p-2 rounded-full ${color}`}>
                    {icon}
                </div>
            </CardHeader>
            <CardContent>
                <div className="text-2xl font-bold">{value}</div>
            </CardContent>
        </Card>
    )
}

// Financial Card Component
const FinancialCard = ({ title, value, change, isPositive, comparison }: {
    title: string,
    value: string,
    change: string,
    isPositive: boolean,
    comparison: string
}) => {
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

// Car Card Component
const CarCard = ({ car }: { car: { name: string, status: string, units: number, image: string } }) => {
    return (
        <Card className="bg-gray-800 border-gray-700 hover:border-gray-600 transition-colors">
            <CardContent className="p-4">
                <div className="flex flex-col items-center">
                    <Avatar className="w-24 h-24 mb-3">
                        <AvatarImage src={car.image} />
                        <AvatarFallback>{car.name.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <h3 className="font-bold text-lg mb-1">{car.name}</h3>
                    <Badge
                        variant={car.status === 'Available' ? 'default' : 'secondary'}
                        className="mb-2"
                    >
                        {car.status}
                    </Badge>
                    <p className="text-gray-400 text-sm">{car.units} units available</p>
                    <Button variant="outline" className="mt-3 w-full">
                        View Details
                    </Button>
                </div>
            </CardContent>
        </Card>
    )
}

export default HostDashBoard
