package com.gorentzyy.backend.services;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import org.springframework.http.ResponseEntity;

public interface SMSService {

    ResponseEntity<ApiResponseObject> sendSMS(String smsNumber,String smsMessage);

}
