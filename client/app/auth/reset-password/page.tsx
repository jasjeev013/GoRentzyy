"use client";
import { useEffect, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { authService } from '../../api/auth/services';
import ForgotPasswordModal from '../../components/loginComponents/ForgotPasswordModal';

export default function ResetPasswordPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [token, setToken] = useState('');
  const [isValidToken, setIsValidToken] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const tokenParam = searchParams.get('token');
    if (tokenParam) {
      setToken(tokenParam);
      validateToken(tokenParam);
    } else {
      setIsLoading(false);
    }
  }, [searchParams]);

  const validateToken = async (token: string) => {
    try {
      const response = await authService.validateResetToken(token);
      if (response.result) {
        setIsValidToken(true);
      }
    } catch (error) {
      console.error('Invalid token:', error);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (!token) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center p-6 max-w-md mx-auto bg-white dark:bg-zinc-800 rounded-lg shadow-md">
          <h2 className="text-xl font-bold mb-4">Invalid Reset Link</h2>
          <p className="mb-4">The password reset link is missing a token. Please request a new reset link.</p>
          <button
            onClick={() => router.push('/login')}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Go to Login
          </button>
        </div>
      </div>
    );
  }

  if (!isValidToken) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center p-6 max-w-md mx-auto bg-white dark:bg-zinc-800 rounded-lg shadow-md">
          <h2 className="text-xl font-bold mb-4">Invalid or Expired Link</h2>
          <p className="mb-4">This password reset link is invalid or has expired. Please request a new one.</p>
          <button
            onClick={() => router.push('/login')}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Go to Login
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="flex items-center justify-center min-h-screen">
      <ForgotPasswordModal 
        onClose={() => router.push('/login')}
        onLoginClick={() => router.push('/login')}
      />
    </div>
  );
}