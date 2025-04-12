package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.models.LoginRequest;
import com.gorentzyy.backend.models.LoginResponse;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.UserDto;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
@Validated
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;




    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;


    }


//    Working
    @PostMapping("/create")
    public ResponseEntity<ApiResponseObject> createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createNewUser(userDto);
    }

    @PutMapping("/updateProfilePhoto")
    public ResponseEntity<ApiResponseObject> updateProfilePhoto(Authentication authentication,@Valid   @RequestParam("image") MultipartFile multipartFile){
        String email = authentication.getName();
        return userService.updateProfilePhoto(multipartFile, email);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponseObject> updateUser(Authentication authentication,@Valid  @RequestBody UserDto userDto){
        String email = authentication.getName();
        return userService.updateUserByEmail(userDto, email);
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponseObject> getUser(Authentication authentication) {
        String email = authentication.getName();  // Extract email

        System.out.println("Ghus gyaa");
        return userService.getUserByEmail(email);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<ApiResponseObject> getUserById(@PathVariable Long userId){
        return userService.getUserById(userId);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponseObject> deleteUser(Authentication authentication){
        String email = authentication.getName();
        return userService.deleteUserByEmail(email);
    }

    @RequestMapping("/basicAuth/login")
    public User getUserDetailsAfterLogin(Authentication authentication) {
        Optional<User> optionalCustomer = userRepository.findByEmail(authentication.getName());
        return optionalCustomer.orElse(null);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> apiLogin(@RequestBody LoginRequest loginRequest){
        return userService.loginUser(loginRequest);
    }


}


