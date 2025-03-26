'use client';
import { useEffect, useRef } from 'react';
import { FaCodeBranch, FaCar, FaCheckCircle } from 'react-icons/fa';

const StatisticsSection = () => {
  const counters = useRef<HTMLDivElement[]>([]);

  useEffect(() => {
    const animateCounters = () => {
      const targetValues = [143, 512345, 59232];
      const durations = [2000, 3000, 2500];
      
      counters.current.forEach((counter, index) => {
        const target = targetValues[index];
        const duration = durations[index];
        const startTime = performance.now();
        
        const updateCounter = (currentTime: number) => {
          const elapsedTime = currentTime - startTime;
          const progress = Math.min(elapsedTime / duration, 1);
          const value = Math.floor(progress * target);
          
          counter.textContent = formatNumber(value);
          
          if (progress < 1) {
            requestAnimationFrame(updateCounter);
          }
        };
        
        requestAnimationFrame(updateCounter);
      });
    };

    const formatNumber = (num: number) => {
      return num.toLocaleString();
    };

    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          animateCounters();
          observer.unobserve(entry.target);
        }
      });
    }, { threshold: 0.5 });

    const section = document.querySelector('.statistics-section');
    if (section) observer.observe(section);

    return () => {
      if (section) observer.unobserve(section);
    };
  }, []);

  return (
    <section 
      className="statistics-sectio w-full mt-20 py-20 bg-cover bg-center bg-no-repeat bg-fixed"
      style={{
        backgroundImage: '/carImages/car4.png',
        position: 'relative'
      }}
    >
      {/* Overlay */}
      <div className="absolute inset-0 bg-black bg-opacity-50"></div>
      
      <div className="container mx-auto px-4 relative z-10">
        <div className="flex flex-wrap justify-center -mx-4">
          {/* Total Branches */}
          <div className="w-full md:w-1/3 lg:w-1/4 px-4 pb-8 md:pb-0">
            <div className="counter text-center">
              <div className="icon flex justify-center mb-4">
                <FaCodeBranch className="text-4xl text-white" />
              </div>
              <div className="odo-area mb-2">
                <h2 
                  className="odo-title text-5xl font-bold text-white"
                  ref={el => { if (el) counters.current[0] = el; }}
                >0</h2>
              </div>
              <h4 className="title text-xl text-white font-medium">Total Branches</h4>
            </div>
          </div>

          {/* Get's Service */}
          <div className="w-full md:w-1/3 lg:w-1/4 px-4 pb-8 md:pb-0">
            <div className="counter text-center">
              <div className="icon flex justify-center mb-4">
                <FaCar className="text-4xl text-white" />
              </div>
              <div className="odo-area mb-2">
                <h2 
                  className="odo-title text-5xl font-bold text-white"
                  ref={el => { if (el) counters.current[1] = el; }}
                >0</h2>
              </div>
              <h4 className="title text-xl text-white font-medium">Get's Service</h4>
            </div>
          </div>

          {/* Successful Customer */}
          <div className="w-full md:w-1/3 lg:w-1/4 px-4">
            <div className="counter text-center">
              <div className="icon flex justify-center mb-4">
                <FaCheckCircle className="text-4xl text-white" />
              </div>
              <div className="odo-area mb-2">
                <h2 
                  className="odo-title text-5xl font-bold text-white"
                  ref={el => { if (el) counters.current[2] = el; }}
                >0</h2>
              </div>
              <h4 className="title text-xl text-white font-medium">Successful Customer</h4>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default StatisticsSection;