package cn.zxlee.znotifyapi.interceptor;

import cn.zxlee.znotifyapi.mapper.IpBlacklistMapper;
import cn.zxlee.znotifyapi.pojo.po.IpBlacklistPO;
import cn.zxlee.znotifyapi.utils.IpUtils;
import cn.zxlee.znotifyapi.utils.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @program: z-notify-api
 * @description: IpLimitInterceptor
 * @author: zxlee
 * @create: 2026-07-18
 **/

@Slf4j
@Component
public class IpLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IpBlacklistMapper ipBlacklistMapper;

    @Value("${ip.limit.time-window:60}")
    private int timeWindow;

    @Value("${ip.limit.max-requests:100}")
    private int maxRequests;

    private static final String BLACKLIST_KEY = "ip:blacklist";
    private static final String COUNT_KEY_PREFIX = "ip:count:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = IpUtils.getIpAddr(request);
        if (ip == null || ip.isEmpty()) {
            return true;
        }

        // 1. Check if IP is blacklisted in Redis
        Boolean isBlacklisted = redisCache.redisTemplate.opsForSet().isMember(BLACKLIST_KEY, ip);
        if (Boolean.TRUE.equals(isBlacklisted)) {
            log.warn("Blocked request from blacklisted IP: {}", ip);
            sendForbiddenResponse(response);
            return false;
        }

        // 2. Count requests in time window
        String countKey = COUNT_KEY_PREFIX + ip;
        Long count = redisCache.redisTemplate.opsForValue().increment(countKey);
        if (count != null) {
            if (count == 1) {
                redisCache.redisTemplate.expire(countKey, timeWindow, TimeUnit.SECONDS);
            } else {
                Long ttl = redisCache.redisTemplate.getExpire(countKey, TimeUnit.SECONDS);
                if (ttl != null && ttl == -1) {
                    redisCache.redisTemplate.expire(countKey, timeWindow, TimeUnit.SECONDS);
                }
            }

            // 3. Check if count exceeds the limit
            if (count > maxRequests) {
                log.error("IP {} exceeded limit ({} > {}). Blacklisting permanently.", ip, count, maxRequests);
                blacklistIp(ip);
                sendForbiddenResponse(response);
                return false;
            }
        }

        return true;
    }

    private void blacklistIp(String ip) {
        try {
            // Add to Redis blacklist set
            redisCache.redisTemplate.opsForSet().add(BLACKLIST_KEY, ip);

            // Check if already in MySQL to avoid DuplicateKeyException
            IpBlacklistPO existing = ipBlacklistMapper.getByIp(ip);
            if (existing == null) {
                IpBlacklistPO po = new IpBlacklistPO();
                po.setIp(ip);
                po.setCreatetime(new Date());
                ipBlacklistMapper.insertOne(po);
                log.info("Permanently blacklisted IP {} in database.", ip);
            }
        } catch (org.springframework.dao.DuplicateKeyException e) {
            log.warn("IP {} is already blacklisted in database (duplicate key).", ip);
        } catch (Exception e) {
            log.error("Failed to blacklist IP {} in database", ip, e);
        }
    }

    private void sendForbiddenResponse(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().append("{\"code\":403,\"msg\":\"Access denied: IP permanently blacklisted due to excessive requests.\"}");
    }
}
