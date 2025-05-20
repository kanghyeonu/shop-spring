package shop.shop_spring.Exception;

import jakarta.mail.MessagingException;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.shop_spring.Dto.ApiResponse;

import java.io.UnsupportedEncodingException;

@RestControllerAdvice
public class GlobalExceptionHandler  {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e){
        System.err.println("IllegalArgumentException: " + e.getMessage());

        ApiResponse<Void> errorResponse = ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataNotFoundExcetpion(DataNotFoundException e){
        System.err.println("DataNotFoundException: " + e.getMessage());
        // 표준 에러 응답 반환
        ApiResponse<Void> errorResponse = ApiResponse.errorNoData(HttpStatus.BAD_REQUEST,
                e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessagingException(MessagingException e) {
        // 에러 로그 기록 (필요 시)
        System.err.println("MessagingException 발생: " + e.getMessage());
        // 표준 에러 응답 반환
        ApiResponse<Void> errorResponse = ApiResponse.errorNoData(HttpStatus.INTERNAL_SERVER_ERROR,
                "이메일 전송 중 시스템 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // UnsupportedEncodingException 발생 시 처리
    @ExceptionHandler(UnsupportedEncodingException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedEncodingException(UnsupportedEncodingException e) {
        System.err.println("UnsupportedEncodingException 발생: " + e.getMessage());
        ApiResponse<Void> errorResponse = ApiResponse.errorNoData(HttpStatus.INTERNAL_SERVER_ERROR,
                "이메일 데이터 처리 중 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException e){
        System.err.println("BadCredentialsException 발생:" + e.getMessage());
        ApiResponse<Void> errorResponse = ApiResponse.errorNoData(HttpStatus.BAD_REQUEST,
                "잘못된 아이디 또는 비밀번호");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 그 외 모든 예상치 못한 Exception 발생 시 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {

        // e.printStackTrace(); // 중요 에러이므로 로깅하면 좋음
        ApiResponse<Void> errorResponse = ApiResponse.errorNoData(HttpStatus.INTERNAL_SERVER_ERROR,
                "서버 내부 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
