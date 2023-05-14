package com.github.shy526.devenvarrange.download;

import com.github.shy526.devenvarrange.config.Config;
import com.github.shy526.devenvarrange.oo.ToolRoute;
import com.github.shy526.devenvarrange.oo.ToolVersion;
import com.github.shy526.http.HttpClientService;
import com.github.shy526.http.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

/**
 * 自定义json 解析
 */
@Component
@Slf4j
public class CustomJsonProcess implements DownloadProcess {
    @Autowired
    private Config config;
    @Autowired
    private HttpClientService httpClientService;

    @Override
    public List<ToolVersion> getVersion(ToolRoute toolRoute,Integer number) {
        ToolRoute.Download download = toolRoute.getDownload();
        List<String> urlRoots = download.getUrlRoot();
        String urlRoot = urlRoots.get(0);
        try (HttpResult httpResult = httpClientService.get(urlRoot)) {
            String entityStr = httpResult.getEntityStr();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public Path downloadFile(ToolRoute toolRoute, String version, String path) {
        return null;
    }

    @Override
    public String getDownloadUrl(ToolRoute toolRoute, String version) {
        return null;
    }
}
