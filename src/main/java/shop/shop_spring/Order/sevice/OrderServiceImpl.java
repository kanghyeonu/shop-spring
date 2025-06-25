package shop.shop_spring.Order.sevice;

import jakarta.persistence.OneToOne;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.shop_spring.Exception.DataNotFoundException;
import shop.shop_spring.Exception.InsufficientStockException;
import shop.shop_spring.Member.domain.Member;
import shop.shop_spring.Member.service.MemberService;
import shop.shop_spring.Order.Dto.DeliveryInfo;
import shop.shop_spring.Order.Dto.OrderDetailDto;
import shop.shop_spring.Order.domain.Delivery;
import shop.shop_spring.Order.domain.Order;
import shop.shop_spring.Order.domain.OrderItem;
import shop.shop_spring.Order.repository.OrderRepository;
import shop.shop_spring.Payment.Dto.PaymentInitiationResponse;
import shop.shop_spring.Payment.service.PaymentService;
import shop.shop_spring.Product.domain.Product;
import shop.shop_spring.Product.service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final PaymentService paymentService;

    @Value("${app.payment.success-callback-url}")
    private String successCallbackUrl;
    @Value("${app.payment.failure-callback-url}")
    private String failureCallbackUrl;

    @Transactional
    @Override
    public PaymentInitiationResponse placeOrder(Long memberId, Long productId, int quantity, DeliveryInfo deliveryInfo, String paymentMethod) {
        // 1. 주문 상품 및 회원 조회
        Member member = memberService.findById(memberId);
        Product product = productService.findById(productId);

        // 2. 재고 체크
        if (product.getStockQuantity() < quantity){
            throw new InsufficientStockException("상품 재고 부족");
        }

        // 3. 주문 상품 생성
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .orderPrice(product.getPrice())
                .count(quantity)
                .productNameAtOrder(product.getTitle())
                .build();

        // 4. 배송 정보 생성
        Delivery delivery = Delivery.builder()
                .receiverName(deliveryInfo.getReceiverName())
                .address(deliveryInfo.getAddress())
                .deliveryMessage(deliveryInfo.getDeliveryMessage())
                .status(Delivery.DeliveryStatus.READY)
                .build();


        // 5. 주문 생성
        Order order = Order.builder()
                .orderer(member)
                .orderDate(LocalDateTime.now())
                .status(Order.OrderStatus.PENDING)
                .totalAmount(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .orderItems(new ArrayList<>())
                .paymentMethod(paymentMethod)
                .build();

        // 연관 관계 설정
        order.addOrderItem(orderItem);
        order.setDelivery(delivery);

        // 6. 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 7. 결제 시스템 결제 요청
        PaymentInitiationResponse initiationResponse = paymentService.initiatePayment(
                savedOrder.getId(),
                savedOrder.getTotalAmount(),
                savedOrder.getPaymentMethod(),
                this.successCallbackUrl,
                this.failureCallbackUrl
        );

        return initiationResponse;
    }

    @Override
    public Long placeCartOrder(Long memberId, DeliveryInfo deliveryInfo) {
        return 0L;
    }

    @Override
    public void cancelOrder(Long memberId, Long orderId) {

    }

    @Override
    public OrderDetailDto getOrderDetails(Long memberId, Long orderId) {
        return null;
    }

    @Override
    public List<OrderDetailDto> getOrdersByMember(Long memberId) {
        return List.of();
    }
    @Transactional
    @Override
    public void handlePaymentSuccessCallback(Long orderId) {
        System.out.println(orderId);
        // 1. 성공 주문 조회
        Order order = orderRepository.findByIdWithOrderItemsAndProduct(orderId)
                .orElseThrow(() -> {
                    throw new DataNotFoundException("결제 성공 처리 중 주문 찾기 실패");
                });

        // 2. 주문 상태 확인(중복 처리 방지 등)
        if (order.getStatus() != Order.OrderStatus.PENDING){
            System.out.println("주문이 이미 처리 됐음" + order.getStatus().toString());
            return;
        }

        // 3. 주문 상태 갱신
        order.setStatus(Order.OrderStatus.PAID);

        // 4. 주문 후속 처리
        for (OrderItem orderItem : order.getOrderItems()){
            Product product = orderItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - orderItem.getCount());
        }

        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void handlePaymentFailureCallback(Long orderId) {
        // 1. 성공 주문 조회
        Order order = orderRepository.findByIdWithOrderItemsAndProduct(orderId)
                .orElseThrow(() -> {
                    throw new DataNotFoundException("결제 성공 처리 중 주문 찾기 실패");
                });

        // 2. 주문 상태 확인(중복 처리 방지)
        if (order.getStatus() != Order.OrderStatus.PENDING){
            System.out.println("주문이 이미 취소 처리 됐음" + order.getStatus().toString());
            return;
        }

        // 3. 주문 상태 갱신
        order.setStatus(Order.OrderStatus.CANCELED);

        orderRepository.save(order);
    }
}
