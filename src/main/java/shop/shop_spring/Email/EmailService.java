package shop.shop_spring.Email;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface EmailService {
    String sendMail(EmailDto dto) throws MessagingException, UnsupportedEncodingException;
}