// app/components/NavbarWrapper.tsx
'use client'; // Mark this as a client component

import { useEffect, useState } from 'react';
import Navbar from './Navbar'; // Import your server-side navbar component


const NavbarWrapper = () => {
    const [isScrolled, setIsScrolled] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
            if (window.scrollY > 0) {
                setIsScrolled(true);
            } else {
                setIsScrolled(false);
            }
        };

        window.addEventListener('scroll', handleScroll);

        return () => {
            window.removeEventListener('scroll', handleScroll);
        };
    }, []);

    return <Navbar isScrolled={isScrolled} />;
};

export default NavbarWrapper;