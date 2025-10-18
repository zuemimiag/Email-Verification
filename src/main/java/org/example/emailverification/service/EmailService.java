package org.example.emailverification.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Async
    public void send(String to, String token){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("zuemimiaung.dev@gmail.com");
            message.setSubject("confirm your email");
            String messageBody =
            """
                   Thank you for registration. Please confirm your email.
                    """.formatted(token);
            message.setText(messageBody);
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
