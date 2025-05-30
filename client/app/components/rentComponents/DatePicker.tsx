import React, { useState } from 'react'
import { Calendar as CalendarIcon } from "lucide-react"
import { format } from "date-fns"
import {Popover, PopoverTrigger, PopoverContent } from '../../../components/ui/popover';
import { Button } from '../../../components/ui/button';
import { Calendar } from "../../../components/ui/calendar"
import { cn } from '../../../lib/utils';
const DatePicker = ({whichDate}) => {
    const [date, setDate] = useState<Date>()

    return (
        <>
            <Popover>
                <PopoverTrigger asChild>
                    <Button
                        variant={"outline"}
                        className={cn(
                            "w-[280px] justify-start text-left font-normal",
                            !date && "text-muted-foreground"
                        )}
                    >
                        <CalendarIcon className="mr-2 h-4 w-4" />
                        {date ? format(date, "PPP") : <span>Pick a {whichDate} date</span>}
                    </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0">
                    <Calendar
                        mode="single"
                        selected={date}
                        onSelect={setDate}
                        initialFocus
                    />
                </PopoverContent>
            </Popover>
        </>
    )
}

export default DatePicker
