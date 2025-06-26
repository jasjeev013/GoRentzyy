'use client';

import { useEffect } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import { useAuth } from '../../../hooks/useAuth';


export default function AuthCallback() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { handleOAuthCallback } = useAuth();

  useEffect(() => {
    const token = searchParams.get('token');
    const role = searchParams.get('role');

    console.log('AuthCallback token:', token);

    if (token && role) {
      handleOAuthCallback(token, role)
        .then(({ success }) => {
          router.push(success ? '/home' : '/login');
        })
        .catch(() => {
          router.push('/login');
        });
    } else {
      router.push('/login');
    }
  }, [searchParams]);

  return <div>Loading...</div>;
}