package shop.shop_spring.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    String[] urlsBePermittedAll = {
            "/products/**",
            "/main",
            "/members/register",
            "/members/login",
            "/members/password-reset",
            "/members/change-password",
            "/categories/**"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring()
                .requestMatchers(PathRequest
                        .toStaticResources()
                        .atCommonLocations()
                );
    }

    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf((csrf) -> csrf.disable()); // csrf 끄기

        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 커스텀 필터 추가
        http.addFilterBefore(jwtAuthenticationFilter, ExceptionTranslationFilter.class);

        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(urlsBePermittedAll).permitAll()
                        .requestMatchers("/members/my-page/**", "/cart/**").authenticated())

                .logout(logout -> logout.permitAll());

        // .formLogin(...) 은 session 방식에서 사용함 빼야함

        return http.build();
    }
}
