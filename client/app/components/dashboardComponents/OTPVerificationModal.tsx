// components/OTPVerificationModal.tsx
import React, { useState, useEffect } from 'react';

import { X } from 'lucide-react';
import { Input } from '../../../components/ui/input';
import { Button } from '../../../components/ui/button';

interface OTPVerificationModalProps {
  email: string;
  onVerify: (otp: string) => Promise<void>;
  onResend: () => Promise<void>;
  onClose: () => void;
}

const OTPVerificationModal = ({ email, onVerify, onResend, onClose }: OTPVerificationModalProps) => {
  const [otp, setOtp] = useState('');
  const [timeLeft, setTimeLeft] = useState(120); // 2 minutes in seconds
  const [isLoading, setIsLoading] = useState(false);
  const [resendLoading, setResendLoading] = useState(false);

  useEffect(() => {
    if (timeLeft <= 0) return;

    const timer = setTimeout(() => {
      setTimeLeft(timeLeft - 1);
    }, 1000);

    return () => clearTimeout(timer);
  }, [timeLeft]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!otp || otp.length !== 6) return;

    setIsLoading(true);
    try {
      await onVerify(otp);
    } finally {
      setIsLoading(false);
    }
  };

  const handleResend = async () => {
    setResendLoading(true);
    try {
      await onResend();
      setTimeLeft(120);
    } finally {
      setResendLoading(false);
    }
  };

  return (
    <div className="fixed inset-0  backdrop-blur-sm flex items-center justify-center z-50">
      <div className="bg-gray-800 rounded-lg p-6 w-full max-w-md relative">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-400 hover:text-white"
        >
          <X className="h-5 w-5" />
        </button>
        
        <h2 className="text-xl font-bold mb-4">Verify Your Email</h2>
        <p className="mb-4">We've sent a 6-digit code to <span className="font-semibold">{email}</span></p>
        
        <form onSubmit={handleSubmit}>
          <Input
            type="text"
            placeholder="Enter OTP"
            value={otp}
            onChange={(e) => setOtp(e.target.value)}
            className="mb-4"
            maxLength={6}
          />
          
          <div className="flex justify-between items-center mb-6">
            <span className="text-sm text-gray-400">
              {Math.floor(timeLeft / 60)}:{(timeLeft % 60).toString().padStart(2, '0')}
            </span>
            <button
              type="button"
              onClick={handleResend}
              disabled={timeLeft > 0 || resendLoading}
              className="text-sm text-blue-500 hover:text-blue-400 disabled:text-gray-500"
            >
              {resendLoading ? 'Sending...' : 'Resend OTP'}
            </button>
          </div>
          
          <Button
            type="submit"
            disabled={!otp || otp.length !== 6 || isLoading}
            className="w-full"
          >
            {isLoading ? 'Verifying...' : 'Verify'}
          </Button>
        </form>
      </div>
    </div>
  );
};

export default OTPVerificationModal;