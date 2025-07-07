import React from 'react'
import { CarIcon, CalendarCheckIcon, DollarSign, MapPin } from 'lucide-react'
import { Card, CardHeader, CardTitle, CardContent } from '../../../components/ui/card'
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

const RenterDashboard = ({ renterBookings }) => {
  // Calculate metrics from bookings
  const totalBookings = renterBookings?.length || 0;
  const upcomingBookings = renterBookings?.filter(booking => 
    new Date(booking.startDate) > new Date()
  ).length || 0;
  const totalSpent = renterBookings?.reduce((sum, booking) => 
    sum + (booking.totalPrice || 0), 0
  ) / 100 || 0; // Assuming price is in cents

  // Prepare chart data
  const prepareChartData = () => {
    if (!renterBookings) return [];
    
    const last6Months = [...Array(6)].map((_, i) => {
      const date = new Date();
      date.setMonth(date.getMonth() - i);
      return date.toISOString().slice(0, 7); // YYYY-MM format
    }).reverse();

    return last6Months.map(month => {
      const monthBookings = renterBookings.filter(booking => 
        booking.startDate?.includes(month)
      );
      
      return {
        name: new Date(`${month}-01`).toLocaleDateString('en-US', { month: 'short' }),
        bookings: monthBookings.length,
        spending: monthBookings.reduce((sum, b) => sum + (b.totalPrice || 0), 0) / 100
      };
    });
  };

  const chartData = prepareChartData();

  return (
    <>
      {/* Metrics Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
        <MetricCard
          title="Total Bookings"
          value={totalBookings.toString()}
          icon={<CalendarCheckIcon className="h-6 w-6" />}
          color="bg-blue-500"
        />
        <MetricCard
          title="Upcoming Trips"
          value={upcomingBookings.toString()}
          icon={<MapPin className="h-6 w-6" />}
          color="bg-green-500"
        />
        <MetricCard
          title="Total Spent"
          value={`$${totalSpent.toLocaleString()}`}
          icon={<DollarSign className="h-6 w-6" />}
          color="bg-purple-500"
        />
      </div>

      {/* Chart Section */}
      <div className="bg-gray-800 rounded-lg p-4 mb-6">
        <h2 className="text-xl font-bold mb-4">Your Rental Activity</h2>
        <div className="h-80">
          {chartData.length > 0 ? (
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#4b5563" />
                <XAxis dataKey="name" stroke="#9ca3af" />
                <YAxis yAxisId="left" stroke="#9ca3af" />
                <YAxis yAxisId="right" orientation="right" stroke="#9ca3af" />
                <Tooltip contentStyle={{ backgroundColor: '#1f2937', borderColor: '#374151' }} />
                <Legend />
                <Line
                  yAxisId="left"
                  type="monotone"
                  dataKey="bookings"
                  name="Bookings"
                  stroke="#a855f7"
                  strokeWidth={2}
                  activeDot={{ r: 8 }}
                />
                <Line
                  yAxisId="right"
                  type="monotone"
                  dataKey="spending"
                  name="Spending ($)"
                  stroke="#10b981"
                  strokeWidth={2}
                />
              </LineChart>
            </ResponsiveContainer>
          ) : (
            <div className="h-full flex items-center justify-center text-gray-400">
              No booking data available
            </div>
          )}
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

export default RenterDashboard