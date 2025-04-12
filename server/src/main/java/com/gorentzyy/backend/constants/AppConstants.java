package com.gorentzyy.backend.constants;

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



}
