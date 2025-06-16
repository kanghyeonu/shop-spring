package shop.shop_spring.Cart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shop_spring.Cart.domain.Cart;
import shop.shop_spring.Cart.domain.CartItem;
import shop.shop_spring.Cart.dto.CartDto;
import shop.shop_spring.Cart.repository.CartItemRepository;
import shop.shop_spring.Cart.repository.CartRepository;
import shop.shop_spring.Member.service.MemberService;
import shop.shop_spring.Product.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock
    private MemberService memberService;
    @Mock
    private ProductService productService;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    @Mock // <--- Cart 객체를 Mock 객체로 생성
    private Cart mockCart;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void 장바구니_조회_성공(){
        // given
        Long memberId = 1L;
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(CartItem.builder().id(100L).cart(mockCart).build());
        cartItems.add(CartItem.builder().id(101L).cart(mockCart).build());
        mockCart.setCartItems(cartItems);

        when(mockCart.getCartItems()).thenReturn(cartItems);
        when(cartRepository.findByMemberIdWithItemsAndProducts(memberId)).thenReturn(Optional.of(mockCart));

        CartDto expectedCartDto = new CartDto();

        try (MockedStatic<CartDto> mockCartDto = Mockito.mockStatic(CartDto.class)) {
            mockCartDto.when(()-> CartDto.fromEntity(any(Cart.class))).thenReturn(expectedCartDto);

            // when
            CartDto result = cartService.getCartForMember(memberId);

            // then
            assertNotNull(result, "상품이 존재해야함");
            assertEquals(expectedCartDto, result);

            verify(cartRepository, times(1)).findByMemberIdWithItemsAndProducts(memberId);
            mockCartDto.verify(() -> CartDto.fromEntity(mockCart), times(1));

            verifyNoInteractions(memberService, productService);

        }
    }

    @Test
    void 장바구니_조회_실패_장바구니_존재하지않음(){
        // given
        Long memberId = 2L;

        when(cartRepository.findByMemberIdWithItemsAndProducts(memberId)).thenReturn(Optional.empty());

        try (MockedStatic<CartDto> mockCartDto = Mockito.mockStatic(CartDto.class)) {
            //when
            CartDto result = cartService.getCartForMember(memberId);

            // then
            assertNull(result);

            verify(cartRepository, times(1)).findByMemberIdWithItemsAndProducts(memberId);

            mockCartDto.verify(() -> CartDto.fromEntity(any(Cart.class)), never());

            verifyNoInteractions(memberService, productService, cartItemRepository);
        }
    }

    @Test
    void 장바구니_조회_실패_장바구니_내_상품_존재하지않음(){
        // given
        Long memberId = 3L;

        List<CartItem> emptyCartItems = new ArrayList<>();

        when(mockCart.getCartItems()).thenReturn(emptyCartItems);
        when(cartRepository.findByMemberIdWithItemsAndProducts(memberId)).thenReturn(Optional.of(mockCart));

        try (MockedStatic<CartDto> mockCartDto = Mockito.mockStatic(CartDto.class)) {
            // when
            CartDto result = cartService.getCartForMember(memberId);

            assertNull(result);
            verify(cartRepository, times(1)).findByMemberIdWithItemsAndProducts(memberId);
            mockCartDto.verify(() -> CartDto.fromEntity(any(Cart.class)), never());

            verifyNoInteractions(memberService, productService, cartItemRepository);
        }
    }
}
