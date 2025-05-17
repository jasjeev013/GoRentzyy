"use client"

import React, { useState } from 'react'
import {
  BellIcon,
  LogOutIcon,

} from 'lucide-react'
import { Tabs, TabsList, TabsTrigger } from '../../../../components/ui/tabs'
import { Button } from '../../../../components/ui/button'

import { useAuth } from '../../../../hooks/useAuth'
import { redirect } from 'next/navigation'
import CarManagementTable from '../../../components/dashboardComponents/CarManagementTable'
import BookingManagementTable from '../../../components/dashboardComponents/BookingManagementTable'
import HostProfileEdit from '../../../components/dashboardComponents/HostProfileEdit'
import HostDashBoard from '../../../components/dashboardComponents/HostDashBoard'

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
  const { userData,logout } = useAuth();
  const [activeTab, setActiveTab] = useState('home')



  return (
    <>

      <div className="w-full min-h-screen flex items-center justify-center">
        <div className="w-[calc(100%-18rem)] min-h-screen">
          <div className="min-h-screen text-white">
            {/* Top Navigation Bar - Modified */}
            <nav className="p-4 flex justify-between items-center">
              <div className="flex items-center space-x-4">
                <h1 className="text-2xl md:text-3xl font-bold">Hey {userData?.fullName}</h1>
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
                  <TabsTrigger value="cars" className="data-[state=active]:bg-black">Cars</TabsTrigger>
                  <TabsTrigger value="bookings" className="data-[state=active]:bg-black">Bookings</TabsTrigger>
                  <TabsTrigger value="profile" className="data-[state=active]:bg-black">Edit Profile</TabsTrigger>
                </TabsList>
              </Tabs>
            </div>

            {/* Tab Contents */}
            <div className="container mx-auto p-4 md:p-6">
              {activeTab === 'home' && (
                <HostDashBoard data={data} popularCars={popularCars} />
              )}

              {activeTab === 'cars' && (
                <CarManagementTable />
              )}

              {activeTab === 'bookings' && (
                <BookingManagementTable />
              )}

              {activeTab === 'profile' && (
                <HostProfileEdit />
              )}
            </div>
          </div>
        </div>
      </div>

    </>
  )
}



export default DashboardPage