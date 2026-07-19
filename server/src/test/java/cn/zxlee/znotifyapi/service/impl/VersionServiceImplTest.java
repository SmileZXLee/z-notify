package cn.zxlee.znotifyapi.service.impl;

import cn.zxlee.znotifyapi.mapper.ProjectMapper;
import cn.zxlee.znotifyapi.mapper.VersionMapper;
import cn.zxlee.znotifyapi.pojo.po.ProjectPO;
import cn.zxlee.znotifyapi.pojo.po.VersionPO;
import cn.zxlee.znotifyapi.pojo.vo.VersionVO;
import cn.zxlee.znotifyapi.utils.TokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class VersionServiceImplTest {

    @InjectMocks
    private VersionServiceImpl versionService;

    @Mock
    private VersionMapper versionMapper;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private TokenUtils tokenUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testList_shouldKeepVersionWildcard() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", "test-token");
        map.put("projectId", "proj-123");

        // Mock project verification
        when(tokenUtils.getUserIdByToken("test-token")).thenReturn("user-123");
        when(projectMapper.listByUserIdAndId("user-123", "proj-123")).thenReturn(new ProjectPO());

        // Prepare test data
        List<VersionPO> poList = new ArrayList<>();
        VersionPO po = new VersionPO();
        po.setVersion("1.2.3");
        po.setDownloadUrl("https://example.com/download/{version}/app.exe");
        po.setConfig("app-config-{version}");
        po.setPlatformSettings("{\"android\":{\"downloadUrl\":\"https://example.com/android/{version}.apk\",\"config\":\"android-config-{version}\"}}");
        poList.add(po);

        when(versionMapper.list(map)).thenReturn(poList);

        List<VersionVO> voList = versionService.list(map);

        assertNotNull(voList);
        assertEquals(1, voList.size());
        VersionVO vo = voList.get(0);
        assertEquals("1.2.3", vo.getVersion());

        // Verify the wildcard {version} remains intact in admin list
        assertEquals("https://example.com/download/{version}/app.exe", vo.getDownloadUrl());
        assertEquals("app-config-{version}", vo.getConfig());
        
        // Verify platformSettings still has {version} intact
        assertNotNull(vo.getPlatformSettings());
        assertTrue(vo.getPlatformSettings() instanceof Map);
        Map<String, Object> settings = (Map<String, Object>) vo.getPlatformSettings();
        Map<String, Object> androidSettings = (Map<String, Object>) settings.get("android");
        assertEquals("https://example.com/android/{version}.apk", androidSettings.get("downloadUrl"));
        assertEquals("android-config-{version}", androidSettings.get("config"));
    }

    @Test
    void testPublicListByVersion_shouldReplaceVersionWildcard() {
        // Prepare test data
        List<VersionPO> poList = new ArrayList<>();
        VersionPO po = new VersionPO();
        po.setVersion("1.2.3");
        po.setDownloadUrl("https://example.com/download/{version}/app.exe");
        po.setConfig("app-config-{version}");
        po.setPlatformSettings("{\"android\":{\"downloadUrl\":\"https://example.com/android/{version}.apk\",\"config\":\"android-config-{version}\"}}");
        poList.add(po);

        when(versionMapper.listByHigherVersion("proj-123", "1.0.0")).thenReturn(poList);

        List<VersionVO> voList = versionService.publicListByVersion("proj-123", "1.0.0");

        assertNotNull(voList);
        assertEquals(1, voList.size());
        VersionVO vo = voList.get(0);
        assertEquals("1.2.3", vo.getVersion());

        // Verify the wildcard {version} is replaced by the actual version "1.2.3" in public list
        assertEquals("https://example.com/download/1.2.3/app.exe", vo.getDownloadUrl());
        assertEquals("app-config-1.2.3", vo.getConfig());
    }
}
