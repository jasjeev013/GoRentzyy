"use client"
import React, { useEffect, useState } from 'react'
import { LogOutIcon, Bell } from 'lucide-react'
import { Tabs, TabsList, TabsTrigger } from '../../../../components/ui/tabs'
import { Button } from '../../../../components/ui/button'
import { useAuth } from '../../../../hooks/useAuth'
import { redirect, useRouter } from 'next/navigation'
import RenterProfileEdit from '../../../components/dashboardComponents/RenterProfileEdit'
import RenterBookingDetails from '../../../components/dashboardComponents/RenterBookingDetails'
import RenterDashboard from '../../../components/dashboardComponents/RenterDashBoard'
import RenterNotificationTimeline from '../../../components/dashboardComponents/RenterNotificationTimeline'
import { useAuthStore } from '../../../../stores/authStore'
import { useBookingStore } from '../../../../stores/bookingStore'

const DashboardPage = () => {
  const router = useRouter();
  const { logout } = useAuth();
  const { userData } = useAuthStore();
  const [activeTab, setActiveTab] = useState('home')
  const [showNotifications, setShowNotifications] = useState(false);
  const { renterBookings, fetchRenterBookings } = useBookingStore();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBookings = async () => {
      try {
        setLoading(true);
        await fetchRenterBookings();
      } catch (error) {
        console.error("Error fetching bookings:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchBookings();
  }, []);

  if (loading) {
    return (
      <div className="w-full min-h-screen flex items-center justify-center">
        <div className="w-[calc(100%-18rem)] min-h-screen p-8">
          {/* Loading Skeleton */}
          <div className="animate-pulse space-y-6">
            {/* Header */}
            <div className="flex justify-between items-center">
              <div className="h-10 w-64 bg-gray-700 rounded"></div>
              <div className="flex space-x-4">
                <div className="h-10 w-10 bg-gray-700 rounded-full"></div>
                <div className="h-10 w-10 bg-gray-700 rounded-full"></div>
              </div>
            </div>
            
            {/* Tabs */}
            <div className="flex space-x-4">
              {['home', 'bookings', 'profile'].map((tab) => (
                <div key={tab} className="h-10 w-24 bg-gray-700 rounded-md"></div>
              ))}
            </div>
            
            {/* Dashboard Content */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {[...Array(3)].map((_, i) => (
                <div key={i} className="h-32 bg-gray-800 rounded-lg"></div>
              ))}
            </div>
            
            <div className="h-80 bg-gray-800 rounded-lg"></div>
          </div>
        </div>
      </div>
    )
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
                <Bell className="h-5 w-5" />
              </Button>
              <Button variant="ghost" size="icon" className="rounded-full" onClick={() => {
                logout()
                router.push('/login')
              }}>
                <LogOutIcon className="h-5 w-5" />
              </Button>
            </div>
          </nav>

          {showNotifications && (
            <RenterNotificationTimeline onClose={() => setShowNotifications(false)} />
          )}

          {/* Navigation Tabs */}
          <div className="px-4">
            <Tabs defaultValue="home" className="w-full" onValueChange={(value) => setActiveTab(value)}>
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
              <RenterDashboard renterBookings={renterBookings} />
            )}
            {activeTab === 'bookings' && <RenterBookingDetails />}
            {activeTab === 'profile' && <RenterProfileEdit />}
          </div>
        </div>
      </div>
    </div>
  )
}

export default DashboardPage