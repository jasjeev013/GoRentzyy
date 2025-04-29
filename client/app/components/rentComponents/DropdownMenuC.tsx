"use client";
import React, { useState } from 'react'
import { Button } from "../../../components/ui/button"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuLabel,
    DropdownMenuRadioGroup,
    DropdownMenuRadioItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "../../../components/ui/dropdown-menu"

const DropdownMenuC = () => {
    const [position, setPosition] = useState("Delhi")
    return (
        <>
            <DropdownMenu>
                <DropdownMenuTrigger asChild>
                    <Button variant="outline" className='w-full'>{position}</Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent className="w-70">
                    <DropdownMenuLabel>Choose Location</DropdownMenuLabel>
                    <DropdownMenuSeparator />
                    <DropdownMenuRadioGroup value={position} onValueChange={setPosition}>
                        <DropdownMenuRadioItem value="Delhi">Delhi</DropdownMenuRadioItem>
                        <DropdownMenuRadioItem value="Banglore">Banglore</DropdownMenuRadioItem>
                        <DropdownMenuRadioItem value="Hyderabad">Hyderabad</DropdownMenuRadioItem>
                    </DropdownMenuRadioGroup>
                </DropdownMenuContent>
            </DropdownMenu>
        </>
    )
}

export default DropdownMenuC
