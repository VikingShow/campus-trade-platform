package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-code")
    public Result<Void> sendCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        emailService.sendVerificationCode(email);
        return Result.success();
    }
}