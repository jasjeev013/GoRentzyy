package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.models.LoginRequest;
import com.gorentzyy.backend.models.LoginResponse;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.UserDto;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
@Validated
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final Environment env;



    @Autowired
    public UserController(UserService userService, UserRepository userRepository, AuthenticationManager authenticationManager, Environment env) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.env = env;

    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponseObject> createUser(@Valid @RequestBody UserDto userDto) {
        System.out.println(userDto);
        return userService.createNewUser(userDto);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponseObject> updateUser(Authentication authentication,@Valid  @RequestBody UserDto userDto){
        String email = authentication.getName();
        return userService.updateUserByEmail(userDto, email);
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponseObject> getUser(Authentication authentication) {
        String email = authentication.getName();  // Extract email
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
        String jwt = "";
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),
                loginRequest.password());

        Authentication authenticationResponse =  authenticationManager.authenticate(authentication);
        System.out.println(authenticationResponse + " Auth Response ");
        if (null != authenticationResponse &&  authenticationResponse.isAuthenticated()){
            if (null!=env){
                String secret = env.getProperty(AppConstants.JWT_SECRET_KEY, AppConstants.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                System.out.println(authenticationResponse + " login in User Controller");
                jwt = Jwts.builder().issuer("GoRentzyy").subject("JWT Token")
                        .claim("username",authenticationResponse.getName())
                        .claim("authorities",authenticationResponse.getAuthorities().stream().map(
                                GrantedAuthority::getAuthority
                        ).collect(Collectors.joining(",")))
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + 30000000))
                        .signWith(secretKey).compact();
            }
        }
        return ResponseEntity.status(HttpStatus.OK).header(AppConstants.JWT_HEADER,jwt)
                .body(new LoginResponse(HttpStatus.OK.getReasonPhrase(),jwt));
    }


}


