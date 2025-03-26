"use client";
import React from 'react'
import { Check, ChevronsUpDown } from "lucide-react"
import { cn } from "../../lib/utils"
import { Button } from "../../components/ui/button"
import {
    Command,
    CommandEmpty,
    CommandGroup,
    CommandInput,
    CommandItem,
    CommandList,
} from "../../components/ui/command"
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from "../../components/ui/popover"

const areas = [
    {
        value: "next.js",
        label: "Next.js",
    },
    {
        value: "sveltekit",
        label: "SvelteKit",
    },
    {
        value: "nuxt.js",
        label: "Nuxt.js",
    },
    {
        value: "remix",
        label: "Remix",
    },
    {
        value: "astro",
        label: "Astro",
    },
]
const types = [
    {
        value: "pext.js",
        label: "pext.js",
    },
    {
        value: "sveltekit",
        label: "SvelteKit",
    },
    {
        value: "nuxt.js",
        label: "Nuxt.js",
    },
    {
        value: "remix",
        label: "Remix",
    },
    {
        value: "astro",
        label: "Astro",
    },
]

const FourthSectionDropdownSection = () => {
    const [open1, setOpen1] = React.useState(false)
    const [value1, setValue1] = React.useState("")
    const [open2, setOpen2] = React.useState(false)
    const [value2, setValue2] = React.useState("")

    return (
        <div className="flex justify-center gap-20 mt-8">
            {/* First Dropdown */}
            <Popover open={open1} onOpenChange={setOpen1}>
                <PopoverTrigger asChild>
                    <Button
                        variant="outline"
                        role="combobox"
                        aria-expanded={open1}
                        className="w-80 h-10 justify-between dark:!bg-blue-500 dark:!text-black dark:hover:!bg-blue-600 !border-black-700 dark:!border-blue-700"
                    >
                        {value1
                            ? areas.find((areas) => areas.value === value1)?.label
                            : "Select Area..."}
                        <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-70 !text-black" />
                    </Button>
                </PopoverTrigger>
                <PopoverContent className="w-80 p-0 !border-blue-700 !bg-blue-400">
                    <Command >
                        <CommandInput
                            placeholder="Search Area..."

                        />
                        <CommandList>
                            <CommandEmpty className="!text-black dark:!text-white">No area found.</CommandEmpty>
                            <CommandGroup className="!text-black dark:!text-white">
                                {areas.map((areas) => (
                                    <CommandItem
                                        key={areas.value}
                                        value={areas.value}
                                        onSelect={(currentValue) => {
                                            setValue1(currentValue === value1 ? "" : currentValue)
                                            setOpen1(false)
                                        }}
                                        className="dark:hover:!bg-blue-500 dark:aria-selected:!bg-blue-600 !text-black dark:!text-white"
                                    >
                                        <Check
                                            className={cn(
                                                "mr-2 h-4 w-4 !text-black dark:!text-white",
                                                value1 === areas.value ? "opacity-100" : "opacity-0"
                                            )}
                                        />
                                        {areas.label}
                                    </CommandItem>
                                ))}
                            </CommandGroup>
                        </CommandList>
                    </Command>
                </PopoverContent>
            </Popover>

            {/* Second Dropdown */}
            <Popover open={open2} onOpenChange={setOpen2}>
                <PopoverTrigger asChild>
                    <Button
                        variant="outline"
                        role="combobox"
                        aria-expanded={open2}
                        className="w-80 h-10 justify-between dark:!bg-blue-500 dark:!text-black dark:hover:!bg-blue-600  dark:!border-blue-700"
                    >
                        {value2
                            ? types.find((types) => types.value === value2)?.label
                            : "Select Type..."}
                        <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-70 !text-black" />
                    </Button>
                </PopoverTrigger>
                <PopoverContent className="w-80 p-0 !border-blue-700 !bg-blue-400">
                    <Command>
                        <CommandInput
                            placeholder="Search Type..."
                        />
                        <CommandList>
                            <CommandEmpty className="!text-black dark:!text-white"> No type found.</CommandEmpty>
                            <CommandGroup className="!text-black dark:!text-white">
                                {types.map((types) => (
                                    <CommandItem
                                        key={types.value}
                                        value={types.value}
                                        onSelect={(currentValue) => {
                                            setValue2(currentValue === value2 ? "" : currentValue)
                                            setOpen2(false)
                                        }}
                                        className="dark:hover:!bg-blue-500 dark:aria-selected:!bg-blue-600 !text-black dark:!text-white"
                                    >
                                        <Check
                                            className={cn(
                                                "mr-2 h-4 w-4 !text-black dark:!text-white",
                                                value2 === types.value ? "opacity-100" : "opacity-0"
                                            )}
                                        />
                                        {types.label}
                                    </CommandItem>
                                ))}
                            </CommandGroup>
                        </CommandList>
                    </Command>
                </PopoverContent>
            </Popover>
        </div>
    )
}

export default FourthSectionDropdownSection