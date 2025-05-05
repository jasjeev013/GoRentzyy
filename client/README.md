This is a [Next.js](https://nextjs.org) project bootstrapped with [`create-next-app`](https://nextjs.org/docs/app/api-reference/cli/create-next-app).

## Getting Started

First, run the development server:

```bash
npm run dev
# or
yarn dev
# or
pnpm dev
# or
bun dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

You can start editing the page by modifying `app/page.tsx`. The page auto-updates as you edit the file.

This project uses [`next/font`](https://nextjs.org/docs/app/building-your-application/optimizing/fonts) to automatically optimize and load [Geist](https://vercel.com/font), a new font family for Vercel.

## Learn More

To learn more about Next.js, take a look at the following resources:

- [Next.js Documentation](https://nextjs.org/docs) - learn about Next.js features and API.
- [Learn Next.js](https://nextjs.org/learn) - an interactive Next.js tutorial.

You can check out [the Next.js GitHub repository](https://github.com/vercel/next.js) - your feedback and contributions are welcome!

## Deploy on Vercel

The easiest way to deploy your Next.js app is to use the [Vercel Platform](https://vercel.com/new?utm_medium=default-template&filter=next.js&utm_source=create-next-app&utm_campaign=create-next-app-readme) from the creators of Next.js.

Check out our [Next.js deployment documentation](https://nextjs.org/docs/app/building-your-application/deploying) for more details.



<!-- 
Commands written 

1. Downloading Bun: npm install -g bun
2. Donwloading next: bun x create-next-app@latest .
3. Running: bun run dev
4. Downloading shadcn:  bun x --bun shadcn@latest init
5. Adding Shadcn components: bun x --bun shadcn@latest add button
6. Downloading tailwind css: bun install tailwindcss @tailwindcss/postcss postcss
7. Acertinity UI components
8. 21st.dev UI components
9. 
 -->

 <!-- https://magicui.design/docs/components/scroll-progress
 https://ui.aceternity.com/components/infinite-moving-cards
 https://lunarui.dev/components/react/scroll-animations/sticky-scroll
 https://ui.shadcn.com/docs/components/carousel 
 F4F4F4-->

 {
    "message": "All cars found",
    "result": true,
    "data": [
        [
            {
                "host": {
                    "userId": 1,
                    "fullName": "Mohammed Arif",
                    "email": "m.arif@example.in",
                    "phoneNumber": "+919998877665",
                    "address": "Charminar, Hyderabad, Telangana",
                    "role": "HOST",
                    "createdAt": "2025-05-04T15:53:12.660049",
                    "updatedAt": "2025-05-04T15:53:12.660049"
                },
                "carId": 1,
                "name": "Honda City",
                "make": "Honda",
                "model": "City ZX",
                "year": 2020,
                "color": "White",
                "registrationNumber": "DL2CAF1234",
                "photos": [],
                "carCategory": "SEDAN",
                "carType": "ECONOMY",
                "fuelType": "PETROL",
                "transmissionMode": "AUTOMATIC",
                "seatingCapacity": 5,
                "luggageCapacity": 500,
                "rentalPricePerDay": 2000.0,
                "rentalPricePerWeek": 12000.0,
                "rentalPricePerMonth": 50000.0,
                "availabilityStatus": "AVAILABLE",
                "maintenanceDueDate": "2025-06-15T10:00:00",
                "createdAt": "2025-05-04T16:09:27.136129",
                "updatedAt": "2025-05-04T16:09:27.136129",
                "insurance": "Comprehensive insurance with third-party cover",
                "roadSideAssistance": "Available 24/7",
                "fuelPolicy": "Full to Full",
                "features": "Air conditioning, Bluetooth, Parking sensors",
                "importantPoints": "Good for city commute, fuel-efficient",
                "location": {
                    "locationId": 1,
                    "city": "Mumbai",
                    "address": "Flat No. 502, Sea Breeze Apartments, Juhu Tara Road, Juhu, Mumbai, Maharashtra",
                    "latitude": 19.076,
                    "longitude": 72.8777
                },
                "reviews": [
                    {
                        "reviewId": 4,
                        "rating": 5,
                        "comments": "The car is a dream to drive! Super smooth handling, excellent fuel efficiency, and top-notch features. Highly recommend!",
                        "createdAt": "2025-05-05T14:26:01.463758"
                    }
                ]
            },
            {
                "host": {
                    "userId": 1,
                    "fullName": "Mohammed Arif",
                    "email": "m.arif@example.in",
                    "phoneNumber": "+919998877665",
                    "address": "Charminar, Hyderabad, Telangana",
                    "role": "HOST",
                    "createdAt": "2025-05-04T15:53:12.660049",
                    "updatedAt": "2025-05-04T15:53:12.660049"
                },
                "carId": 2,
                "name": "Mahindra XUV700",
                "make": "Mahindra",
                "model": "XUV700 AX7",
                "year": 2023,
                "color": "Silver",
                "registrationNumber": "MH12DG5467",
                "photos": [],
                "carCategory": "SUV",
                "carType": "LUXURY",
                "fuelType": "DIESEL",
                "transmissionMode": "MANUAL",
                "seatingCapacity": 7,
                "luggageCapacity": 800,
                "rentalPricePerDay": 4500.0,
                "rentalPricePerWeek": 27000.0,
                "rentalPricePerMonth": 110000.0,
                "availabilityStatus": "RESERVED",
                "maintenanceDueDate": "2025-07-10T12:00:00",
                "createdAt": "2025-05-04T16:09:35.053976",
                "updatedAt": "2025-05-04T16:09:35.053976",
                "insurance": "Full coverage with accidental damage protection",
                "roadSideAssistance": "24/7 roadside assistance",
                "fuelPolicy": "Full to Full",
                "features": "Sunroof, Leather seats, 4WD",
                "importantPoints": "Perfect for long trips and off-road",
                "location": {
                    "locationId": 2,
                    "city": "Delhi",
                    "address": "House No. 23, Block C, Janakpuri, New Delhi, Delhi",
                    "latitude": 28.626,
                    "longitude": 77.072
                },
                "reviews": [
                    {
                        "reviewId": 5,
                        "rating": 4,
                        "comments": "Great car overall. The performance is excellent, but I feel the interior could be a little more refined for the price.",
                        "createdAt": "2025-05-05T14:27:09.695208"
                    },
                    {
                        "reviewId": 6,
                        "rating": 3,
                        "comments": "It's a decent car, but it lacks power and comfort. Not the best in its class, but it gets the job done.",
                        "createdAt": "2025-05-05T14:28:36.201261"
                    }
                ]
            },
            {
                "host": {
                    "userId": 1,
                    "fullName": "Mohammed Arif",
                    "email": "m.arif@example.in",
                    "phoneNumber": "+919998877665",
                    "address": "Charminar, Hyderabad, Telangana",
                    "role": "HOST",
                    "createdAt": "2025-05-04T15:53:12.660049",
                    "updatedAt": "2025-05-04T15:53:12.660049"
                },
                "carId": 3,
                "name": "Maruti Suzuki Swift",
                "make": "Maruti Suzuki",
                "model": "Swift ZXI",
                "year": 2021,
                "color": "Red",
                "registrationNumber": "TN10CA9876",
                "photos": [],
                "carCategory": "HATCHBACK",
                "carType": "ECONOMY",
                "fuelType": "PETROL",
                "transmissionMode": "IMT",
                "seatingCapacity": 5,
                "luggageCapacity": 250,
                "rentalPricePerDay": 1500.0,
                "rentalPricePerWeek": 9000.0,
                "rentalPricePerMonth": 35000.0,
                "availabilityStatus": "AVAILABLE",
                "maintenanceDueDate": "2025-10-20T08:00:00",
                "createdAt": "2025-05-04T16:09:41.778984",
                "updatedAt": "2025-05-04T16:09:41.778984",
                "insurance": "Third-party liability insurance",
                "roadSideAssistance": "Limited coverage",
                "fuelPolicy": "Full to Full",
                "features": "Touchscreen, Reverse sensors, Apple CarPlay",
                "importantPoints": "Compact, good for city driving",
                "location": {
                    "locationId": 3,
                    "city": "Bangalore",
                    "address": "No. 12, 2nd Cross, 3rd Main, Jayanagar 4th Block, Bangalore, Karnataka",
                    "latitude": 12.9342,
                    "longitude": 77.588
                },
                "reviews": []
            },
            {
                "host": {
                    "userId": 1,
                    "fullName": "Mohammed Arif",
                    "email": "m.arif@example.in",
                    "phoneNumber": "+919998877665",
                    "address": "Charminar, Hyderabad, Telangana",
                    "role": "HOST",
                    "createdAt": "2025-05-04T15:53:12.660049",
                    "updatedAt": "2025-05-04T15:53:12.660049"
                },
                "carId": 4,
                "name": "BMW 3 Series",
                "make": "BMW",
                "model": "320d Luxury Line",
                "year": 2022,
                "color": "Black",
                "registrationNumber": "KA03MB0011",
                "photos": [],
                "carCategory": "SEDAN",
                "carType": "LUXURY",
                "fuelType": "DIESEL",
                "transmissionMode": "AUTOMATIC",
                "seatingCapacity": 5,
                "luggageCapacity": 400,
                "rentalPricePerDay": 8000.0,
                "rentalPricePerWeek": 48000.0,
                "rentalPricePerMonth": 180000.0,
                "availabilityStatus": "AVAILABLE",
                "maintenanceDueDate": "2025-08-30T15:00:00",
                "createdAt": "2025-05-04T16:09:48.027262",
                "updatedAt": "2025-05-04T16:09:48.027262",
                "insurance": "Comprehensive insurance with roadside assistance",
                "roadSideAssistance": "24/7 road assistance",
                "fuelPolicy": "Full to Full",
                "features": "Navigation, Premium audio system, Automatic climate control",
                "importantPoints": "Luxury sedan with great performance",
                "location": {
                    "locationId": 4,
                    "city": "Chennai",
                    "address": "Flat No. 3B, 2nd Floor, Green Park Apartments, Anna Nagar, Chennai, Tamil Nadu",
                    "latitude": 13.0827,
                    "longitude": 80.2707
                },
                "reviews": [
                    {
                        "reviewId": 7,
                        "rating": 2,
                        "comments": "Disappointed with the car's reliability. It's had several issues since purchase, and the handling isn't as responsive as I expected.",
                        "createdAt": "2025-05-05T14:28:47.99841"
                    },
                    {
                        "reviewId": 12,
                        "rating": 5,
                        "comments": "Absolutely love this car! The acceleration is fantastic, and it handles corners like a pro. The tech features are amazing too. Worth every penny.",
                        "createdAt": "2025-05-05T14:31:27.221811"
                    }
                ]
            },
            {
                "host": {
                    "userId": 2,
                    "fullName": "Amit Patel",
                    "email": "amit.patel@example.com",
                    "phoneNumber": "+919812345678",
                    "address": "SG Highway, Ahmedabad, Gujarat",
                    "role": "HOST",
                    "createdAt": "2025-05-04T15:53:20.925373",
                    "updatedAt": "2025-05-04T15:53:20.925373"
                },
                "carId": 5,
                "name": "Audi Q7",
                "make": "Audi",
                "model": "Q7 55 TFSI",
                "year": 2022,
                "color": "White",
                "registrationNumber": "DL3CB9876",
                "photos": [],
                "carCategory": "SUV",
                "carType": "LUXURY",
                "fuelType": "PETROL",
                "transmissionMode": "AUTOMATIC",
                "seatingCapacity": 7,
                "luggageCapacity": 500,
                "rentalPricePerDay": 12000.0,
                "rentalPricePerWeek": 70000.0,
                "rentalPricePerMonth": 250000.0,
                "availabilityStatus": "AVAILABLE",
                "maintenanceDueDate": "2025-05-05T13:00:00",
                "createdAt": "2025-05-04T16:11:31.802609",
                "updatedAt": "2025-05-04T16:11:31.802609",
                "insurance": "Comprehensive insurance including third-party cover",
                "roadSideAssistance": "24/7 assistance",
                "fuelPolicy": "Full to Full",
                "features": "Panoramic sunroof, Parking assist, Premium sound system",
                "importantPoints": "Luxury SUV with top-notch features",
                "location": {
                    "locationId": 5,
                    "city": "Kolkata",
                    "address": "Flat No. 7A, 5th Floor, Park Street, Kolkata, West Bengal",
                    "latitude": 22.5726,
                    "longitude": 88.3639
                },
                "reviews": [
                    {
                        "reviewId": 11,
                        "rating": 4,
                        "comments": "Solid car with good value for money. It's reliable and fuel-efficient, but the ride could be a bit more comfortable on rough roads.",
                        "createdAt": "2025-05-05T14:31:11.498737"
                    }
                ]
            },
            {
                "host": {
                    "userId": 2,
                    "fullName": "Amit Patel",
                    "email": "amit.patel@example.com",
                    "phoneNumber": "+919812345678",
                    "address": "SG Highway, Ahmedabad, Gujarat",
                    "role": "HOST",
                    "createdAt": "2025-05-04T15:53:20.925373",
                    "updatedAt": "2025-05-04T15:53:20.925373"
                },
                "carId": 6,
                "name": "Ford Ecosport",
                "make": "Ford",
                "model": "EcoSport Titanium",
                "year": 2019,
                "color": "Orange",
                "registrationNumber": "WB06A12345",
                "photos": [],
                "carCategory": "CROSSOVER",
                "carType": "ECONOMY",
                "fuelType": "PETROL",
                "transmissionMode": "MANUAL",
                "seatingCapacity": 5,
                "luggageCapacity": 300,
                "rentalPricePerDay": 1800.0,
                "rentalPricePerWeek": 10800.0,
                "rentalPricePerMonth": 45000.0,
                "availabilityStatus": "UNDER_MAINTENANCE",
                "maintenanceDueDate": "2025-12-25T10:00:00",
                "createdAt": "2025-05-04T16:11:38.794627",
                "updatedAt": "2025-05-04T16:11:38.794627",
                "insurance": "Third-party insurance",
                "roadSideAssistance": "Available 24/7",
                "fuelPolicy": "Full to Full",
                "features": "Bluetooth, LED lights, Alloy wheels",
                "importantPoints": "Compact SUV with good mileage",
                "location": {
                    "locationId": 7,
                    "city": "Mumbai",
                    "address": "Plot No. 21, Malabar Hill, Mumbai, Maharashtra",
                    "latitude": 18.9685,
                    "longitude": 72.8231
                },
                "reviews": []
            },
            {
                "host": {
                    "userId": 3,
                    "fullName": "Rajesh Kumar",
                    "email": "rajesh.kumar@example.in",
                    "phoneNumber": "+919876543210",
                    "address": "MG Road, Bengaluru, Karnataka",
                    "role": "HOST",
                    "createdAt": "2025-05-04T15:53:27.427387",
                    "updatedAt": "2025-05-04T15:53:27.427387"
                },
                "carId": 7,
                "name": "Tata Nexon",
                "make": "Tata",
                "model": "Nexon XZ+",
                "year": 2021,
                "color": "Blue",
                "registrationNumber": "MH04TG1234",
                "photos": [],
                "carCategory": "CROSSOVER",
                "carType": "ELECTRIC",
                "fuelType": "ELECTRIC",
                "transmissionMode": "AUTOMATIC",
                "seatingCapacity": 5,
                "luggageCapacity": 350,
                "rentalPricePerDay": 2500.0,
                "rentalPricePerWeek": 15000.0,
                "rentalPricePerMonth": 60000.0,
                "availabilityStatus": "UNDER_MAINTENANCE",
                "maintenanceDueDate": "2025-10-15T14:00:00",
                "createdAt": "2025-05-04T16:12:57.044792",
                "updatedAt": "2025-05-04T16:12:57.044792",
                "insurance": "Basic electric car insurance",
                "roadSideAssistance": "Available during working hours",
                "fuelPolicy": "Full to Full",
                "features": "Electric sunroof, Rearview camera, Keyless entry",
                "importantPoints": "Environmentally friendly, low running cost",
                "location": {
                    "locationId": 8,
                    "city": "Delhi",
                    "address": "Block D-8, Shalimar Bagh, New Delhi, Delhi",
                    "latitude": 28.6776,
                    "longitude": 77.1463
                },
                "reviews": [
                    {
                        "reviewId": 8,
                        "rating": 1,
                        "comments": "Horrible experience. The car broke down within a few months, and the customer service was terrible. Would not recommend.",
                        "createdAt": "2025-05-05T14:29:27.074493"
                    }
                ]
            },
            {
                "host": {
                    "userId": 3,
                    "fullName": "Rajesh Kumar",
                    "email": "rajesh.kumar@example.in",
                    "phoneNumber": "+919876543210",
                    "address": "MG Road, Bengaluru, Karnataka",
                    "role": "HOST",
                    "createdAt": "2025-05-04T15:53:27.427387",
                    "updatedAt": "2025-05-04T15:53:27.427387"
                },
                "carId": 8,
                "name": "Hyundai Verna",
                "make": "Hyundai",
                "model": "Verna SX",
                "year": 2020,
                "color": "Grey",
                "registrationNumber": "HR26CU3245",
                "photos": [],
                "carCategory": "SEDAN",
                "carType": "SPORTS",
                "fuelType": "PETROL",
                "transmissionMode": "MANUAL",
                "seatingCapacity": 5,
                "luggageCapacity": 400,
                "rentalPricePerDay": 2500.0,
                "rentalPricePerWeek": 15000.0,
                "rentalPricePerMonth": 60000.0,
                "availabilityStatus": "AVAILABLE",
                "maintenanceDueDate": "2025-11-10T09:00:00",
                "createdAt": "2025-05-04T16:13:10.40629",
                "updatedAt": "2025-05-04T16:13:10.40629",
                "insurance": "Comprehensive insurance",
                "roadSideAssistance": "Available 24/7",
                "fuelPolicy": "Full to Full",
                "features": "Touchscreen infotainment, Cruise control, Leather seats",
                "importantPoints": "Stylish sedan with a sporty feel",
                "location": {
                    "locationId": 9,
                    "city": "Delhi",
                    "address": "Flat No. 302, Vasant Vihar, New Delhi, Delhi",
                    "latitude": 28.5477,
                    "longitude": 77.168
                },
                "reviews": [
                    {
                        "reviewId": 10,
                        "rating": 4,
                        "comments": "Great family car! Spacious, safe, and has a decent amount of power for everyday driving. The infotainment system could be faster, though.",
                        "createdAt": "2025-05-05T14:31:01.561849"
                    }
                ]
            },
            {
                "host": {
                    "userId": 3,
                    "fullName": "Rajesh Kumar",
                    "email": "rajesh.kumar@example.in",
                    "phoneNumber": "+919876543210",
                    "address": "MG Road, Bengaluru, Karnataka",
                    "role": "HOST",
                    "createdAt": "2025-05-04T15:53:27.427387",
                    "updatedAt": "2025-05-04T15:53:27.427387"
                },
                "carId": 9,
                "name": "Toyota Innova Crysta",
                "make": "Toyota",
                "model": "Innova Crysta 2.8 ZX",
                "year": 2023,
                "color": "Silver",
                "registrationNumber": "KA53FB8899",
                "photos": [],
                "carCategory": "MINIVAN",
                "carType": "LUXURY",
                "fuelType": "DIESEL",
                "transmissionMode": "AUTOMATIC",
                "seatingCapacity": 7,
                "luggageCapacity": 600,
                "rentalPricePerDay": 5500.0,
                "rentalPricePerWeek": 33000.0,
                "rentalPricePerMonth": 130000.0,
                "availabilityStatus": "RESERVED",
                "maintenanceDueDate": "2025-07-25T11:00:00",
                "createdAt": "2025-05-04T16:13:20.265544",
                "updatedAt": "2025-05-04T16:13:20.265544",
                "insurance": "Full insurance coverage with additional benefits",
                "roadSideAssistance": "24/7 roadside assistance",
                "fuelPolicy": "Full to Full",
                "features": "Leather upholstery, Advanced safety features, Premium audio system",
                "importantPoints": "Best for family trips and long drives",
                "location": {
                    "locationId": 10,
                    "city": "Bangalore",
                    "address": "Flat No. 6, Prestige Shantiniketan, Whitefield, Bangalore, Karnataka",
                    "latitude": 12.9769,
                    "longitude": 77.7513
                },
                "reviews": [
                    {
                        "reviewId": 9,
                        "rating": 3,
                        "comments": "It’s an okay car. Comfortable, but the performance doesn’t really impress me. It’s slow to accelerate, and the road noise is a bit much at higher speeds.",
                        "createdAt": "2025-05-05T14:30:50.592992"
                    }
                ]
            }
        ]
    ]
}