"use client"
import React, { useEffect, useState } from 'react'
import { BellIcon, LogOutIcon } from 'lucide-react'
import { Tabs, TabsList, TabsTrigger } from '../../../../components/ui/tabs'
import { Button } from '../../../../components/ui/button'
import { useAuth } from '../../../../hooks/useAuth'
import { redirect } from 'next/navigation'
import CarManagementTable from '../../../components/dashboardComponents/CarManagementTable'
import BookingManagementTable from '../../../components/dashboardComponents/BookingManagementTable'
import HostProfileEdit from '../../../components/dashboardComponents/HostProfileEdit'
import HostDashBoard from '../../../components/dashboardComponents/HostDashBoard'
import HostNotificationTimeline from '../../../components/dashboardComponents/HostNotificationTimeline'
import { useCarStore } from '../../../../stores/carStore'
import { useBookingStore } from '../../../../stores/bookingStore'
import SkeletonHostDashboard from '../../../components/dashboardComponents/SkeletonHostDashboard'


const DashboardPage = () => {
  const { hostCars, fetchAllCarsOfHost } = useCarStore();
  const { hostBookings, fetchHostBookings } = useBookingStore();
  const { userData, logout } = useAuth();
  const [activeTab, setActiveTab] = useState('home')
  const [showNotifications, setShowNotifications] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        await fetchAllCarsOfHost();
        await fetchHostBookings();
      } catch (error) {
        console.error("Error fetching data:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [])

  if (loading) {
    <SkeletonHostDashboard/>
  }

  return (
    <div className="w-full min-h-screen flex items-center justify-center">
      <div className="w-[calc(100%-18rem)] min-h-screen">
        <div className="min-h-screen text-white">
          {/* Top Navigation Bar */}
          <nav className="p-4 flex justify-between items-center">
            <div className="flex items-center space-x-4">
              <h1 className="text-2xl md:text-3xl font-bold">Hey {userData?.fullName}</h1>
            </div>
            <div className="flex items-center space-x-4">
              <Button variant="ghost" size="icon" className="rounded-full" onClick={() => setShowNotifications(!showNotifications)}>
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

          {showNotifications && (
            <HostNotificationTimeline onClose={() => setShowNotifications(false)} />
          )}

          {/* Navigation Tabs */}
          <div className="px-4">
            <Tabs defaultValue="home" className="w-full" onValueChange={(value) => setActiveTab(value)}>
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
              <HostDashBoard hostCars={hostCars} hostBookings={hostBookings} />
            )}
            {activeTab === 'cars' && <CarManagementTable />}
            {activeTab === 'bookings' && <BookingManagementTable />}
            {activeTab === 'profile' && <HostProfileEdit />}
          </div>
        </div>
      </div>
    </div>
  )
}

export default DashboardPage