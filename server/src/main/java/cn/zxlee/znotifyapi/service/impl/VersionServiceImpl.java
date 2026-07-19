package cn.zxlee.znotifyapi.service.impl;

import cn.zxlee.znotifyapi.exception.CommonException;
import cn.zxlee.znotifyapi.mapper.VersionMapper;
import cn.zxlee.znotifyapi.pojo.bo.VersionBO;
import cn.zxlee.znotifyapi.pojo.bo.VersionPageBO;
import cn.zxlee.znotifyapi.pojo.po.VersionPO;
import cn.zxlee.znotifyapi.pojo.vo.VersionVO;
import cn.zxlee.znotifyapi.pojo.vo.base.PageResultVO;
import cn.zxlee.znotifyapi.service.IVersionService;
import cn.zxlee.znotifyapi.service.base.impl.BaseInProjectServiceImpl;
import cn.zxlee.znotifyapi.utils.BeanConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @program: z-notify-api
 * @description:
 * @author: zxlee
 * @create: 2022-12-11 16:29
 **/

@Service
public class VersionServiceImpl extends BaseInProjectServiceImpl implements IVersionService<VersionVO, VersionBO, VersionPageBO> {
    @Autowired
    private VersionMapper versionMapper;

    private void resolveVoDetails(VersionPO po, VersionVO vo) {
        resolveVoDetails(po, vo, false);
    }

    private void resolveVoDetails(VersionPO po, VersionVO vo, boolean replaceVersion) {
        if (po == null || vo == null) {
            return;
        }
        if (org.springframework.util.StringUtils.hasText(po.getDownloadUrl())) {
            try {
                vo.setDownloadUrl(com.alibaba.druid.support.json.JSONUtils.parse(po.getDownloadUrl()));
            } catch (Exception e) {
                vo.setDownloadUrl(po.getDownloadUrl());
            }
        }
        if (org.springframework.util.StringUtils.hasText(po.getPlatformSettings())) {
            try {
                vo.setPlatformSettings(com.alibaba.druid.support.json.JSONUtils.parse(po.getPlatformSettings()));
            } catch (Exception e) {
                vo.setPlatformSettings(po.getPlatformSettings());
            }
        }
        if (replaceVersion) {
            if (org.springframework.util.StringUtils.hasText(vo.getConfig())) {
                vo.setConfig(vo.getConfig().replace("{version}", vo.getVersion()));
            }
            vo.setDownloadUrl(resolveDownloadUrl(vo.getDownloadUrl(), vo.getVersion()));
            vo.setPlatformSettings(resolvePlatformSettings(vo.getPlatformSettings(), vo.getVersion()));
        }
    }

    private Object resolveDownloadUrl(Object downloadUrl, String version) {
        if (downloadUrl == null) {
            return null;
        }
        if (downloadUrl instanceof String) {
            return ((String) downloadUrl).replace("{version}", version);
        }
        if (downloadUrl instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) downloadUrl;
            Map<String, Object> resolvedMap = new java.util.HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof String) {
                    resolvedMap.put(entry.getKey(), ((String) entry.getValue()).replace("{version}", version));
                } else {
                    resolvedMap.put(entry.getKey(), entry.getValue());
                }
            }
            return resolvedMap;
        }
        return downloadUrl;
    }

    private Object resolvePlatformSettings(Object platformSettings, String version) {
        if (platformSettings == null || !(platformSettings instanceof Map)) {
            return platformSettings;
        }
        Map<String, Object> settingsMap = (Map<String, Object>) platformSettings;
        for (Map.Entry<String, Object> entry : settingsMap.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> platformOverride = (Map<String, Object>) entry.getValue();
                if (platformOverride.containsKey("downloadUrl") && platformOverride.get("downloadUrl") instanceof String) {
                    String url = (String) platformOverride.get("downloadUrl");
                    platformOverride.put("downloadUrl", url.replace("{version}", version));
                }
                if (platformOverride.containsKey("download_url") && platformOverride.get("download_url") instanceof String) {
                    String url = (String) platformOverride.get("download_url");
                    platformOverride.put("download_url", url.replace("{version}", version));
                }
                if (platformOverride.containsKey("config") && platformOverride.get("config") instanceof String) {
                    String configStr = (String) platformOverride.get("config");
                    platformOverride.put("config", configStr.replace("{version}", version));
                }
            }
        }
        return settingsMap;
    }

    @Override
    public List<VersionVO> list(Map map) {
        checkIsCurrentProject(map.get("token").toString(), map.get("projectId").toString());
        return BeanConvertUtils.convertListTo(versionMapper.list(map), VersionVO::new, (po, vo) -> {
            resolveVoDetails(po, vo, false);
        });
    }

    @Override
    public PageResultVO<VersionVO> listByPage(Map map, VersionPageBO pageBO) {
        PageResultVO<VersionVO> pageResultVO = new PageResultVO<>();
        com.github.pagehelper.PageInfo<VersionPO> pageInfo = com.github.pagehelper.PageHelper.startPage(pageBO.getCurrent(), pageBO.getPageSize())
                .doSelectPageInfo(() -> versionMapper.list(map));
        pageResultVO.setCurrent(pageInfo.getPageNum());
        pageResultVO.setPageSize(pageInfo.getPageSize());
        pageResultVO.setTotal(pageInfo.getTotal());
        pageResultVO.setResults(BeanConvertUtils.convertListTo(pageInfo.getList(), VersionVO::new, (po, vo) -> {
            resolveVoDetails(po, vo, false);
        }));
        com.github.pagehelper.PageHelper.clearPage();
        return pageResultVO;
    }

    @Override
    public int saveOne(String token, VersionBO bo) {
        checkIsCurrentProject(token, bo.getProjectId());
        List<VersionPO> oldPOList = versionMapper.listByVersion(bo.getProjectId(), bo.getVersion());
        if (null != oldPOList && oldPOList.size() > 0) {
            throw new CommonException("此版本号已存在");
        }
        VersionPO po = BeanConvertUtils.convertTo(bo, VersionPO::new);
        if (bo.getDownloadUrl() != null) {
            po.setDownloadUrl(com.alibaba.druid.support.json.JSONUtils.toJSONString(bo.getDownloadUrl()));
        }
        if (bo.getPlatformSettings() != null) {
            po.setPlatformSettings(com.alibaba.druid.support.json.JSONUtils.toJSONString(bo.getPlatformSettings()));
        }
        return versionMapper.insertOne(po);
    }

    @Override
    public int updateOne(Map map, String id, VersionBO bo) {
        super.<VersionPO>checkInProjectForUpdate(versionMapper, map.get("token").toString(), id);
        VersionPO po = BeanConvertUtils.convertTo(bo, VersionPO::new);
        if (bo.getDownloadUrl() != null) {
            po.setDownloadUrl(com.alibaba.druid.support.json.JSONUtils.toJSONString(bo.getDownloadUrl()));
        }
        if (bo.getPlatformSettings() != null) {
            po.setPlatformSettings(com.alibaba.druid.support.json.JSONUtils.toJSONString(bo.getPlatformSettings()));
        }
        return versionMapper.updateOne(id, po);
    }

    @Override
    public int deleteById(Map map, String id) {
        super.<VersionPO>checkInProjectForUpdate(versionMapper, map.get("token").toString(), id);
        return versionMapper.deleteById(id);
    }

    @Override
    public List<VersionVO> publicListByVersion(String projectId, String version) {
        return publicListByVersion(projectId, version, null);
    }

    @Override
    public List<VersionVO> publicListByVersion(String projectId, String version, String platform) {
        return publicListByVersion(projectId, version, platform, null);
    }

    @Override
    public List<VersionVO> publicListByVersion(String projectId, String version, String platform, String lang) {
        List<VersionVO> list = BeanConvertUtils.convertListTo(versionMapper.listByHigherVersion(projectId, version), VersionVO::new, (po, vo) -> {
            resolveVoDetails(po, vo, true);
        });

        // 首先解析顶层默认的多语言更新日志
        for (VersionVO vo : list) {
            vo.setContent(resolveMultiLanguageContent(vo.getContent(), lang));
        }

        if (org.springframework.util.StringUtils.hasText(platform)) {
            String targetPlatform = platform.toLowerCase();
            // 1. 过滤掉在此平台被禁用的版本
            list.removeIf(vo -> {
                if (vo.getPlatformSettings() instanceof Map) {
                    Map<String, Object> settings = (Map<String, Object>) vo.getPlatformSettings();
                    for (Map.Entry<String, Object> entry : settings.entrySet()) {
                        if (entry.getKey().toLowerCase().equals(targetPlatform)) {
                            Object platConfig = entry.getValue();
                            if (platConfig instanceof Map) {
                                Map<String, Object> platMap = (Map<String, Object>) platConfig;
                                if (platMap.containsKey("enable")) {
                                    Object enableVal = platMap.get("enable");
                                    if (enableVal instanceof Boolean && !((Boolean) enableVal)) {
                                        return true;
                                    }
                                    if (enableVal instanceof String && "false".equalsIgnoreCase((String) enableVal)) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                return false;
            });

            // 2. 将此平台的覆写参数合并替换至顶层，并清除 platformSettings 字段以保持轻量
            for (VersionVO vo : list) {
                if (vo.getPlatformSettings() instanceof Map) {
                    Map<String, Object> settings = (Map<String, Object>) vo.getPlatformSettings();
                    for (Map.Entry<String, Object> entry : settings.entrySet()) {
                        if (entry.getKey().toLowerCase().equals(targetPlatform)) {
                            Object platConfig = entry.getValue();
                            if (platConfig instanceof Map) {
                                Map<String, Object> platMap = (Map<String, Object>) platConfig;
                                
                                // 覆写更新内容 (需要做多语言解析)
                                if (platMap.containsKey("content") && platMap.get("content") != null && org.springframework.util.StringUtils.hasText(platMap.get("content").toString())) {
                                    vo.setContent(resolveMultiLanguageContent(platMap.get("content").toString(), lang));
                                }
                                
                                // 覆写配置内容
                                if (platMap.containsKey("config") && platMap.get("config") != null && org.springframework.util.StringUtils.hasText(platMap.get("config").toString())) {
                                    vo.setConfig(platMap.get("config").toString());
                                }
                                
                                // 覆写下载链接
                                Object dlUrl = null;
                                if (platMap.containsKey("download_url") && platMap.get("download_url") != null && org.springframework.util.StringUtils.hasText(platMap.get("download_url").toString())) {
                                    dlUrl = platMap.get("download_url");
                                } else if (platMap.containsKey("downloadUrl") && platMap.get("downloadUrl") != null && org.springframework.util.StringUtils.hasText(platMap.get("downloadUrl").toString())) {
                                    dlUrl = platMap.get("downloadUrl");
                                }
                                if (dlUrl != null) {
                                    vo.setDownloadUrl(dlUrl);
                                }
                            }
                        }
                    }
                }
                // 清理其他平台的冗余参数，仅保留最终合并结果
                vo.setPlatformSettings(null);
            }
        }
        return list;
    }

    /**
     * 根据客户端语言解析多语言更新日志内容
     */
    private String resolveMultiLanguageContent(String contentJson, String lang) {
        if (!org.springframework.util.StringUtils.hasText(contentJson)) {
            return "";
        }
        String trimmed = contentJson.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            try {
                Object parsed = com.alibaba.druid.support.json.JSONUtils.parse(contentJson);
                if (parsed instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) parsed;
                    String targetLang = org.springframework.util.StringUtils.hasText(lang) ? lang : "zh-Hans";
                    // 兼容 zh-CN 传参为中文简体
                    if ("zh-CN".equalsIgnoreCase(targetLang)) {
                        targetLang = "zh-Hans";
                    }
                    if (map.containsKey(targetLang) && map.get(targetLang) != null && org.springframework.util.StringUtils.hasText(map.get(targetLang).toString())) {
                        return map.get(targetLang).toString();
                    }
                    // 默认降级为中文简体 zh-Hans
                    if (map.containsKey("zh-Hans") && map.get("zh-Hans") != null) {
                        return map.get("zh-Hans").toString();
                    }
                    // 降级为任意非空语言
                    for (Object val : map.values()) {
                        if (val != null && org.springframework.util.StringUtils.hasText(val.toString())) {
                            return val.toString();
                        }
                    }
                }
            } catch (Exception e) {
                // 解析 JSON 失败说明不是 JSON 字符串，直接作为老版本纯文本处理
            }
        }
        return contentJson;
    }

}
