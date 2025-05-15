package shop.shop_spring.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${spring.jwt.secret}")
    private String secretKeyString;
    private static SecretKey key;

    @PostConstruct
    public void init(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // JWT 만들어주는 함수
    public static String createToken(Authentication auth) {
        var user = (MyUser) auth.getPrincipal();
        String jwt = Jwts.builder()
                .claim("username", user.getUsername())
                .claim("id", user.getId().toString())
                .claim("nickname", user.getNickname())
                .claim("name", user.getName())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10000)) //유효기간 10초
                .signWith(key)
                .compact();
        return jwt;
    }

    // JWT 디코딩
    public static Claims extractToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
        return claims;
    }

}