package com.gorentzyy.backend.constants;

import org.springframework.beans.factory.annotation.Value;

public final class AppConstants {



    public enum Status {CONFIRMED, CANCELED, MODIFIED}
    public enum Category {ECONOMY, LUXURY, SUV, OTHER}
    public enum FuelType {PETROL, DIESEL, ELECTRIC, CNG, OTHER}
    public enum AvailabilityStatus {AVAILABLE, RESERVED, UNDER_MAINTENANCE}
    public enum Type {BOOKING_CONFIRMATION, PAYMENT_ALERT, REMINDER, OTHER}
    public enum PaymentMethod {CREDIT_CARD, PAYPAL,UPI, OTHER}
    public enum PaymentStatus {SUCCESSFUL, FAILED, PENDING}
    public enum RefundStatus {REFUNDED, NON_REFUNDABLE, PENDING}
    public enum Role {HOST, RENTER, BOTH}

    @Value("${JWT_SECRET_KEY}")
    public static final String JWT_SECRET_KEY = "JWT_SECRET";

    @Value("${JWT_SECRET_DEFAULT_VALUE}")
    public static final String JWT_SECRET_DEFAULT_VALUE = "JWT_SECRET_DEFAULT_VALUE";

    @Value("${JWT_HEADER}")
    public static final String JWT_HEADER = "JWT_HEADER";

    @Value("${CLOUDINARY_CLOUD_NAME}")
    public static final String CLOUDINARY_CLOUD_NAME= "CLOUDINARY_CLOUD_NAME";

    @Value("${CLOUDINARY_API_KEY}")
    public static final String CLOUDINARY_API_KEY= "CLOUDINARY_API_KEY";

    @Value("${CLOUDINARY_API_SECRET}")
    public static final String CLOUDINARY_API_SECRET= "CLOUDINARY_API_SECRET";

}
