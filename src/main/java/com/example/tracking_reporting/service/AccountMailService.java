package com.example.tracking_reporting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountMailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@tracking-reporting.local}")
    private String from;

    @Value("${app.frontend.login-url:http://localhost:3000/login}")
    private String loginUrl;

    public void sendAccountCreatedEmail(String to,
                                        String firstName,
                                        String username,
                                        String password) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Your Tracking Reporting account");
        message.setText(
                "Bonjour " + firstName + ",\n\n" +
                        "Votre compte a été créé avec succès.\n\n" +
                        "Nom d'utilisateur : " + username + "\n" +
                        "Mot de passe : " + password + "\n\n" +
                        "Lien de connexion : " + loginUrl + "\n\n" +
                        "Vous pouvez garder ce mot de passe ou le modifier après connexion.\n\n" +
                        "Cordialement,\n" +
                        "Tracking Reporting"
        );

        mailSender.send(message);
    }
}