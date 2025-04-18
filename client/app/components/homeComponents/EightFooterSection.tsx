import React from 'react'

const EightFooterSection = () => {
    return (
        <>
            <div className="bg-gray-900 text-white py-10 px-4">
                <div className="max-w-9xl mx-auto text-center">
                    <h2 className="text-3xl font-bold mb-4">Stay Connected</h2>
                    <p className="mb-6">Subscribe to our newsletter for the latest updates.</p>
                    <input type="email" placeholder="Enter your email" className="p-2 rounded-md mr-2" />
                    <button className="bg-blue-600 text-white p-2 rounded-md">Subscribe</button>
                </div>
            </div>
            <div className="bg-gray-800 text-white py-6">
                <div className="max-w-9xl mx-auto text-center">
                    <p>&copy; 2023 Your Company. All rights reserved.</p>
                    <div className="flex justify-center space-x-4 mt-4">
                        <a href="#" className="text-gray-400 hover:text-white">Privacy Policy</a>
                        <a href="#" className="text-gray-400 hover:text-white">Terms of Service</a>
                    </div>
                </div>
            </div>
        </>
    )
}

export default EightFooterSection
