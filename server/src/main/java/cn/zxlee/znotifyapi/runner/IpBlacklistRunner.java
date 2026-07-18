package cn.zxlee.znotifyapi.runner;

import cn.zxlee.znotifyapi.mapper.IpBlacklistMapper;
import cn.zxlee.znotifyapi.pojo.po.IpBlacklistPO;
import cn.zxlee.znotifyapi.utils.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: z-notify-api
 * @description: IpBlacklistRunner
 * @author: zxlee
 * @create: 2026-07-18
 **/

@Slf4j
@Component
public class IpBlacklistRunner implements CommandLineRunner {

    @Autowired
    private IpBlacklistMapper ipBlacklistMapper;

    @Autowired
    private RedisCache redisCache;

    private static final String BLACKLIST_KEY = "ip:blacklist";

    @Override
    public void run(String... args) throws Exception {
        log.info("Loading IP blacklist from database to Redis...");
        try {
            List<IpBlacklistPO> list = ipBlacklistMapper.list(new HashMap<>());
            if (list != null && !list.isEmpty()) {
                Set<String> ips = list.stream().map(IpBlacklistPO::getIp).collect(Collectors.toSet());
                // Clean the existing set in Redis and reload
                redisCache.redisTemplate.delete(BLACKLIST_KEY);
                redisCache.redisTemplate.opsForSet().add(BLACKLIST_KEY, ips.toArray(new String[0]));
                log.info("Successfully loaded {} blacklisted IPs to Redis.", ips.size());
            } else {
                log.info("No blacklisted IPs found in database.");
            }
        } catch (Exception e) {
            log.error("Failed to load IP blacklist to Redis", e);
        }
    }
}
