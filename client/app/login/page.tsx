"use client";

import { useRouter } from "next/navigation";
import LoginComponent from "../components/loginComponents/LoginComponent";

import React from 'react'

const page = () => {
  const router = useRouter()
  return (
    <LoginComponent
      onClose={() => router.back()}
    />
  )
}

export default page
