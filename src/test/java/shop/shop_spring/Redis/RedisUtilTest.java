package shop.shop_spring.Redis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("redis")
public class RedisUtilTest {
    @Autowired private RedisUtil redisUtil;
    @Autowired private StringRedisTemplate stringRedisTemplate; // 테스트 후 정리용

    @BeforeEach
    void setUp() {
        stringRedisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void 기본_데이터_입력_조회() throws InterruptedException{
        // Given
        String key = "testKey";
        String value = "testValue";
        long duration = 1;

        // When: 데이터 저장 테스트
        redisUtil.setDataExpire(key, value, duration);

        // Then: 데이터 저장, 만료 확인
        assertEquals(value, redisUtil.getData(key));
        assertTrue(redisUtil.existData(key));

        Thread.sleep(duration * 1000 + 500);
        assertNull(redisUtil.getData(key));
        assertFalse(redisUtil.existData(key));
    }

    @AfterEach
    void tearDown() {
        // 테스트 종료 후 Redis 상태를 다시 초기화 (정리)
        stringRedisTemplate.getConnectionFactory().getConnection().flushAll();
    }
}
