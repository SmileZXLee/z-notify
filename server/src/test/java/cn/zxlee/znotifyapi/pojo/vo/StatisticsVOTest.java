package cn.zxlee.znotifyapi.pojo.vo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatisticsVOTest {

    @Test
    void testGetIp_desensitization() {
        StatisticsVO vo = new StatisticsVO();

        // IPv4 desensitization
        vo.setIp("59.61.104.136");
        assertEquals("59.61.104.*", vo.getIp());

        vo.setIp("127.0.0.1");
        assertEquals("127.0.0.*", vo.getIp());

        // IPv6 desensitization
        vo.setIp("2001:db8:85a3:8d3:1319:8a2e:370:7348");
        assertEquals("2001:db8:85a3:8d3:1319:8a2e:370:*", vo.getIp());

        // Null and empty checks
        vo.setIp(null);
        assertNull(vo.getIp());

        vo.setIp("");
        assertEquals("", vo.getIp());

        // Invalid format
        vo.setIp("invalidip");
        assertEquals("invalidip", vo.getIp());
    }
}
