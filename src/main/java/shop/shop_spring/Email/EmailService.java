package shop.shop_spring.Email;

import jakarta.mail.MessagingException;
import shop.shop_spring.Dto.EmailDto;

import java.io.UnsupportedEncodingException;

public interface EmailService {
    String sendMail(EmailDto dto) throws MessagingException, UnsupportedEncodingException;
}