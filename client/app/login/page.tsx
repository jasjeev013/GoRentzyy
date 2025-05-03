"use client";


import { useRouter } from "next/navigation";
import { useAuthStore } from "../../stores/authStore";
import LoginComponent from "../components/loginComponents/LoginComponent";

import React, { useEffect } from 'react'

const page = () => {

  const { isAuthenticated } = useAuthStore();
  const router = useRouter();

  useEffect(() => {
    if (isAuthenticated) {
      router.push('/');
    }
  }, [isAuthenticated, router]);

  return <LoginComponent />;
}

export default page

