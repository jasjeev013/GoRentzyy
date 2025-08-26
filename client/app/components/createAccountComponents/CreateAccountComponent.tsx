"use client";
import React, { useState } from 'react';
import { X, ArrowRight } from 'lucide-react';
import { Input } from '../../../components/ui/input';
import { Button } from '../../../components/ui/button';
import { Label } from '../../../components/ui/label';
import { signIn } from 'next-auth/react';
import { motion } from 'framer-motion';
import { redirect, useRouter } from 'next/navigation';
import {Select, SelectContent,SelectItem,SelectTrigger,SelectValue,} from '../../../components/ui/select';
import OTPVerificationPopup from '../dashboardComponents/OTPVerificationPopupComponent';
import { useAuth } from '../../../hooks/useAuth';
import { toast } from 'sonner';

const CreateAccountComponent = () => {
  const router = useRouter();

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    phone: '',
    role: '',
    address: ''
  });
  const [isLoading, setIsLoading] = useState(false);
  const [passwordMatch, setPasswordMatch] = useState(true);
  const [showOTPVerification, setShowOTPVerification] = useState(false);
  const {register} = useAuth();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Check password match when confirming password
    if (name === 'confirmPassword' || name === 'password') {
      setPasswordMatch(
        name === 'password'
          ? value === formData.confirmPassword
          : value === formData.password
      );
    }
  };

  const handleRoleChange = (value: string) => {
    setFormData(prev => ({
      ...prev,
      role: value
    }));
  };
  const emailValidated = () => {
    setShowOTPVerification(false);
    toast.success('Email verified successfully! Redirecting to home...');
    router.push('/home');
  }
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!passwordMatch || !formData.role) return;

    setIsLoading(true);

    const response = await register({
      fullName: formData.name,
      email: formData.email,
      phoneNumber: formData.phone,
      password: formData.password,
      role: formData.role,
      address: formData.address
    });

    if (response.success) {
       setShowOTPVerification(true);
      // router.back();
    }

    setIsLoading(false);
  };

  const socialSignup = async (provider: 'google' | 'github') => {
    setIsLoading(true);
    await signIn(provider);
    setIsLoading(false);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* Animated gradient background */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        className="absolute inset-0 bg-gradient-to-br from-black-500/10 via-grey-500/10 to-zinc-500/10 backdrop-blur-md"
        onClick={() => router.back()}
      />

      {/* Floating particles */}
      <div className="absolute inset-0 overflow-hidden">
        {[...Array(15)].map((_, i) => (
          <motion.div
            key={i}
            className="absolute rounded-full bg-blue-500/20 dark:bg-blue-400/20"
            initial={{
              x: Math.random() * 100,
              y: Math.random() * 100,
              scale: Math.random() * 0.5 + 0.5,
              opacity: 0
            }}
            animate={{
              x: Math.random() * window.innerWidth,
              y: Math.random() * window.innerHeight,
              opacity: [0, 0.3, 0],
              transition: {
                duration: Math.random() * 10 + 10,
                repeat: Infinity,
                repeatType: "reverse"
              }
            }}
            style={{
              width: `${Math.random() * 10 + 5}px`,
              height: `${Math.random() * 10 + 5}px`,
            }}
          />
        ))}
      </div>

      {/* Signup card */}
      <motion.div
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        exit={{ scale: 0.9, opacity: 0 }}
        className="relative w-full max-w-md bg-white dark:bg-zinc-800 rounded-2xl shadow-xl p-8 z-10 mx-4 border border-gray-200 dark:border-gray-700"
      >
        {/* Close button */}
        <motion.button
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.9 }}
          onClick={() => router.back()}
          className="absolute top-4 right-4 p-1 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors"
        >
          <X className="h-5 w-5 text-gray-500 dark:text-gray-400" />
        </motion.button>

        {/* Header */}
        <div className="text-center mb-8">
          <motion.h2
            initial={{ y: -20 }}
            animate={{ y: 0 }}
            className="text-3xl font-bold text-gray-800 dark:text-white mb-2"
          >
            Join Us
          </motion.h2>
          <p className="text-sm text-gray-600 dark:text-gray-400">
            Already have an account?{' '}
            <button
              onClick={() => router.push('/login')}
              className="text-blue-600 dark:text-blue-400 hover:underline font-medium"
            >
              Sign in
            </button>
          </p>
        </div>

        {/* Form */}
        <motion.form
          onSubmit={handleSubmit}
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="space-y-5"
        >
          <div className="space-y-3">
            <Label htmlFor="name" className="text-gray-700 dark:text-gray-300">
              Full Name
            </Label>
            <motion.div whileHover={{ scale: 1.01 }}>
              <Input
                id="name"
                name="name"
                type="text"
                placeholder="John Doe"
                value={formData.name}
                onChange={handleChange}
                className="w-full px-4 py-3 rounded-lg border-gray-300 dark:border-gray-600 focus:ring-2 focus:ring-blue-500"
                required
              />
            </motion.div>
          </div>

          <div className="space-y-3">
            <Label htmlFor="email" className="text-gray-700 dark:text-gray-300">
              Email
            </Label>
            <motion.div whileHover={{ scale: 1.01 }}>
              <Input
                id="email"
                name="email"
                type="email"
                placeholder="your@email.com"
                value={formData.email}
                onChange={handleChange}
                className="w-full px-4 py-3 rounded-lg border-gray-300 dark:border-gray-600 focus:ring-2 focus:ring-blue-500"
                required
              />
            </motion.div>
          </div>

          <div className="space-y-3">
            <Label htmlFor="phone" className="text-gray-700 dark:text-gray-300">
              Phone Number
            </Label>
            <motion.div whileHover={{ scale: 1.01 }}>
              <Input
                id="phone"
                name="phone"
                type="tel"
                placeholder="+91 XXXXXXXXXX"
                value={formData.phone}
                onChange={handleChange}
                className="w-full px-4 py-3 rounded-lg border-gray-300 dark:border-gray-600 focus:ring-2 focus:ring-blue-500"
                required
              />
            </motion.div>
          </div>

          <div className="space-y-3">
            <Label htmlFor="address" className="text-gray-700 dark:text-gray-300">
              Address
            </Label>
            <motion.div whileHover={{ scale: 1.01 }}>
              <Input
                id="address"
                name="address"
                type="text"
                placeholder="Your full address"
                value={formData.address}
                onChange={handleChange}
                className="w-full px-4 py-3 rounded-lg border-gray-300 dark:border-gray-600 focus:ring-2 focus:ring-blue-500"
                required
              />
            </motion.div>
          </div>

          <div className="space-y-3">
            <Label className="text-gray-700 dark:text-gray-300">
              Role
            </Label>
            <Select onValueChange={handleRoleChange} required>
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Select your role" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="HOST">Host (List your cars)</SelectItem>
                <SelectItem value="RENTER">Renter (Book cars)</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-3">
            <Label htmlFor="password" className="text-gray-700 dark:text-gray-300">
              Password
            </Label>
            <motion.div whileHover={{ scale: 1.01 }}>
              <Input
                id="password"
                name="password"
                type="password"
                placeholder="••••••••"
                value={formData.password}
                onChange={handleChange}
                className="w-full px-4 py-3 rounded-lg border-gray-300 dark:border-gray-600 focus:ring-2 focus:ring-blue-500"
                required
                minLength={8}
              />
            </motion.div>
          </div>

          <div className="space-y-3">
            <Label htmlFor="confirmPassword" className="text-gray-700 dark:text-gray-300">
              Confirm Password
            </Label>
            <motion.div whileHover={{ scale: 1.01 }}>
              <Input
                id="confirmPassword"
                name="confirmPassword"
                type="password"
                placeholder="••••••••"
                value={formData.confirmPassword}
                onChange={handleChange}
                className={`w-full px-4 py-3 rounded-lg border-gray-300 dark:border-gray-600 focus:ring-2 ${!passwordMatch && formData.confirmPassword ? 'border-red-500 focus:ring-red-500' : 'focus:ring-blue-500'}`}
                required
                minLength={8}
              />
            </motion.div>
            {!passwordMatch && formData.confirmPassword && (
              <motion.p
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                className="text-sm text-red-500"
              >
                {`Passwords don't match`}
              </motion.p>
            )}
          </div>

          <motion.div whileHover={{ scale: 1.01 }}>
            <Button
              type="submit"
              className="w-full bg-gradient-to-r from-blue-600 to-blue-500 hover:from-blue-700 hover:to-blue-600 text-white py-3 rounded-lg shadow-md hover:shadow-lg transition-all"
              disabled={isLoading || !passwordMatch || !formData.role}
            >
              {isLoading ? (
                <div className="flex items-center justify-center">
                  <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin mr-2"></div>
                  Creating account...
                </div>
              ) : (
                <>
                  Create Account
                  <ArrowRight className="ml-2 h-4 w-4" />
                </>
              )}
            </Button>
          </motion.div>
        </motion.form>

        {/* Divider */}
        <div className="relative my-8">
          <div className="absolute inset-0 flex items-center">
            <div className="w-full border-t border-gray-300 dark:border-gray-600" />
          </div>
          <div className="relative flex justify-center">
            <span className="px-3 bg-white dark:bg-zinc-800 text-gray-500 dark:text-gray-400 text-sm">
              OR CONTINUE WITH
            </span>
          </div>
        </div>

        {/* Social signup buttons */}
        <div className="flex flex-col space-y-3">
          <motion.div whileHover={{ y: -2 }} whileTap={{ scale: 0.98 }}>
            <Button
              variant="outline"
              className="w-full flex items-center justify-center gap-3 py-3 rounded-lg border-gray-300 dark:border-gray-600 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
              onClick={() => socialSignup('google')}
              disabled={isLoading}
            >
              <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none">
                <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4" />
                <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853" />
                <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05" />
                <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335" />
              </svg>
              Google
            </Button>
          </motion.div>

          <motion.div whileHover={{ y: -2 }} whileTap={{ scale: 0.98 }}>
            <Button
              variant="outline"
              className="w-full flex items-center justify-center gap-3 py-3 rounded-lg border-gray-300 dark:border-gray-600 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
              onClick={() => socialSignup('github')}
              disabled={isLoading}
            >
              <svg className="w-5 h-5" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z" />
              </svg>
              GitHub
            </Button>
          </motion.div>
        </div>
      </motion.div>
      {showOTPVerification && (
        <OTPVerificationPopup
          email={formData.email} // or the email from registration form
          onClose={() => emailValidated()}
          
        />
      )}
    </div>
  );
};

export default CreateAccountComponent;