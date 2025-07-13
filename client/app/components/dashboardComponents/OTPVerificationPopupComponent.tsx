"use client";
import React, { useState, useEffect } from 'react';
import { X, RotateCw, CheckCircle } from 'lucide-react';
import { Input } from '../../../components/ui/input';
import { Button } from '../../../components/ui/button';
import { Label } from '../../../components/ui/label';
import { motion } from 'framer-motion';
import { useAuth } from '../../../hooks/useAuth';
import { authService } from '../../../app/api/auth/services';

interface OTPVerificationPopupProps {
  email: string;
  onClose: () => void;
  onVerificationSuccess: () => void;
}

const OTPVerificationPopup: React.FC<OTPVerificationPopupProps> = ({
  email,
  onClose,
  onVerificationSuccess}) => {
  const [otp, setOtp] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [countdown, setCountdown] = useState(0);
  const [otpSent, setOtpSent] = useState(false);
  const { token } = useAuth();

  // Request OTP when component mounts
  useEffect(() => {
    if (token) {
      requestOTP();
    }
  }, [token]);

  // Handle countdown timer
  useEffect(() => {
    let timer: NodeJS.Timeout;
    if (countdown > 0) {
      timer = setTimeout(() => setCountdown(countdown - 1), 1000);
    }
    return () => clearTimeout(timer);
  }, [countdown]);

  const requestOTP = async () => {
    setIsLoading(true);
    setError('');
    try {
      await authService.requestEmailOTP(token);
      setOtpSent(true);
      setCountdown(60); // 60 seconds countdown
    } catch (err) {
      setError('Failed to send OTP. Please try again.');
      console.error('Error requesting OTP:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const verifyOTP = async () => {
    if (!otp || otp.length !== 6) {
      setError('Please enter a valid 6-digit OTP');
      return;
    }

    setIsLoading(true);
    setError('');
    try {
      const result = await authService.verifyEmailOTP(token, otp);
      console.log('OTP verification result:', result);
      if (result.result) {
        setSuccess(true);
        setTimeout(() => {
          onVerificationSuccess();
          onClose();
        }, 1500);
      } else {
        setError('Invalid OTP. Please try again.');
      }
    } catch (err) {
      setError('Verification failed. Please try again.');
      console.error('Error verifying OTP:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleResendOTP = async () => {
    if (countdown > 0) return;
    await requestOTP();
  };

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.2 }}
      className="fixed inset-0 z-50 flex items-center justify-center p-4"
    >
      {/* Backdrop */}
      <motion.div
        className="absolute inset-0 bg-black/30 backdrop-blur-sm"
        onClick={onClose}
      />

      {/* Popup content */}
      <motion.div
        initial={{ scale: 0.95, y: 20 }}
        animate={{ scale: 1, y: 0 }}
        className="relative w-full max-w-md bg-white dark:bg-zinc-800 rounded-xl shadow-xl p-6 z-10 mx-4 border border-gray-200 dark:border-gray-700"
      >
        {/* Close button */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-1 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors"
        >
          <X className="h-5 w-5 text-gray-500 dark:text-gray-400" />
        </button>

        {/* Header */}
        <div className="text-center mb-6">
          <h2 className="text-2xl font-bold text-gray-800 dark:text-white mb-2">
            Verify Your Email
          </h2>
          <p className="text-sm text-gray-600 dark:text-gray-400">
            We've sent a 6-digit code to{' '}
            <span className="font-medium text-gray-800 dark:text-gray-200">
              {email}
            </span>
          </p>
        </div>

        {success ? (
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            className="flex flex-col items-center justify-center py-8"
          >
            <CheckCircle className="h-16 w-16 text-green-500 mb-4" />
            <h3 className="text-xl font-semibold text-gray-800 dark:text-white mb-2">
              Email Verified!
            </h3>
            <p className="text-gray-600 dark:text-gray-400">
              Your email has been successfully verified.
            </p>
          </motion.div>
        ) : (
          <>
            {/* OTP Input */}
            <div className="space-y-4 mb-6">
              <div>
                <Label htmlFor="otp" className="text-gray-700 dark:text-gray-300">
                  Enter OTP Code
                </Label>
                <Input
                  id="otp"
                  type="text"
                  inputMode="numeric"
                  pattern="[0-9]*"
                  maxLength={6}
                  placeholder="123456"
                  value={otp}
                  onChange={(e) => {
                    const value = e.target.value.replace(/\D/g, '');
                    setOtp(value.slice(0, 6));
                  }}
                  className="w-full text-center text-xl tracking-widest py-4"
                  disabled={isLoading}
                />
              </div>

              {error && (
                <motion.p
                  initial={{ opacity: 0, y: -10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="text-sm text-red-500 text-center"
                >
                  {error}
                </motion.p>
              )}

              <div className="text-center text-sm text-gray-600 dark:text-gray-400">
                {otpSent ? (
                  <p>
                    Didn't receive the code?{' '}
                    <button
                      onClick={handleResendOTP}
                      disabled={countdown > 0}
                      className={`font-medium ${countdown > 0 ? 'text-gray-500' : 'text-blue-600 dark:text-blue-400 hover:underline'}`}
                    >
                      {countdown > 0
                        ? `Resend in ${countdown}s`
                        : 'Resend OTP'}
                    </button>
                  </p>
                ) : (
                  <p>Preparing to send OTP...</p>
                )}
              </div>
            </div>

            {/* Verify Button */}
            <Button
              onClick={verifyOTP}
              disabled={isLoading || !otp || otp.length !== 6}
              className="w-full py-3"
            >
              {isLoading ? (
                <>
                  <RotateCw className="h-4 w-4 mr-2 animate-spin" />
                  Verifying...
                </>
              ) : (
                'Verify Email'
              )}
            </Button>
          </>
        )}
      </motion.div>
    </motion.div>
  );
};

export default OTPVerificationPopup;