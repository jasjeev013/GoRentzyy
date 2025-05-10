"use client";
import React, { useState } from 'react';
import { Camera, Check, X, Mail, Phone } from 'lucide-react';
import { Input } from '../../../components/ui/input';
import { Button } from '../../../components/ui/button';
import { Label } from '../../../components/ui/label';
import { Avatar, AvatarImage, AvatarFallback } from '../../../components/ui/avatar';
import { Badge } from '../../../components/ui/badge';
import { Textarea } from '../../../components/ui/textarea';
import { Card, CardHeader, CardTitle, CardContent } from '../../../components/ui/card';

const RenterProfileEdit = () => {
  const [userData, setUserData] = useState({
    name: 'Jasjeev Singh Kohli',
    address: '123 Main St, New Delhi, India',
    organization: 'CarRental Pro',
    phone: '+919876543210',
    email: 'jasjeev@example.com',
    phoneVerified: false,
    emailVerified: false,
    profileImage: '/profile.jpg'
  });

  const [originalData] = useState({ ...userData });
  const [isEditingPhone, setIsEditingPhone] = useState(false);
  const [isEditingEmail, setIsEditingEmail] = useState(false);
  const [phoneOTP, setPhoneOTP] = useState('');
  const [emailOTP, setEmailOTP] = useState('');
  const [file, setFile] = useState<File | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setUserData(prev => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const selectedFile = e.target.files[0];
      setFile(selectedFile);
      setUserData(prev => ({
        ...prev,
        profileImage: URL.createObjectURL(selectedFile)
      }));
    }
  };

  const handleVerifyPhone = () => {
    setIsEditingPhone(true);
    // In a real app, you would call an API to send OTP here
    console.log('OTP sent to phone');
  };

  const handleVerifyEmail = () => {
    setIsEditingEmail(true);
    // In a real app, you would call an API to send OTP here
    console.log('OTP sent to email');
  };

  const confirmPhoneVerification = () => {
    // In a real app, you would verify OTP with backend
    setUserData(prev => ({ ...prev, phoneVerified: true }));
    setIsEditingPhone(false);
    setPhoneOTP('');
  };

  const confirmEmailVerification = () => {
    // In a real app, you would verify OTP with backend
    setUserData(prev => ({ ...prev, emailVerified: true }));
    setIsEditingEmail(false);
    setEmailOTP('');
  };

  const handleSave = () => {
    // In a real app, you would save to backend here
    console.log('Saved:', userData);
  };

  const handleCancel = () => {
    setUserData({ ...originalData });
    setFile(null);
    setIsEditingPhone(false);
    setIsEditingEmail(false);
  };

  return (
    <div className="border-gray-700 rounded-lg p-6">
      <Card className="border-gray-700">
        <CardHeader>
          <CardTitle className="text-2xl">Edit Profile</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col md:flex-row gap-8">
            {/* Left Column - Profile Picture */}
            <div className="flex flex-1/3 flex-col items-center gap-4 w-full md:w-auto">
              <div className="relative">
                <Avatar className="h-50 w-50">
                  <AvatarImage src={userData.profileImage} />
                  <AvatarFallback>
                    {userData.name.split(' ').map(n => n[0]).join('')}
                  </AvatarFallback>
                </Avatar>
                <label 
                  htmlFor="profile-upload"
                  className="absolute -bottom-3 left-1/2 -translate-x-1/2 cursor-pointer"
                >
                  <div className="bg-gray-700 hover:bg-gray-600 p-2 rounded-full border border-gray-500">
                    <Camera className="h-5 w-5" />
                  </div>
                  <input
                    id="profile-upload"
                    type="file"
                    accept="image/*"
                    className="hidden"
                    onChange={handleFileChange}
                  />
                </label>
              </div>
              <Button 
                variant="outline" 
                className="border-gray-600"
                onClick={() => document.getElementById('profile-upload')?.click()}
              >
                Change Photo
              </Button>
            </div>

            {/* Right Column - Form */}
            <div className="flex-2/3 space-y-4">
              {/* Name */}
              <div className="space-y-2">
                <Label htmlFor="name">Full Name</Label>
                <Input
                  id="name"
                  name="name"
                  value={userData.name}
                  onChange={handleChange}
                  className="bg-gray-700 border-gray-600"
                />
              </div>

              {/* Address */}
              <div className="space-y-2">
                <Label htmlFor="address">Address</Label>
                <Textarea
                  id="address"
                  name="address"
                  value={userData.address}
                  onChange={handleChange}
                  className="bg-gray-700 border-gray-600 min-h-[80px]"
                />
              </div>

              {/* Organization */}
              <div className="space-y-2">
                <Label htmlFor="organization">Organization</Label>
                <Input
                  id="organization"
                  name="organization"
                  value={userData.organization}
                  onChange={handleChange}
                  className="bg-gray-700 border-gray-600"
                />
              </div>

              {/* Phone with Verification */}
              <div className="space-y-2">
                <Label htmlFor="phone">Mobile Phone</Label>
                <div className="flex gap-2">
                  <Input
                    id="phone"
                    name="phone"
                    value={userData.phone}
                    onChange={handleChange}
                    className="bg-gray-700 border-gray-600 flex-1"
                  />
                  {userData.phoneVerified ? (
                    <Badge className="bg-green-500 hover:bg-green-600 h-10 px-3 flex items-center gap-1">
                      <Check className="h-4 w-4" />
                      Verified
                    </Badge>
                  ) : isEditingPhone ? (
                    <div className="flex gap-2">
                      <Input
                        placeholder="Enter OTP"
                        value={phoneOTP}
                        onChange={(e) => setPhoneOTP(e.target.value)}
                        className="bg-gray-700 border-gray-600 w-24"
                      />
                      <Button 
                        size="sm" 
                        onClick={confirmPhoneVerification}
                        disabled={!phoneOTP}
                      >
                        Confirm
                      </Button>
                    </div>
                  ) : (
                    <Button 
                      variant="outline" 
                      className="border-gray-600"
                      onClick={handleVerifyPhone}
                    >
                      <Phone className="h-4 w-4 mr-2" />
                      Verify
                    </Button>
                  )}
                </div>
              </div>

              {/* Email with Verification */}
              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <div className="flex gap-2">
                  <Input
                    id="email"
                    name="email"
                    type="email"
                    value={userData.email}
                    onChange={handleChange}
                    className="bg-gray-700 border-gray-600 flex-1"
                  />
                  {userData.emailVerified ? (
                    <Badge className="bg-green-500 hover:bg-green-600 h-10 px-3 flex items-center gap-1">
                      <Check className="h-4 w-4" />
                      Verified
                    </Badge>
                  ) : isEditingEmail ? (
                    <div className="flex gap-2">
                      <Input
                        placeholder="Enter OTP"
                        value={emailOTP}
                        onChange={(e) => setEmailOTP(e.target.value)}
                        className="bg-gray-700 border-gray-600 w-24"
                      />
                      <Button 
                        size="sm" 
                        onClick={confirmEmailVerification}
                        disabled={!emailOTP}
                      >
                        Confirm
                      </Button>
                    </div>
                  ) : (
                    <Button 
                      variant="outline" 
                      className="border-gray-600"
                      onClick={handleVerifyEmail}
                    >
                      <Mail className="h-4 w-4 mr-2" />
                      Verify
                    </Button>
                  )}
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex justify-end gap-3 pt-6">
                <Button 
                  variant="outline" 
                  className="border-gray-600"
                  onClick={handleCancel}
                >
                  <X className="h-4 w-4 mr-2" />
                  Cancel
                </Button>
                <Button onClick={handleSave}>
                  <Check className="h-4 w-4 mr-2" />
                  Save Changes
                </Button>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default RenterProfileEdit;