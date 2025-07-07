package com.gorentzyy.backend.constants;

public final class AppConstants {

    public enum Status {CONFIRMED, CANCELED, MODIFIED,PENDING}
    public enum CarCategory {SEDAN, COUPE, HATCHBACK, CONVERTIBLE, WAGON, SUV, CROSSOVER, PICKUP_TRUCK, MINIVAN}
    public enum CarType {ECONOMY,LUXURY,SPORTS,SUPERCAR,ELECTRIC,HYBRID,OFF_ROAD}
    public enum FuelType {PETROL, DIESEL, ELECTRIC, CNG, OTHER}
    public enum AvailabilityStatus {AVAILABLE, RESERVED, UNDER_MAINTENANCE}
    public enum Type {BOOKING_CONFIRMATION, PAYMENT_ALERT, REMINDER, OTHER}
    public enum PaymentMethod {CREDIT_CARD, PAYPAL,UPI, OTHER}
    public enum PaymentStatus {SUCCESSFUL, FAILED, PENDING}
    public enum RefundStatus {REFUNDED, NON_REFUNDABLE, PENDING}
    public enum Role {HOST, RENTER}
    public enum TransmissionMode {MANUAL, AUTOMATIC, IMT}



}
