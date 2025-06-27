package com.campus.trade.service.impl;

import com.campus.trade.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    public static final String VERIFICATION_CODE_KEY_PREFIX = "verification_code:";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendVerificationCode(String to) {
        String code = generateVerificationCode();

        // 将验证码存入 Redis，有效期5分钟
        redisTemplate.opsForValue().set(VERIFICATION_CODE_KEY_PREFIX + to, code, 5, TimeUnit.MINUTES);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("2Hand - 您的注册验证码");
        message.setText("欢迎注册2Hand校园二手平台！您的验证码是：" + code + "，有效期为5分钟。");

        try {
            mailSender.send(message);
            log.info("已成功向 {} 发送验证码: {}", to, code);
        } catch (Exception e) {
            log.error("向 {} 发送邮件失败", to, e);
            // 在实际项目中，这里应该抛出自定义异常
        }
    }

    private String generateVerificationCode() {
        // 生成一个6位的随机数字验证码
        return String.format("%06d", new Random().nextInt(999999));
    }
}