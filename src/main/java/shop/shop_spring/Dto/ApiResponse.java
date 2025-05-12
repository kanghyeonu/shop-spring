package shop.shop_spring.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    // 성공 응답을 생성하는 정적 팩토리 메서드
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(HttpStatus.OK.value(), message, null);
    }

    // 실패 응답을 생성하는 정적 팩토리 메서드
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status.value(), message, null);
    }

    // 오버로드: 데이터가 없는 성공 응답
    public static ApiResponse<Void> successNoData(String message) {
        return new ApiResponse<>(HttpStatus.OK.value(), message, null);
    }

    // 오버로드: 데이터가 없는 실패 응답
    public static ApiResponse<Void> errorNoData(HttpStatus status, String message) {
        return new ApiResponse<>(status.value(), message, null);
    }
}
