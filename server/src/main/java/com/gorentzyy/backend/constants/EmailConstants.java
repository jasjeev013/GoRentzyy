package com.gorentzyy.backend.constants;

public final class EmailConstants {
    public static String getNewUserCreatedSubject = "Welcome to GoRentzyy - Get Started Today!";
    public static String getNewUserCreatedBody(String name){
        return "Hello "+name+",\n" +
                "\n" +
                "Welcome to GoRentzyy! We're excited to have you on board. Your account has been successfully created, and you're all set to explore the features we offer.\n" +
                "\n" +
                "Here’s how to get started:\n" +
                "\n" +
                "Log in to your account\n" +
                "\n" +
                "Explore your dashboard and features\n" +
                "\n" +
                "Update your profile to personalize your experience\n" +
                "\n" +
                "If you have any questions or need assistance, feel free to reach out to our support team at gorenzyy@gmail.com.\n" +
                "\n" +
                "Happy exploring! \uD83D\uDE80\n" +
                "The GoRentzyy Team";
    }
    public static String getUserLoginSubject = "New Device Logged In Successfully – GoRentzyy";
    public static String getUserLoginBody(String name){
        return "Hello "+name+",\n" +
                "\n" +
                "We wanted to let you know that your account has been successfully accessed from a new device.\n" +
                "\n" +
                "Device Details:\n" +
                "\n" +
                "Device: [Device Type, e.g., iPhone, Android, Windows]\n" +
                "\n" +
                "Location: [Approximate Location or IP Address, if available]\n" +
                "\n" +
                "Date & Time: [Login Date and Time]\n" +
                "\n" +
                "If this was you, no further action is needed. Enjoy your experience!\n" +
                "\n" +
                "If you didn’t recognize this login:\n" +
                "\n" +
                "Immediately change your password\n" +
                "\n" +
                "Review recent account activity [Link to Security Settings]\n" +
                "\n" +
                "Contact support if you need help\n" +
                "\n" +
                "Your security is our top priority.\n" +
                "\n" +
                "Stay safe,\n" +
                "The [Your Application Name] Team";
    }
    public static String bookingConfirmedOfRenterSubject(String carName){
        return "Booking Confirmed for the car"+carName;
    }
    public static String bookingConfirmedOfRenterBody = "Booking confirmation is done for the specific date with the specific timings";
    public static String bookingConfirmedOfSpecificCarOfHostSubject(String carName,String renterName){
        return "Booking Confirmed for your car"+carName+ "with the renter "+renterName;
    }
    public static String bookingConfirmedOfSpecificCarOfHostBody = "Booking confirmation is done for the specific date with the specific timings";
}
