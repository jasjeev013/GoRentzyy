package com.gorentzyy.backend.services;

import com.gorentzyy.backend.models.LoginRequest;
import com.gorentzyy.backend.models.LoginResponse;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.ForgotPasswordRequest;
import com.gorentzyy.backend.payloads.ResetPasswordRequest;
import com.gorentzyy.backend.payloads.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {

    ResponseEntity<ApiResponseObject> createNewUser( UserDto userDto);
    ResponseEntity<LoginResponse> loginUser(LoginRequest loginRequest);
    ResponseEntity<ApiResponseObject> updateProfilePhoto(MultipartFile file, String emailId);
    ResponseEntity<ApiResponseObject> updateUserByEmail(UserDto userDto, String emailId,MultipartFile multipartFile);
    ResponseEntity<ApiResponseObject> getUserById(Long userId);
    ResponseEntity<ApiResponseObject> deleteUserByEmail(String email);
    ResponseEntity<ApiResponseObject> getUserByEmail(String email);
    ResponseEntity<ApiResponseObject> sendOTPForEmailVerification(String email);
    ResponseEntity<ApiResponseObject> validateOTPForEmailVerification(String email,String token);
    ResponseEntity<ApiResponseObject> sendOTpForPhoneNumberVerification(String phoneNumber);
    ResponseEntity<ApiResponseObject> validateOTPForPhoneNumberVerification(String phoneNumber,String token);
    ResponseEntity<ApiResponseObject> forgotPassword(ForgotPasswordRequest request);
    ResponseEntity<ApiResponseObject> validateResetToken( String token);
    ResponseEntity<ApiResponseObject> resetPassword( ResetPasswordRequest request);



}
