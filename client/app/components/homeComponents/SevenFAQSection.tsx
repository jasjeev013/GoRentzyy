'use client';
import React, { useState } from 'react';
import { FiChevronDown, FiChevronUp } from 'react-icons/fi';

const SevenFAQSection = () => {
  const [activeIndex, setActiveIndex] = useState(null);

  const toggleFAQ = (index) => {
    setActiveIndex(activeIndex === index ? null : index);
  };

  const faqs = [
    {
      question: "What is GoRentzyy?",
      answer: "GoRentzyy is a peer-to-peer car rental platform that connects car owners with travelers looking to rent vehicles. We offer a convenient way to monetize your idle car or find affordable rental options."
    },
    {
      question: "How do I list my car on GoRentzyy?",
      answer: "Listing your car is easy! Simply create an account, provide your car details (make, model, year, photos), set your availability and pricing, and submit for approval. Once approved, your car will be visible to renters."
    },
    {
      question: "How do I rent a car on GoRentzyy?",
      answer: "Browse available cars in your area, select your desired dates, and book instantly. You'll need a valid driver's license and payment method. After booking, you'll coordinate directly with the owner for pickup."
    },
    {
      question: "How are payments handled?",
      answer: "All payments are processed securely through our platform. Owners receive payment after the rental period ends, minus our service fee. Renters pay upfront when booking."
    }
  ];

  return (
    <div className="max-w-6xl mx-auto py-16 px-4 sm:px-6 lg:px-8">
      <h2 className="text-3xl font-extrabold text-center text-white mb-12">
        Frequently Asked Questions
      </h2>
      
      <div className="space-y-2">
        {faqs.map((faq, index) => (
          <div 
            key={index}
            className={`transition-all duration-300 ease-in-out ${
              activeIndex === index 
                ? 'h-full bg-gray-10  rounded-lg shadow-md text-white'
                : 'h-[50%] mx-auto bg-gray-30  rounded-lg text-white'
            }`}
          >
            <button
              onClick={() => toggleFAQ(index)}
              className={`w-full flex justify-between items-center p-6 text-left focus:outline-none transition-all duration-200 ${
                activeIndex === index ? 'text-white' : 'text-gray-300'
              }`}
            >
              <h3 className="text-lg font-medium">
                {faq.question}
              </h3>
              <span className="ml-4">
                {activeIndex === index ? (
                  <FiChevronUp className="h-5 w-5 text-white" />
                ) : (
                  <FiChevronDown className="h-5 w-5 text-gray-500" />
                )}
              </span>
            </button>
            
            <div
              className={`px-6 overflow-hidden transition-all duration-300 ease-in-out ${
                activeIndex === index 
                  ? 'max-h-96 pb-6 opacity-100'
                  : 'max-h-0 pb-0 opacity-0'
              }`}
            >
              <p className="text-white">{faq.answer}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default SevenFAQSection;