"use client";


import { useRouter } from "next/navigation";
import { useAuthStore } from "../../stores/authStore";
import LoginComponent from "../components/loginComponents/LoginComponent";

import React, { useEffect } from 'react'

const page = () => { 

  return <LoginComponent />;
}

export default page;

