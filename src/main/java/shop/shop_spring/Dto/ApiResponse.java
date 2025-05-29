package shop.shop_spring.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    public static Map<String, String> createResponseData(String key, String message){
        Map<String, String> responseData = new HashMap<>();
        responseData.put(key, message);;
        return responseData;
    }

    public static Map<String, String> createResponseData(List<String> key, List<String> data){
        if (key.size() != data.size()) {
            throw new IllegalArgumentException("입력된 key와 data의 길이가 다름");
        }
        Map<String, String> responseData = new HashMap<>();
        for (int i = 0; i < key.size(); i++){
            responseData.put(key.get(i), data.get(i).toString());
        }
        return responseData;
    }
}
