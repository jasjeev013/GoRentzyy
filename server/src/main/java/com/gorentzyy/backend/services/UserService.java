package com.gorentzyy.backend.services;

import com.gorentzyy.backend.payloads.ApiResponseData;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.UserDto;
import org.springframework.http.ResponseEntity;



public interface UserService {

    /**
     * Creates a new user.
     *
     * @param userDto Data Transfer Object for the user to be created.
     * @return ResponseEntity with status and result.
     */
    ResponseEntity<ApiResponseObject> createNewUser(UserDto userDto);

    /**
     * Updates the existing user.
     *
     * @param userDto Data Transfer Object for the updated user information.
     * @param userId ID of the user to be updated.
     * @return ResponseEntity with status and result.
     */
    ResponseEntity<ApiResponseObject> updateUser(UserDto userDto, Long userId);

    /**
     * Retrieves a user by their unique ID.
     *
     * @param userId ID of the user to retrieve.
     * @return ResponseEntity with user data or error information.
     */
    ResponseEntity<ApiResponseObject> getUserById(Long userId);

    /**
     * Deletes a user by their ID.
     *
     * @param userId ID of the user to delete.
     * @return ResponseEntity with status and result of the operation.
     */
    ResponseEntity<ApiResponseObject> deleteUser(Long userId);

    /**
     * Retrieves a user by their email address.
     *
     * @param email Email of the user to retrieve.
     * @return ResponseEntity with user data or error information.
     */
    ResponseEntity<ApiResponseObject> getUserByEmail(String email);

}
