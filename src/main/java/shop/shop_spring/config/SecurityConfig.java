package shop.shop_spring.config;

import org.aspectj.weaver.BCException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable()); // csrf 끄기


        // <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
        // csrf를 킨다면 form에 위의 코드를 삽입해줘서 서버가 발급한 token 정보도 서버에게 전달 해야함
        // csrf를 키면 ajax에서도 토큰을 넣어줘야함 -> 검색 해보자
        http.csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository())
                .ignoringRequestMatchers("/**") // 보안을 끌 페이지 설정 안하는게 좋음
        );

        http.authorizeHttpRequests((authorize) ->
                authorize.requestMatchers("/**").permitAll() // permitAll 모든 유저의 접속을 허락
        );
        // session login
        // 폼 형태로 로그인
        http.formLogin((formLogin) -> formLogin.loginPage("/login")
                .defaultSuccessUrl("/my-page")
                //.failureUrl("/login?error=true")
        );
        http.logout(logout -> logout.logoutUrl("/logout")
                .logoutSuccessUrl("/items/list")
        );

        return http.build();
    }

}
