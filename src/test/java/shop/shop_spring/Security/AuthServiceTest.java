package shop.shop_spring.Security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServiceTest {
    @Autowired private AuthService authService;

    @MockitoBean
    private AuthenticationManagerBuilder authenticationManagerBuilderMock;

    @MockitoBean
    private JwtUtil jwtUtil;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 로그인_성공() {
        // Given
        String testUsername = "testUser@example.com";
        String testPassword = "password";
        String expectedJwtToken = "mocked-jwt-token"; // Mock으로 반환할 JWT 토큰
        // Mock Authentication 객체 생성 및 설정
        Authentication authenticatedAuthMock = mock(Authentication.class);

        // Mock AuthenticationManager 설정
        AuthenticationManager mockAuthenticationManager = mock(AuthenticationManager.class);
        when(authenticationManagerBuilderMock.getObject()).thenReturn(mockAuthenticationManager);
        when(mockAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticatedAuthMock);

        // *** SecurityContextHolder static Mocking 시작 ***
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {

            //  Mock SecurityContext 생성
            SecurityContext mockSecurityContext = mock(SecurityContext.class);

            // SecurityContextHolder.getContext() 호출 시 mockSecurityContext 반환하도록 스텁
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            // mockSecurityContext.setAuthentication() 호출은 무시 (또는 verify)
            doNothing().when(mockSecurityContext).setAuthentication(any(Authentication.class)); // 어떤 Authentication 객체가 오든 무시

            // *** 중요: mockSecurityContext.getAuthentication() 호출 시 authenticatedAuthMock 반환하도록 스텁 ***
            when(mockSecurityContext.getAuthentication()).thenReturn(authenticatedAuthMock);


            // Mock JwtUtil 설정
            // JwtUtil.createToken이 mockSecurityContext.getAuthentication()이 반환하는 객체(authenticatedAuthMock)를 인자로 받을 때
            // expectedJwtToken을 반환하도록 스텁
            when(jwtUtil.createToken(authenticatedAuthMock)).thenReturn(expectedJwtToken);


        // When
        // AuthService.login 메서드 호출
            String actualJwtToken = authService.login(testUsername, testPassword);

            // Then
            // 1. login 메서드가 기대한 JWT 토큰을 반환했는지 검증
            assertEquals(expectedJwtToken, actualJwtToken, "로그인 성공 시 JWT 토큰이 올바르게 반환되어야 합니다.");

            // 2. SecurityContextHolder 관련 검증 (static mocking 시 verify 방식 변경)
            mockedSecurityContextHolder.verify(SecurityContextHolder::getContext, times(2)); // getContext()가 두 번 호출됨 (set, get)
            verify(mockSecurityContext, times(1)).setAuthentication(authenticatedAuthMock); // setAuthentication이 정확한 객체로 호출되었는지
            verify(mockSecurityContext, times(1)).getAuthentication(); // getAuthentication이 호출되었는지 확인 (JwtUtil 호출 전)


            // 3. Mock 객체의 메서드가 기대한 대로 호출되었는지 검증
            verify(authenticationManagerBuilderMock, times(1)).getObject();
            verify(mockAuthenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            // JwtUtil.createToken()이 authenticatedAuthMock 객체를 인자로 받아 1번 호출되었는지 검증
            verify(jwtUtil, times(1)).createToken(authenticatedAuthMock);

            // try-with-resources 블록이 끝나면 static mocking이 자동으로 해제됩니다.

        } // *** SecurityContextHolder static Mocking 끝 ***

    }

    @Test
    void 로그인_실패(){
        // Given
        String testUsername = "wrongUser@example.com";
        String testPassword = "wrongpassword";
        String expectedJwtToken = "mocked-jwt-token"; // Mock으로 반환할 JWT 토큰

        AuthenticationManager authenticationManagerMock = mock(AuthenticationManager.class);

        when(authenticationManagerBuilderMock.getObject()).thenReturn(authenticationManagerMock);

        // 사용자인증 토큰 인증 시 무조건 예외처리 -> 실패
        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class))).
                thenThrow(new BadCredentialsException("잘못된 사용자 정보"));

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock =
                     Mockito.mockStatic(SecurityContextHolder.class)) {

            SecurityContext securityContextMock = mock(SecurityContext.class);

            securityContextHolderMock.when(SecurityContextHolder::getContext).
                    thenReturn(securityContextMock);

            BadCredentialsException thrown = assertThrows(BadCredentialsException.class,
                    () -> {
                        authService.login(testUsername, testPassword);
                    }, "잘못된 로그인 요청은 BadCredentialsException 발생");

            assertEquals("잘못된 사용자 정보", thrown.getMessage());
            // 인증 처리를 위해 builder, manager는 한 번씩 호출됨
            verify(authenticationManagerBuilderMock, times(1)).getObject();
            verify(authenticationManagerMock, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));

            // 인증 실패 했으므로 그 아래 코드들은 실행되지 않아야 함
            verify(jwtUtil, never()).createToken(any(Authentication.class)); // createToken은 호출되지 않아야 함
            verify(securityContextMock, never()).setAuthentication(any(Authentication.class));
            verify(securityContextMock, never()).getAuthentication();

        }
    }

}
