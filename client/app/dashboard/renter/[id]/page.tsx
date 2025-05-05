"use client"

import React, { useState } from 'react'
import {
  BellIcon,
  LogOutIcon,
  CarIcon,
  UsersIcon,
  GaugeIcon,
  CalendarCheckIcon,
  TrendingUpIcon,
  TrendingDownIcon
} from 'lucide-react'
import { Card, CardHeader, CardTitle, CardContent, CardDescription } from '../../../../components/ui/card'
import { Tabs, TabsList, TabsTrigger } from '../../../../components/ui/tabs'
import { Button } from '../../../../components/ui/button'
import { Avatar, AvatarFallback, AvatarImage } from '../../../../components/ui/avatar'
import { Badge } from '../../../../components/ui/badge'
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
import { useAuth } from '../../../../hooks/useAuth'
import { redirect } from 'next/navigation'

const data = [
  { name: 'Mon', reserved: 12, rental: 8, done: 10 },
  { name: 'Tue', reserved: 19, rental: 12, done: 14 },
  { name: 'Wed', reserved: 15, rental: 10, done: 12 },
  { name: 'Thu', reserved: 18, rental: 14, done: 16 },
  { name: 'Fri', reserved: 24, rental: 18, done: 20 },
  { name: 'Sat', reserved: 30, rental: 22, done: 25 },
  { name: 'Sun', reserved: 28, rental: 20, done: 23 },
]

const popularCars = [
  { id: 1, name: 'Nissan Ariya', status: 'Available', units: 12, image: '/nissan-ariya.jpg' },
  { id: 2, name: 'Tesla Model 3', status: 'Available', units: 8, image: '/tesla-model3.jpg' },
  { id: 3, name: 'Toyota RAV4', status: 'Low Stock', units: 3, image: '/toyota-rav4.jpg' },
  { id: 4, name: 'Honda CR-V', status: 'Available', units: 10, image: '/honda-crv.jpg' },
]

const DashboardPage = () => {
  const { logout } = useAuth();
  const [activeTab, setActiveTab] = useState('home')
  return (
    <>

      <div className="w-full min-h-screen flex items-center justify-center">
        <div className="w-[calc(100%-18rem)] min-h-screen">
          <div className="min-h-screen text-white">
            {/* Top Navigation Bar - Modified */}
            <nav className="p-4 flex justify-between items-center">
              <div className="flex items-center space-x-4">
                <h1 className="text-2xl md:text-3xl font-bold">Hey Jasjeev Singh Kohli Renter</h1>
              </div>
              <div className="flex items-center space-x-4">
                <Button variant="ghost" size="icon" className="rounded-full">
                  <BellIcon className="h-5 w-5" />
                </Button>
                <Button variant="ghost" size="icon" className="rounded-full" onClick={() => {
                  logout()
                  redirect('/home')
                }}>
                  <LogOutIcon className="h-5 w-5" />
                </Button>
              </div>
            </nav>

            {/* Navigation Tabs */}
            <div className="px-4">
              <Tabs
                defaultValue="home"
                className="w-full"
                onValueChange={(value) => setActiveTab(value)}
              >
                <TabsList className="w-auto">
                  <TabsTrigger value="home" className="data-[state=active]:bg-black">Home</TabsTrigger>
                  <TabsTrigger value="bookings" className="data-[state=active]:bg-black">Bookings</TabsTrigger>
                  <TabsTrigger value="profile" className="data-[state=active]:bg-black">Edit Profile</TabsTrigger>
                </TabsList>
              </Tabs>
            </div>

            {/* Tab Contents */}
            <div className="container mx-auto p-4 md:p-6">
              {activeTab === 'home' && (
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

                </>
              )}

              {activeTab === 'bookings' && (
                <div className="bg-gray-800 rounded-lg p-6">
                  <h2 className="text-2xl font-bold mb-6">Bookings Management</h2>
                  <div className="grid gap-6">
                    <div className="bg-gray-700 p-4 rounded-lg">
                      <h3 className="text-xl font-semibold mb-4">Current Bookings</h3>
                      <p className="text-gray-300">All active bookings will be displayed here</p>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="bg-gray-700 p-4 rounded-lg">
                        <h3 className="text-lg font-semibold mb-2">Booking Calendar</h3>
                        <Button className="w-full">View Calendar</Button>
                      </div>
                      <div className="bg-gray-700 p-4 rounded-lg">
                        <h3 className="text-lg font-semibold mb-2">Booking Reports</h3>
                        <Button variant="outline" className="w-full">Generate Reports</Button>
                      </div>
                    </div>
                  </div>
                </div>
              )}

              {activeTab === 'profile' && (
                <div className="bg-gray-800 rounded-lg p-6">
                  <h2 className="text-2xl font-bold mb-6">Edit Profile</h2>
                  <div className="grid gap-6">
                    <div className="bg-gray-700 p-4 rounded-lg">
                      <h3 className="text-xl font-semibold mb-4">Personal Information</h3>
                      <div className="space-y-4">
                        <div>
                          <label className="block text-gray-300 mb-1">Full Name</label>
                          <input
                            type="text"
                            className="w-full bg-gray-600 rounded px-3 py-2 text-white"
                            defaultValue="Jasjeev Singh Kohli"
                          />
                        </div>
                        <div>
                          <label className="block text-gray-300 mb-1">Email</label>
                          <input
                            type="email"
                            className="w-full bg-gray-600 rounded px-3 py-2 text-white"
                            defaultValue="jasjeev@example.com"
                          />
                        </div>
                      </div>
                    </div>
                    <div className="bg-gray-700 p-4 rounded-lg">
                      <h3 className="text-xl font-semibold mb-4">Security</h3>
                      <Button className="w-full">Change Password</Button>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
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

export default DashboardPage