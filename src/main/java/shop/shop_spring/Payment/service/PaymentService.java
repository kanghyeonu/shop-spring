package shop.shop_spring.Payment.service;

import shop.shop_spring.Payment.Dto.PaymentInitiationResponse;

import java.math.BigDecimal;

public interface PaymentService {
    PaymentInitiationResponse initiatePayment(
            Long orderId,
            BigDecimal amount,
            String paymentMethod,
            String successCallbackUrl,
            String failureCallbackUrl);
}
