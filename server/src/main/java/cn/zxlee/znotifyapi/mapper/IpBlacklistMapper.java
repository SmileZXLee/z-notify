package cn.zxlee.znotifyapi.mapper;

import cn.zxlee.znotifyapi.mapper.base.BaseMapper;
import cn.zxlee.znotifyapi.pojo.po.IpBlacklistPO;
import org.springframework.stereotype.Repository;

/**
 * @program: z-notify-api
 * @description: IpBlacklistMapper
 * @author: zxlee
 * @create: 2026-07-18
 **/

@Repository
public interface IpBlacklistMapper extends BaseMapper<IpBlacklistPO> {
    IpBlacklistPO getByIp(String ip);
}
