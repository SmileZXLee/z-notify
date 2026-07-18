package cn.zxlee.znotifyapi.interceptor;

import cn.zxlee.znotifyapi.mapper.IpBlacklistMapper;
import cn.zxlee.znotifyapi.pojo.po.IpBlacklistPO;
import cn.zxlee.znotifyapi.utils.redis.RedisCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class IpLimitInterceptorTest {

    @InjectMocks
    private IpLimitInterceptor interceptor;

    @Mock
    private RedisCache redisCache;

    @Mock
    private IpBlacklistMapper ipBlacklistMapper;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private SetOperations setOperations;

    @Mock
    private ValueOperations valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        redisCache.redisTemplate = redisTemplate;
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Inject configuration values using ReflectionTestUtils
        ReflectionTestUtils.setField(interceptor, "timeWindow", 60);
        ReflectionTestUtils.setField(interceptor, "maxRequests", 5);
    }

    @Test
    void testPreHandle_whenNotBlacklistedAndUnderLimit() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Mock Redis checks: not blacklisted
        when(setOperations.isMember(eq("ip:blacklist"), eq("192.168.1.100"))).thenReturn(false);

        // Mock increment: count is 1 (first request)
        when(valueOperations.increment(eq("ip:count:192.168.1.100"))).thenReturn(1L);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verify(redisTemplate).expire(eq("ip:count:192.168.1.100"), eq(60L), eq(TimeUnit.SECONDS));
    }

    @Test
    void testPreHandle_whenBlacklisted() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Mock Redis check: blacklisted
        when(setOperations.isMember(eq("ip:blacklist"), eq("192.168.1.100"))).thenReturn(true);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        assertEquals(403, response.getStatus());
        assertTrue(response.getContentAsString().contains("permanently blacklisted"));
    }

    @Test
    void testPreHandle_exceedLimit_shouldBlacklist() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Mock Redis checks: not blacklisted
        when(setOperations.isMember(eq("ip:blacklist"), eq("192.168.1.100"))).thenReturn(false);

        // Mock increment: count is 6 (limit is 5)
        when(valueOperations.increment(eq("ip:count:192.168.1.100"))).thenReturn(6L);

        // Mock database check: not in db yet
        when(ipBlacklistMapper.getByIp(eq("192.168.1.100"))).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        assertEquals(403, response.getStatus());

        // Verify blacklisting logic
        verify(setOperations).add(eq("ip:blacklist"), eq("192.168.1.100"));
        verify(ipBlacklistMapper).insertOne(any(IpBlacklistPO.class));
    }
}
