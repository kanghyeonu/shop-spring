package shop.shop_spring.Payment.service;

import shop.shop_spring.Payment.Dto.PaymentInitiationResponse;

import java.util.Map;

public interface PgApiClient {
    void setCredentials(String apiKey, String apiSecret);

    Map<String, Object> requestPaymentInitiation(Map<String, Object> requestData);

    PaymentInitiationResponse parseInitiationResponse(Map<String, Object> pgResponse);
}
