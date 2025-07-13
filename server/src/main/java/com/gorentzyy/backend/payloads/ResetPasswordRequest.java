package com.gorentzyy.backend.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
