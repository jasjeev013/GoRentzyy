"use client";
import React, { useEffect, useState } from 'react';
import { Search, ChevronDown, MoreVertical } from 'lucide-react';
import { Input } from '../../../components/ui/input';
import { Button } from '../../../components/ui/button';
import { Card, CardHeader, CardTitle, CardContent } from '../../../components/ui/card';
import { Badge } from '../../../components/ui/badge';
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from '../../../components/ui/select';
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from '../../../components/ui/table';
import { Avatar, AvatarImage, AvatarFallback } from '../../../components/ui/avatar';
import { format } from 'date-fns';
import { useBookingStore } from '../../../stores/bookingStore';

const paymentMethods = ["CREDIT_CARD", "PAYPAL", "UPI", "OTHER"];
const paymentStatuses = ["SUCCESSFUL", "FAILED", "PENDING"];

const BookingManagementTable = () => {

  const { hostBookings, fetchHostBookings } = useBookingStore();


  const [searchTerm, setSearchTerm] = useState('');
  const [paymentMethodFilter, setPaymentMethodFilter] = useState('');
  const [paymentStatusFilter, setPaymentStatusFilter] = useState('');

  useEffect(() => {
    fetchHostBookings();
    setBookings(hostBookings);
  }, [fetchHostBookings]);

  const [bookings, setBookings] = useState([]);

  const filteredBookings = bookings.length != 0 ? bookings

  .filter(booking => {
    const matchesSearch =
      booking.car.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      booking.car.registrationNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
      booking.renter.fullName.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesPaymentMethod = paymentMethodFilter ? booking.paymentMethod === paymentMethodFilter : true;
    const matchesPaymentStatus = paymentStatusFilter ? booking.paymentStatus === paymentStatusFilter : true;
    return matchesSearch && matchesPaymentMethod && matchesPaymentStatus;
  })
  : [];



const formatDateTime = (dateTimeString: string) => {
  const date = new Date(dateTimeString);
  return format(date, 'MMM dd, yyyy hh:mm a');
};

const getPaymentStatusColor = (status: string) => {
  switch (status) {
    case "SUCCESSFUL": return "bg-green-500 hover:bg-green-600";
    case "FAILED": return "bg-red-500 hover:bg-red-600";
    case "PENDING": return "bg-yellow-500 hover:bg-yellow-600";
    default: return "bg-gray-500 hover:bg-gray-600";
  }
};

return (
  <div className="space-y-6">
    {/* Filter/Search Row */}
    <div className="bg-gray-800 rounded-lg p-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="flex flex-col sm:flex-row gap-4 w-full md:w-auto">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
            <Input
              placeholder="Search bookings..."
              className="pl-10 w-full"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <Select value={paymentMethodFilter} onValueChange={setPaymentMethodFilter}>
            <SelectTrigger className="w-full sm:w-[180px]">
              <SelectValue placeholder="Payment Method" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value=" ">All Methods</SelectItem>
              {paymentMethods.map(method => (
                <SelectItem key={method} value={method}>{method}</SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Select value={paymentStatusFilter} onValueChange={setPaymentStatusFilter}>
            <SelectTrigger className="w-full sm:w-[180px]">
              <SelectValue placeholder="Payment Status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value=" ">All Statuses</SelectItem>
              {paymentStatuses.map(status => (
                <SelectItem key={status} value={status}>{status}</SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>
    </div>

    {/* Desktop Table View */}
    <div className="hidden md:block">
      <div className="bg-gray-800 rounded-lg overflow-hidden">
        <Table className="min-w-full">
          <TableHeader className="bg-gray-700">
            <TableRow>
              <TableHead className="text-gray-300">S.No</TableHead>
              <TableHead className="text-gray-300">Car</TableHead>
              <TableHead className="text-gray-300">Renter</TableHead>
              <TableHead className="text-gray-300">Start Date</TableHead>
              <TableHead className="text-gray-300">End Date</TableHead>
              <TableHead className="text-gray-300">Total Price</TableHead>
              <TableHead className="text-gray-300">Payment Method</TableHead>
              <TableHead className="text-gray-300">Status</TableHead>
              <TableHead className="text-gray-300"></TableHead>
            </TableRow>
          </TableHeader>
          <TableBody className="divide-y divide-gray-700">
            {filteredBookings.length != 0 && filteredBookings.map((booking, index) => (
              <TableRow key={booking.bookingId} className="hover:bg-gray-700">
                <TableCell className="text-gray-300">{index + 1}</TableCell>
                <TableCell>
                  <div className="font-medium text-white">{booking.car.name}</div>
                  <div className="text-sm text-gray-400">{booking.car.registrationNumber}</div>
                </TableCell>
                <TableCell>
                  <div className="flex items-center gap-3">
                    <Avatar className="h-8 w-8">
                      <AvatarImage src={booking.renter.profilePicture} />
                      <AvatarFallback>{booking.renter.fullName.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <span>{booking.renter.fullName}</span>
                  </div>
                </TableCell>
                <TableCell className="text-gray-300">{formatDateTime(booking.startDate)}</TableCell>
                <TableCell className="text-gray-300">{formatDateTime(booking.endDate)}</TableCell>
                <TableCell className="text-gray-300">₹{booking.totalPrice}</TableCell>
                <TableCell className="text-gray-300">CREDIT_CARD</TableCell>
                <TableCell>
                  <Badge className={`${getPaymentStatusColor(booking?.payment?.paymentStatus ?? "PENDING")}`}>
                    {booking?.payment?.paymentStatus ?? "PENDING"}
                  </Badge>
                </TableCell>
                <TableCell>
                  <Button variant="ghost" size="icon">
                    <MoreVertical className="h-4 w-4" />
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>

    {/* Mobile Card View */}
    <div className="md:hidden space-y-4">
      {filteredBookings.length != 0 && filteredBookings.map((booking, index) => (
        <Card key={index} className="bg-gray-800 border-gray-700">
          <CardHeader className="flex flex-row items-start justify-between pb-4">
            <div>
              <CardTitle className="text-lg">{booking.car.name}</CardTitle>
              <div className="text-sm text-gray-400">{booking.car.registrationNumber}</div>
            </div>
            <Badge className={`${getPaymentStatusColor("SUCCESSFUL")}`}>
              SUCCESSFUL
            </Badge>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center gap-3">
              <Avatar className="h-10 w-10">
                <AvatarImage src={booking.renter.profilePhoto} />
                <AvatarFallback>{booking.renter.fullName.charAt(0)}</AvatarFallback>
              </Avatar>
              <div>
                <div className="font-medium">{booking.renter.fullName}</div>
                <div className="text-sm text-gray-400">"CREDIT_CARD</div>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <div className="text-sm text-gray-400">Start Date</div>
                <div>{formatDateTime(booking.startDate)}</div>
              </div>
              <div>
                <div className="text-sm text-gray-400">End Date</div>
                <div>{formatDateTime(booking.endDate)}</div>
              </div>
            </div>

            <div className="flex justify-between items-center pt-2 border-t border-gray-700">
              <div>
                <div className="text-sm text-gray-400">Total Price</div>
                <div className="font-medium">₹{booking.totalPrice}</div>
              </div>
              <Button variant="ghost" size="icon">
                <MoreVertical className="h-4 w-4" />
              </Button>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  </div>
);
};

export default BookingManagementTable;