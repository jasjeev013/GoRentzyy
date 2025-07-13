import React, { useState } from 'react';
import { Star, StarHalf, User } from 'lucide-react';
import { useAuthStore } from '../../../stores/authStore';
import { api } from '../../../app/api/config';
import { reviewService } from '../../api/review/service';

interface Review {
  reviewId: number;
  rating: number;
  comments: string;
  createdAt: string;
  user?: {
    fullName: string;
    profilePicture?: string;
  };
}

interface ReviewsSectionProps {
  reviews: Review[];
  carId: number;
}

const ReviewsSection = ({ reviews, carId }: ReviewsSectionProps) => {
  const { isAuthenticated, userData } = useAuthStore();
  const [newReview, setNewReview] = useState({
    rating: 5,
    comment: ''
  });
  const {addReview} = reviewService;
  const [submitting, setSubmitting] = useState(false);
  const [allReviews, setAllReviews] = useState(reviews);

  const handleSubmitReview = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isAuthenticated || !newReview.comment.trim()) return;

    setSubmitting(true);
    try {
      const response = await addReview(carId, {
        rating: newReview.rating,
        comments: newReview.comment
      });
      console.log('Review submitted:', response);
      /*setAllReviews([...allReviews, {
        ...response.data,
        user: {
          fullName: userData?.fullName || 'You',
          profilePicture: userData?.profilePicture
        }
      }]);*/

      // setNewReview({ rating: 5, comment: '' });
    } catch (error) {
      console.error('Failed to submit review:', error);
    } finally {
      setSubmitting(false);
    }
  };

  const renderStars = (rating: number) => {
    const stars = [];
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 >= 0.5;
    
    for (let i = 1; i <= 5; i++) {
      if (i <= fullStars) {
        stars.push(<Star key={i} className="h-5 w-5 text-yellow-400 fill-current" />);
      } else if (i === fullStars + 1 && hasHalfStar) {
        stars.push(<StarHalf key={i} className="h-5 w-5 text-yellow-400 fill-current" />);
      } else {
        stars.push(<Star key={i} className="h-5 w-5 text-gray-300 fill-current" />);
      }
    }
    
    return <div className="flex">{stars}</div>;
  };

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
      <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-6">
        Customer Reviews ({allReviews.length})
      </h2>

      {isAuthenticated && (
        <form onSubmit={handleSubmitReview} className="mb-8 border-b border-gray-200 dark:border-gray-700 pb-6">
          <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">Write a Review</h3>
          
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Rating
            </label>
            <div className="flex items-center">
              {[1, 2, 3, 4, 5].map((star) => (
                <button
                  key={star}
                  type="button"
                  onClick={() => setNewReview({...newReview, rating: star})}
                  className="focus:outline-none"
                >
                  {star <= newReview.rating ? (
                    <Star className="h-6 w-6 text-yellow-400 fill-current" />
                  ) : (
                    <Star className="h-6 w-6 text-gray-300 fill-current" />
                  )}
                </button>
              ))}
            </div>
          </div>
          
          <div className="mb-4">
            <label htmlFor="comment" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Your Review
            </label>
            <textarea
              id="comment"
              rows={4}
              value={newReview.comment}
              onChange={(e) => setNewReview({...newReview, comment: e.target.value})}
              className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:text-white"
              placeholder="Share your experience with this car..."
            />
          </div>
          
          <button
            type="submit"
            disabled={submitting || !newReview.comment.trim()}
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {submitting ? 'Submitting...' : 'Submit Review'}
          </button>
        </form>
      )}

      <div className="space-y-6">
        {allReviews.length === 0 ? (
          <p className="text-gray-500 dark:text-gray-400">No reviews yet. Be the first to review!</p>
        ) : (
          allReviews.map((review) => (
            <div key={review.reviewId} className="border-b border-gray-200 dark:border-gray-700 pb-6 last:border-0 last:pb-0">
              <div className="flex items-start gap-3">
                <div className="flex-shrink-0">
                  {review.user?.profilePicture ? (
                    <img 
                      src={review.user.profilePicture} 
                      alt={review.user.fullName}
                      className="h-10 w-10 rounded-full object-cover"
                    />
                  ) : (
                    <div className="h-10 w-10 rounded-full bg-gray-200 dark:bg-gray-600 flex items-center justify-center">
                      <User className="h-5 w-5 text-gray-500 dark:text-gray-400" />
                    </div>
                  )}
                </div>
                
                <div>
                  <div className="flex items-center gap-2">
                    <h4 className="font-medium text-gray-900 dark:text-white">
                      {review.user?.fullName || 'Anonymous'}
                    </h4>
                    <span className="text-xs text-gray-500 dark:text-gray-400">
                      {new Date(review.createdAt).toLocaleDateString()}
                    </span>
                  </div>
                  
                  <div className="mt-1 mb-2">
                    {renderStars(review.rating)}
                  </div>
                  
                  <p className="text-gray-700 dark:text-gray-300">
                    {review.comments}
                  </p>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default ReviewsSection;