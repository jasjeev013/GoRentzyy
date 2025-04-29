"use client";

import React from 'react'
import CreateAccountComponent from '../components/createAccountComponents/CreateAccountComponent'
import { useRouter } from 'next/navigation';
// import { useRouter } from 'next/router';

const page = () => {
  const router = useRouter();
  return (
    <>
      <CreateAccountComponent
        onClose={() => router.back()}

      />
    </>
  )
}

export default page
