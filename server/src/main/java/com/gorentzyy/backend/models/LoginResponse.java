package com.gorentzyy.backend.models;

public record LoginResponse(String status,String jwtToken,String role) {
}
