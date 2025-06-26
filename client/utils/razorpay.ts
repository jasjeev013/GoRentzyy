import axios from 'axios';
import { useBookingStore } from '../stores/bookingStore';
import { api } from '../app/api/config';

// Update the loadRazorpay function to return the Razorpay constructor
// Define the RazorpayConstructor type
type RazorpayConstructor = new (options: RazorpayOptions) => any;

export const loadRazorpay = async (): Promise<RazorpayConstructor> => {
  return new Promise((resolve) => {
    if (typeof window !== 'undefined' && (window as any).Razorpay) {
      return resolve((window as any).Razorpay);
    }

    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.onload = () => {
      resolve((window as any).Razorpay);
    };
    script.onerror = () => {
      throw new Error('Failed to load Razorpay script');
    };
    document.body.appendChild(script);
  });
};


interface RazorpayOptions {
  key: string;
  amount: number;
  currency: string;
  name: string;
  description: string;
  image: string;
  order_id: string;
  handler: (response: any) => void;
  prefill: {
    name?: string;
    email?: string;
    contact?: string;
  };
  notes: {
    bookingId: string;
  };
  theme: {
    color: string;
  };
}

export const initiateRazorpayPayment = async (
  amount: number,
  bookingId: number,
  carName: string,
  userDetails: { name?: string; email?: string; phone?: string },
  onSuccess: (response: any) => void,
  onError: (error: string) => void
) => {
  try {
    console.log("Initiating Razorpay payment...");
    
    // 0. First load Razorpay
    const Razorpay = await loadRazorpay();
    if (!Razorpay) {
      throw new Error('Razorpay failed to load');
    }

    // 1. Create order on backend
    const orderResponse = await api.post('/api/payment/create-order', {
      amount,
      bookingId
    });

    const orderData = orderResponse.data.object;
    console.log("Order created:", orderResponse); 

    // 2. Initialize Razorpay
    const options = {
      key: orderData.key,
      amount: orderData.amount,
      currency: orderData.currency,
      name: "Car Rental App",
      description: `Booking for ${carName}`,
      image: "/logo.png",
      order_id: orderData.orderId,
      handler: async function (response: any) {
        try {
          // Verify payment on backend
          await api.post('/api/payment/verify', {
            razorpayOrderId: response.razorpay_order_id,
            razorpayPaymentId: response.razorpay_payment_id,
            razorpaySignature: response.razorpay_signature
          });
          onSuccess(response);
          console.log("Payment verified successfully:", response);
        } catch (error) {
          onError("Payment verification failed");
        }
      },
      prefill: {
        name: userDetails.name,
        email: userDetails.email,
        contact: userDetails.phone
      },
      notes: {
        bookingId: bookingId.toString()
      },
      theme: {
        color: "#3399cc"
      }
    };

    const rzp = new Razorpay(options);
    rzp.open();
  } catch (error) {
    console.error('Payment initialization failed:', error);
    onError(error instanceof Error ? error.message : "Payment failed");
  }
};