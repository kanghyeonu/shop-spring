package shop.shop_spring.Security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import shop.shop_spring.Member.enums.Role;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServiceTest {
    @Autowired private AuthService authService;

    @MockitoBean
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @MockitoBean
    private AuthenticationManager mockAuthenticationManager = mock(AuthenticationManager.class);

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp(){
        // SecurityContextHolder은 static -> mocking을 위해 테스트 전 읨의로
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void 로그인_성공(){
        // Given
        String testUsername = "testUser";
        String testPassword = "password";
        String expectedJwtToken = "mocked-jwt-token"; // Mock으로 반환할 JWT 토큰

        // Mock Authentication 객체 생성 및 설정 (authenticate 메서드의 반환값)
        Authentication authenticatedAuthMock = mock(Authentication.class);
        when(authenticatedAuthMock.getName()).thenReturn(testUsername);
        // 필요에 따라 authenticatedAuthMock에 대한 추가 Stubbing

        // AuthenticationManagerBuilder가 getObject() 호출 시 Mock AuthenticationManager 반환
        when(authenticationManagerBuilder.getObject()).thenReturn(mockAuthenticationManager);

        // Mock AuthenticationManager의 authenticate() 호출 시 Mock Authentication 객체 반환
        when(mockAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticatedAuthMock);

        // JwtUtil 스태틱 메서드 Mocking 시작
        // try-with-resources 구문을 사용하여 Mocking 범위 지정
        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {

            // JwtUtil.createToken()이 특정 Authentication 객체(authenticatedAuthMock)를 받으면
            // 기대한 JWT 토큰(expectedJwtToken)을 반환하도록 설정
            mockedJwtUtil.when(() -> JwtUtil.createToken(authenticatedAuthMock))
                    .thenReturn(expectedJwtToken);

            // When
            // AuthService.login 메서드 호출
            String actualJwtToken = authService.login(testUsername, testPassword);

            // Then
            // 1. login 메서드가 기대한 JWT 토큰을 반환했는지 검증
            assertEquals(expectedJwtToken, actualJwtToken, "로그인 성공 시 JWT 토큰이 올바르게 반환되어야 합니다.");

            // 2. SecurityContextHolder에 인증 정보가 올바르게 설정되었는지 검증
            // (이 부분은 앞서 설명드린 대로 Authentication 객체가 Mock 객체와 동일한지 등 확인)
            assertEquals(authenticatedAuthMock, SecurityContextHolder.getContext().getAuthentication(), "SecurityContextHolder에 설정된 인증 정보가 Mock Authentication 객체와 일치해야 합니다.");
            assertEquals(testUsername, SecurityContextHolder.getContext().getAuthentication().getName(), "SecurityContextHolder에 설정된 인증 정보의 principal이 일치해야 합니다.");


            // 3. Mock 객체의 메서드가 기대한 대로 호출되었는지 검증
            verify(authenticationManagerBuilder, times(1)).getObject();
            verify(mockAuthenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));

            // 4. JwtUtil.createToken() 스태틱 메서드가 기대한 인자로 호출되었는지 검증
            mockedJwtUtil.verify(() -> JwtUtil.createToken(authenticatedAuthMock), times(1));
        }
    }

    @Test
    void 로그인_실패(){

    }


}
