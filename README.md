# Car Rental System

This project is a Car Rental System with a Spring Boot backend and a Next.js frontend. The backend and frontend are located in the `server` and `client` folders, respectively.

## Project Structure

```
/C:/Users/jasje/Desktop/GoRentzyy/
├── client/   # Next.js frontend
└── server/   # Spring Boot backend
```

## Technologies Used

- **Backend**: Spring Boot
- **Frontend**: Next.js

## Getting Started

### Prerequisites

- Java 11 or higher
- Node.js 14 or higher
- npm or yarn

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/yourusername/GoRentzyy.git
    cd GoRentzyy
    ```

2. Install backend dependencies:
    ```bash
    cd server
    ./mvnw install
    ```

3. Install frontend dependencies:
    ```bash
    cd ../client
    npm install
    ```

### Running the Application

1. Start the backend server:
    ```bash
    cd server
    ./mvnw spring-boot:run
    ```

2. Start the frontend server:
    ```bash
    cd ../client
    npm run dev
    ```

3. Open your browser and navigate to `http://localhost:3000` to see the application running.

## Features

- User authentication and authorization
- Car listing and booking
- Admin panel for managing cars and bookings

## Contributing

Contributions are welcome! Please fork the repository and create a pull request with your changes.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

For any inquiries, please contact [your-email@example.com](mailto:your-email@example.com).

## Extras

1. Accessing Data through Dtos
2. Added Validations in DTo
3. GlobalExceptionHandler for exceptions & with custom exceptions
<!-- 4. Added Swagger for API Documentation -->
5. Added Custom UserDetails Service & Authentication Provider
6. Done with the concept of Profiles
7. Added AUthenticationEntryPoint & AccessDeniedHandler
8. Session time & no. of session control
9. Authentication events
