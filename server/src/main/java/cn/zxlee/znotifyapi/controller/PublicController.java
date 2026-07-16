package cn.zxlee.znotifyapi.controller;

import cn.zxlee.znotifyapi.annotation.NoLoginAuth;
import cn.zxlee.znotifyapi.annotation.enumValidator.EnumValidator;
import cn.zxlee.znotifyapi.enums.StatisticsBadgeType;
import cn.zxlee.znotifyapi.enums.StatisticsVisitorBy;
import cn.zxlee.znotifyapi.pojo.bo.FeedbackBO;
import cn.zxlee.znotifyapi.pojo.bo.StatisticsBO;
import cn.zxlee.znotifyapi.pojo.bo.StatisticsBadgeBO;
import cn.zxlee.znotifyapi.pojo.vo.*;
import cn.zxlee.znotifyapi.response.Result;
import cn.zxlee.znotifyapi.service.*;
import cn.zxlee.znotifyapi.utils.IpUtils;
import cn.zxlee.znotifyapi.utils.oss.IOssService;
import cn.zxlee.znotifyapi.utils.thirdPartyApi.IThirdPartyApiService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;

/**
 * @program: z-notify-api
 * @description: 公用接口控制器
 * @author: zxlee
 * @create: 2022-11-25 17:41
 **/

@Slf4j
@RestController
@RequestMapping("/v1/public")
@Api(value = "公共接口", tags = {"公共接口"})
@Validated
public class PublicController {

    @Autowired
    private INoticeService noticeService;

    @Autowired
    private ITextService textService;

    @Autowired
    private IVersionService versionService;

    @Autowired
    private IFeedbackService feedbackService;

    @Autowired
    private IStatisticsService statisticsService;

    @Autowired
    private IOssService ossService;

    @Autowired
    private IThirdPartyApiService thirdPartyApiService;

    @GetMapping("/notices/{project_id}")
    @ApiOperation("获取通知列表")
    @NoLoginAuth
    public Result<List<NoticeVO>> getNotices(@NotEmpty @PathVariable("project_id") String projectId){
        return Result.success(noticeService.publicList(projectId));
    }

    @GetMapping("/text/{project_id}/{key}")
    @ApiOperation("根据key获取文本")
    @NoLoginAuth
    public Result<TextVO> getTextByKey(@NotEmpty @PathVariable("project_id") String projectId, @NotEmpty @PathVariable("key") String key){
        List<TextVO> textVOS = textService.publicListByKey(projectId, key);
        return Result.success(textVOS.size() > 0 ?  textVOS.get(0) : null);
    }

    @GetMapping("/versions/{project_id}/{version}")
    @ApiOperation("根据版本号获取版本，传当前版本号，会返回高于此版本的所有版本，如果为空则代表没有新版本")
    @NoLoginAuth
    public Result<List<VersionVO>> getVersions(
            @NotEmpty @PathVariable("project_id") String projectId,
            @Pattern(regexp = "^\\d+(\\.\\d+)*$", message = "版本号格式不合法") @PathVariable("version") String version,
            @RequestParam(value = "platform", required = false) String platform,
            @RequestParam(value = "lang", required = false) String lang) {
        return Result.success(versionService.publicListByVersion(projectId, version, platform, lang));
    }

    @GetMapping("/versions/{project_id}/{version}/tauri")
    @ApiOperation("支持 Tauri 自动更新器的专用接口")
    @NoLoginAuth
    public ResponseEntity<Map<String, Object>> getTauriVersions(
            @NotEmpty @PathVariable("project_id") String projectId,
            @Pattern(regexp = "^\\d+(\\.\\d+)*$", message = "版本号格式不合法") @PathVariable("version") String version,
            @RequestParam(value = "platform", required = false) String platform,
            @RequestParam(value = "lang", required = false) String lang) {

        List<VersionVO> versions = versionService.publicListByVersion(projectId, version, platform, lang);
        if (versions == null || versions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        VersionVO latest = versions.get(0);

        String downloadUrl = latest.getDownloadUrl() != null ? latest.getDownloadUrl().toString() : "";
        String signature = latest.getConfig() != null ? latest.getConfig() : "";
        String notes = latest.getContent() != null ? latest.getContent() : "";

        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("version", latest.getVersion());

        String pubDate = "";
        if (latest.getCreatetime() != null) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .withZone(java.time.ZoneId.of("UTC"));
            pubDate = formatter.format(latest.getCreatetime().toInstant());
        } else {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .withZone(java.time.ZoneId.of("UTC"));
            pubDate = formatter.format(new java.util.Date().toInstant());
        }
        response.put("pub_date", pubDate);
        response.put("url", downloadUrl);
        response.put("signature", signature);
        response.put("notes", notes);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/versions/{project_id}/{version}/electron")
    @ApiOperation("支持 Electron built-in autoUpdater (Squirrel) 规格的专用接口")
    @NoLoginAuth
    public ResponseEntity<Map<String, Object>> getElectronVersions(
            @NotEmpty @PathVariable("project_id") String projectId,
            @Pattern(regexp = "^\\d+(\\.\\d+)*$", message = "版本号格式不合法") @PathVariable("version") String version,
            @RequestParam(value = "platform", required = false) String platform,
            @RequestParam(value = "lang", required = false) String lang) {
        List<VersionVO> versions = versionService.publicListByVersion(projectId, version, platform, lang);
        if (versions == null || versions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        VersionVO latest = versions.get(0);

        String downloadUrl = "";
        if (latest.getPlatformSettings() instanceof Map) {
            Map<String, Object> settingsMap = (Map<String, Object>) latest.getPlatformSettings();
            if (org.springframework.util.StringUtils.hasText(platform)) {
                String targetPlatform = platform.toLowerCase();
                for (Map.Entry<String, Object> entry : settingsMap.entrySet()) {
                    if (entry.getKey().toLowerCase().equals(targetPlatform)) {
                        Object platformOverride = entry.getValue();
                        if (platformOverride instanceof Map) {
                            Map<String, Object> overrideDetail = (Map<String, Object>) platformOverride;
                            if (overrideDetail.containsKey("download_url") && overrideDetail.get("download_url") != null) {
                                downloadUrl = overrideDetail.get("download_url").toString();
                            } else if (overrideDetail.containsKey("downloadUrl") && overrideDetail.get("downloadUrl") != null) {
                                downloadUrl = overrideDetail.get("downloadUrl").toString();
                            }
                        }
                    }
                }
            }
        }

        if (!org.springframework.util.StringUtils.hasText(downloadUrl) && latest.getDownloadUrl() != null) {
            downloadUrl = latest.getDownloadUrl().toString();
        }

        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("url", downloadUrl);
        response.put("name", latest.getVersion());
        response.put("notes", latest.getContent());

        String pubDate = "";
        if (latest.getCreatetime() != null) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .withZone(java.time.ZoneId.of("UTC"));
            pubDate = formatter.format(latest.getCreatetime().toInstant());
        } else {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .withZone(java.time.ZoneId.of("UTC"));
            pubDate = formatter.format(new java.util.Date().toInstant());
        }
        response.put("pub_date", pubDate);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/feedback/feedback")
    @ApiOperation("添加反馈数据")
    @NoLoginAuth
    public Result saveFeedback(@Validated @RequestBody FeedbackBO bo){
        int result = feedbackService.publicSaveOne(bo);
        return result > 0 ? Result.success() : Result.fail("添加失败");
    }

    @GetMapping("/feedbacks/{project_id}/{username}")
    @ApiOperation("查询某个用户下的反馈列表")
    @NoLoginAuth
    public Result<List<FeedbackVO>> getFeedbacks(@NotEmpty @PathVariable("project_id") String projectId, @PathVariable("username") String username){
        return Result.success(feedbackService.publicListByUsername(projectId, username));
    }

    @PostMapping(value = "/upload/uploadFiles", headers = "content-type=multipart/form-data")
    @ApiOperation("文件上传，支持多个文件同时上传，每个文件上限为1MB")
    @NoLoginAuth
    public Result<List<String>> uploadFiles(@RequestPart("files") MultipartFile[] files) {
        return Result.success(ossService.uploadFiles(files));
    }

    @GetMapping("/statistics/{project_id}/addOnly")
    @ApiOperation("访问一次项目(不返回任何信息，可以放在img标签中并设置display:none来达到无感记录的效果)")
    @NoLoginAuth
    public String visitGetStatistics(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @NotEmpty @PathVariable("project_id") String projectId,
                                     @ApiParam("用于额外区分不同个体的标签") @RequestParam(value = "tag", required = false) String tag,
                                     @ApiParam("访问者来源") @RequestParam(value = "from", required = false) String from){
        response.setHeader("Cache-Control", "no-cache,max-age=0,no-store,s-maxage=0,proxy-revalidate");
        response.setHeader("Expires", "0");
        String ip = IpUtils.getIpAddr(request);
        StatisticsBO bo = new StatisticsBO();
        bo.setProjectId(projectId);
        bo.setIp(ip);
        bo.setTag(tag);
        bo.setFrom(from);
        statisticsService.publicSaveOne(bo);
        return null;
    }

    @GetMapping("/statistics/{project_id}")
    @ApiOperation("访问一次项目并获取项目统计信息")
    @NoLoginAuth
    public Result<StatisticsResultVO> visitAndGetStatistics(HttpServletRequest request,
                                                            @NotEmpty @PathVariable("project_id") String projectId,
                                                            @ApiParam("用于额外区分不同个体的标签") @RequestParam(value = "tag", required = false) String tag,
                                                            @ApiParam("访问者来源") @RequestParam(value = "from", required = false) String from,
                                                            @ApiParam("visitor_count计算根据什么区分，默认为ip，可选值有ip、tag") @RequestParam(value = "visitor_by", required = false) @EnumValidator(StatisticsVisitorBy.class) String visitorBy) {
        if (Objects.equals(projectId, "8316326835763216384")) {
            return null;
        }
        String ip = IpUtils.getIpAddr(request);
        StatisticsBO bo = new StatisticsBO();

        bo.setProjectId(projectId);
        bo.setIp(ip);
        bo.setTag(tag);
        bo.setFrom(from);
        statisticsService.publicSaveOne(bo);
        return Result.success(statisticsService.publicGetStatisticsResult(projectId, visitorBy));
    }

    @GetMapping(value = "/statistics/{project_id}/badge", produces="image/svg+xml;charset=utf-8")
    @ApiOperation("访问一次项目并获取项目统计信息以badge形式展示(依赖于shields.io)")
    @NoLoginAuth
    public String visitAndGetStatisticsOnBadge(HttpServletRequest request, HttpServletResponse response, @NotEmpty @PathVariable("project_id") String projectId, @Validated StatisticsBadgeBO badgeBO) {
        response.setHeader("Cache-Control", "no-cache,max-age=0,no-store,s-maxage=0,proxy-revalidate");
        response.setHeader("Expires", "0");
        if (Objects.equals(projectId, "8316326835763216384")) {
            return null;
        }
        String ip = IpUtils.getIpAddr(request);
        StatisticsBO bo = new StatisticsBO();
        bo.setProjectId(projectId);
        bo.setIp(ip);
        bo.setTag(badgeBO.getTag());
        bo.setFrom(badgeBO.getFrom());
        statisticsService.publicSaveOne(bo);

        StatisticsResultVO statisticsResultVO = statisticsService.publicGetStatisticsResult(projectId, badgeBO.getVisitorBy());

        String badgeType = badgeBO.getType();
        Integer count = StatisticsBadgeType.VIEW_COUNT.getValue().equals(badgeType) ? statisticsResultVO.getViewCount() : statisticsResultVO.getVisitorCount();

        return thirdPartyApiService.getBadge(badgeBO.getTitle(), count.toString(), badgeBO.getColor(), badgeBO.getStyle());
    }
}
