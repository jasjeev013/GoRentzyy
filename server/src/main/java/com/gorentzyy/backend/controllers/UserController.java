package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.UserDto;
import com.gorentzyy.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponseObject> createUser(@RequestBody UserDto userDto){
        return userService.createNewUser(userDto);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<ApiResponseObject> updateUser(@PathVariable Long userId,@RequestBody UserDto userDto){
        return userService.updateUser(userDto,userId);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<ApiResponseObject> getUser(@PathVariable Long userId){
        return userService.getUserById(userId);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponseObject> deleteUser(@PathVariable Long userId){
        return userService.deleteUser(userId);
    }

    @GetMapping("/get/email/{emailId}")
    public ResponseEntity<ApiResponseObject> getUserByEmail(@PathVariable String emailId){
        return userService.getUserByEmail(emailId);
    }

}
