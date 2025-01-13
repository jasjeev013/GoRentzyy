package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/")
    public ResponseEntity<ApiResponseObject> test(){
        return new ResponseEntity<>(new ApiResponseObject("THe API is running fine",true,null), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponseObject> testPost(@RequestBody String name){
        return new ResponseEntity<>(new ApiResponseObject("THe API is running fine",true,name),HttpStatus.ACCEPTED);
    }

}
