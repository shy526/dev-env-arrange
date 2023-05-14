package com.github.shy526.devenvarrange.download;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.shy526.date.DateFormat;
import com.github.shy526.devenvarrange.config.Config;
import com.github.shy526.devenvarrange.help.PlaceholderHelper;
import com.github.shy526.devenvarrange.oo.ToolRoute;
import com.github.shy526.devenvarrange.oo.ToolVersion;
import com.github.shy526.http.HttpClientService;
import com.github.shy526.http.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
/**
 * @author shy526
 */
@Component
@Slf4j
public class GithubProcess extends AbsDownloadProcess {


    @Autowired
    private Config config;
    private static final String RELEASES_LIST_URL = "https://api.github.com/repos/%s/%s/releases?per_page=100&page=%s";
    private static final String download_url = "https://github.com/${owner}/${repo}/releases/download";
    private static final int startPage = 1;

    @Autowired
    public GithubProcess(HttpClientService httpClientService) {
        super(httpClientService);
    }

    @Override
    public List<ToolVersion> getVersion(ToolRoute toolRoute, Integer number) {
        int page = startPage;
        List<ToolVersion> toolVersions = new ArrayList<>();
        Properties envProperties = config.getEnvProperties(toolRoute);
        String urlRoot = PlaceholderHelper.to(download_url, envProperties);
        envProperties.setProperty("urlRoot", urlRoot);
        for (; toolVersions.size() < number; page++) {
            JSONArray releases = getReleases(toolRoute, page);
            if (releases.isEmpty()) {
                break;
            }
            for (int i = 0; i < releases.size(); i++) {
                JSONObject release = releases.getJSONObject(i);
                String version = release.getString("tag_name");
                Date date = release.getDate("created_at");
                envProperties.setProperty("version", version);
                String url = toolRoute.getDownload().getUrl();
                String downloadUrl = PlaceholderHelper.to(url, envProperties);
                toolVersions.add(new ToolVersion(version, date, DateFormat.dateFormat(date), downloadUrl));
                if (toolVersions.size() >= number) {
                    break;
                }
            }
        }
        return versionSort(toolVersions, number);
    }

    @Override
    public String getDownloadUrl(ToolRoute toolRoute, String version) {
        Properties envProperties = config.getEnvProperties(toolRoute);
        String urlRoot = PlaceholderHelper.to(download_url, envProperties);
        envProperties.setProperty("urlRoot", urlRoot);
        envProperties.setProperty("version", version);
        String url = toolRoute.getDownload().getUrl();
        return PlaceholderHelper.to(url, envProperties);
    }

    private JSONArray getReleases(ToolRoute toolRoute, Integer page) {
        Map<String, String> variable =
                toolRoute.getVariable();
        String url = String.format(RELEASES_LIST_URL, variable.get("owner"), variable.get("repo"), page);
        JSONArray result = new JSONArray();

        try (HttpResult httpResult = httpClientService.get(url);) {
            result = JSONObject.parseArray(httpResult.getEntityStr());
        } catch (Exception ignored) {

        }
        return result;
    }


}
