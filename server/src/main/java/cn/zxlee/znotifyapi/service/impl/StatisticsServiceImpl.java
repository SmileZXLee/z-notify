package cn.zxlee.znotifyapi.service.impl;

import cn.zxlee.znotifyapi.mapper.StatisticsMapper;
import cn.zxlee.znotifyapi.pojo.bo.StatisticsBO;
import cn.zxlee.znotifyapi.pojo.bo.StatisticsPageBO;
import cn.zxlee.znotifyapi.pojo.po.StatisticsPO;
import cn.zxlee.znotifyapi.pojo.vo.*;
import cn.zxlee.znotifyapi.pojo.vo.base.PageResultVO;
import cn.zxlee.znotifyapi.service.IStatisticsService;
import cn.zxlee.znotifyapi.exception.CommonException;
import cn.zxlee.znotifyapi.service.base.impl.BaseInProjectServiceImpl;
import cn.zxlee.znotifyapi.utils.BeanConvertUtils;
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @program: z-notify-api
 * @description:
 * @author: zxlee
 * @create: 2022-12-17 17:15
 **/

@Service
public class StatisticsServiceImpl extends BaseInProjectServiceImpl implements IStatisticsService<StatisticsVO, StatisticsBO, StatisticsPageBO> {

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Autowired
    private Ip2regionSearcher ip2regionSearcher;

    @Override
    public List<StatisticsVO> list(Map map) {
        return BeanConvertUtils.convertListTo(statisticsMapper.list(map), StatisticsVO::new);
    }

    @Override
    public PageResultVO<StatisticsVO> listByPage(Map map, StatisticsPageBO pageBO) {
        return baseListByPage(pageBO, map, StatisticsVO::new, () -> statisticsMapper.list(map));
    }

    @Override
    public int saveOne(String token, StatisticsBO bo) {
        return statisticsMapper.insertOne(BeanConvertUtils.convertTo(bo, StatisticsPO::new));
    }

    @Override
    public int updateOne(Map map, String id, StatisticsBO bo) {
        return 0;
    }

    @Override
    public int deleteById(Map map, String id) {
        return 0;
    }

    @Override
    @Async
    public void publicSaveOne(StatisticsBO bo) {
        checkHasProject(bo.getProjectId());
        bo.setIpRegion(ip2regionSearcher.getAddress(bo.getIp()));
        saveOne("", bo);
    }

    @Override
    public StatisticsResultVO publicGetStatisticsResult(String projectId, String visitorBy) {
        visitorBy = null == visitorBy ? "ip" : visitorBy;
        int visitorCount = statisticsMapper.countGroupBy(projectId, visitorBy);
        int statisticsListCount = statisticsMapper.listCount(projectId);
        StatisticsResultVO statisticsResultVO = new StatisticsResultVO();
        statisticsResultVO.setViewCount(statisticsListCount);
        statisticsResultVO.setVisitorCount(visitorCount);
        return statisticsResultVO;
    }

    @Override
    public StatisticsAnalysisResultVO getStatisticsAnalysisResult(String token, String projectId, String visitorBy) {
        visitorBy = null == visitorBy ? "ip" : visitorBy;
        checkIsCurrentProject(token, projectId);

        final String visitorByFinal = visitorBy;

        // 1. 并行执行汇总查询（包含9个指标的单次聚合查询）
        CompletableFuture<StatisticsAnalysisResultVO> summaryFuture = CompletableFuture.supplyAsync(() ->
                statisticsMapper.getAnalysisSummary(projectId, visitorByFinal)
        );

        // 2. 并行执行 ipRegionCountList
        CompletableFuture<List<StatisticsRegionCountVO>> ipRegionFuture = CompletableFuture.supplyAsync(() ->
                statisticsMapper.ipRegionCountList(projectId)
        );

        // 3. 并行执行 hour24CountList
        CompletableFuture<List<StatisticsTimeCountVO>> hour24Future = CompletableFuture.supplyAsync(() ->
                statisticsMapper.hour24CountList(projectId)
        );

        // 4. 并行执行 days10CountList
        CompletableFuture<List<StatisticsDateCountVO>> days10Future = CompletableFuture.supplyAsync(() ->
                statisticsMapper.days10CountList(projectId)
        );

        // 5. 并行执行 months12CountList
        CompletableFuture<List<StatisticsDateCountVO>> months12Future = CompletableFuture.supplyAsync(() ->
                statisticsMapper.months12CountList(projectId)
        );

        // 等待所有查询并行执行完毕
        CompletableFuture.allOf(summaryFuture, ipRegionFuture, hour24Future, days10Future, months12Future).join();

        try {
            StatisticsAnalysisResultVO resultVO = summaryFuture.get();
            if (resultVO == null) {
                resultVO = new StatisticsAnalysisResultVO();
            }
            resultVO.setIpRegionCountList(ipRegionFuture.get());
            resultVO.setHour24CountList(hour24Future.get());
            resultVO.setDays10CountList(days10Future.get());
            resultVO.setMonths12CountList(months12Future.get());
            return resultVO;
        } catch (Exception e) {
            throw new CommonException("获取统计分析数据失败: " + e.getMessage());
        }
    }
}
