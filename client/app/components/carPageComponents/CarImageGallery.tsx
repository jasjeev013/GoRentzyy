'use client';
import { useState } from 'react';


interface CarImageGalleryProps {
  photos: string[];
}

const CarImageGallery = ({ photos }: CarImageGalleryProps) => {
  const [mainImage, setMainImage] = useState(photos[0]);

  return (
    <div className="mb-8">
      <div className="relative h-96 w-full rounded-lg overflow-hidden mb-4">
        <img
          src={mainImage}
          alt="Main car image"
          height={400}
          width={800} 
          // fill
          className="object-cover"
          // priority
        />
      </div>
      <div className="grid grid-cols-4 gap-2">
        {photos.map((photo, index) => (
          <button 
            key={index}
            onClick={() => setMainImage(photo)}
            className={`relative h-20 rounded-md overflow-hidden ${mainImage === photo ? 'ring-2 ring-blue-500' : ''}`}
          >
            <img
              src={photo}
              alt={`Car thumbnail ${index + 1}`}
              // fill
              className="object-cover"
            />
          </button>
        ))}
      </div>
    </div>
  );
};

export default CarImageGallery;