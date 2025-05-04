package com.gorentzyy.backend.config;

import com.gorentzyy.backend.exceptionhandling.CustomAccessDeniedHandler;
import com.gorentzyy.backend.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import com.gorentzyy.backend.filter.JWTTokenGeneratorFilter;
import com.gorentzyy.backend.filter.JWTTokenValidatorFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@Profile("prod")
public class ProdSecurityConfig {

    private final JWTTokenGeneratorFilter jwtTokenGeneratorFilter;
    private final JWTTokenValidatorFilter jwtTokenValidatorFilter;

    public ProdSecurityConfig(JWTTokenGeneratorFilter jwtTokenGeneratorFilter, JWTTokenValidatorFilter jwtTokenValidatorFilter) {
        this.jwtTokenGeneratorFilter = jwtTokenGeneratorFilter;
        this.jwtTokenValidatorFilter = jwtTokenValidatorFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{


        http
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(smc -> smc.invalidSessionUrl("/invalidSession").maximumSessions(300).maxSessionsPreventsLogin(true))
                .addFilterAfter(jwtTokenGeneratorFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(jwtTokenValidatorFilter, BasicAuthenticationFilter.class)
//                .requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()) // Only HTTP
                .authorizeHttpRequests(auth -> auth
                        // Public routes
                        .requestMatchers("/api/user/create","/api/car/getAll","/api/google/callback", "/api/user/login", "/api/test/","/api/cloudinary/upload","/api/test/email").permitAll()

                        // User routes (Authenticated)
                        .requestMatchers("/api/user/update", "/api/user/get", "/api/user/get/{userId}",
                                "/api/user/delete").authenticated()

                        // Car routes (Only for HOST)
                        .requestMatchers("/api/car/create", "/api/car/update/{carId}",
                                "/api/car/delete/{carId}").hasRole("HOST")

                        .requestMatchers("/api/car/get/{carId}", "/api/car/getAllSpecific").authenticated()

                        // Booking routes
                        .requestMatchers("/api/booking/create/{carId}", "/api/booking/update/{bookingId}",
                                "/api/booking/delete/{bookingId}", "/api/booking/getByRenter").hasRole("RENTER")
                        .requestMatchers("/api/booking/get/{bookingId}").authenticated()
                        .requestMatchers("/api/booking/getByCar/{carId}",
                                "/api/booking/getByHost").hasRole("HOST")

                        // Location routes (Only for HOST)
                        .requestMatchers("/api/location/create/{carId}", "/api/location/update/{locationId}",
                                "/api/location/delete/{locationId}").hasRole("HOST")
                        .requestMatchers("/api/location/get/{locationId}").authenticated()

                        // Notification, Payment, Promotion, Review (Authenticated)
                        .requestMatchers("/api/notification/**", "/api/payment/**", "/api/promotion/**",
                                "/api/review/**").authenticated()

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                );
//        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                .requestMatchers("/api/host/**").hasRole("HOST")
//                .requestMatchers("/api/renter/**").hasRole("RENTER")



        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(Customizer.withDefaults());
        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }


    // Checks if the password is compromised or not
//    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker(){
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        GoRentzyyProdUsernamePwdAuthenticationProvider authenticationProvider =
                new GoRentzyyProdUsernamePwdAuthenticationProvider(userDetailsService,passwordEncoder);

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }
}
