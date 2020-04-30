package com.mitrais.chipper.temankondangan.backendapps.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value("spring.mail.username")
    String username;

    @Value("spring.mail.password")
    String password;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");

        return mailSender;
    }

    @Bean
    public SimpleMailMessage templateForgotPasswordMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText(
                "We have received a request to reset the password for your %s account.\n" +
                        "To reset your password, please enter this verification code:\n" +
                        "\n" +
                        "%d\n" +
                        "\n" +
                        "If you're didn't request, Please ignore this email.\n" +
                        "\n" +
                        "Regards,\n" +
                        "Teman Kondangan\n" +
                        "\n" +
                        "PS: Please do not reply to this message, it was send from server.");
        return message;
    }
}
