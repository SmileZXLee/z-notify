package cn.zxlee.znotifyapi.pojo.po;

import cn.zxlee.znotifyapi.pojo.BasePOJO;
import lombok.Data;

/**
 * @program: z-notify-api
 * @description: IpBlacklistPO
 * @author: zxlee
 * @create: 2026-07-18
 **/

@Data
public class IpBlacklistPO extends BasePOJO {
    private String ip;
}
