package shop.shop_spring.Exception;

public class InvalidOrderStatusException extends RuntimeException{
    public InvalidOrderStatusException(String message){
        super(message);
    }
}
