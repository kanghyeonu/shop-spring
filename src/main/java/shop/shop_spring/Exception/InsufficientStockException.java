package shop.shop_spring.Exception;

public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(String message){super(message);}
}
