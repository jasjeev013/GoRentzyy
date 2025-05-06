package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.services.SMSService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SMSServiceImpl implements SMSService {


    @Value("${api.twilio.auth.id}")
    private String Account_SID;
    @Value("${api.twilio.auth.token}")
    private String Auth_Token;
    @Value("${api.twilio.outgoing.sms.number}")
    private String Outgoing_SMS_Number;

    @PostConstruct
    private void setup(){
        Twilio.init(Account_SID,Auth_Token);
    }

    @Override
    public ResponseEntity<ApiResponseObject> sendSMS(String smsNumber, String smsMessage) {
        Message message = Message.creator(
                new PhoneNumber(smsNumber),
                new PhoneNumber(Outgoing_SMS_Number),
                smsMessage).create();
        return new ResponseEntity<>(new ApiResponseObject("Message Sent Successfully",true,null), HttpStatus.OK);
    }
}
