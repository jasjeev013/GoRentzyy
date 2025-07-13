package com.gorentzyy.backend.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ForgotPasswordRequest {
    private String email;
}
