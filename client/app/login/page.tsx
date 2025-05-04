"use client";


import { useRouter } from "next/navigation";
import { useAuthStore } from "../../stores/authStore";
import LoginComponent from "../components/loginComponents/LoginComponent";

import React, { useEffect } from 'react'

const page = () => {

  const { isAuthenticated,role } = useAuthStore();
  const router = useRouter();

  useEffect(() => {
    if (isAuthenticated) {
      if (role === 'ROLE_RENTER') router.push('/dashboard/renter/{renterId}');
      else router.push('/dashboard/host/{hostId}');
    }
  }, [isAuthenticated, router]);

  return <LoginComponent />;
}

export default page;

