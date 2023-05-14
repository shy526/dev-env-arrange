package com.github.shy526.devenvarrange.download;

import com.github.shy526.devenvarrange.config.Config;
import com.github.shy526.devenvarrange.constant.Constant;
import com.github.shy526.devenvarrange.help.IoHelp;
import com.github.shy526.devenvarrange.help.PlaceholderHelper;
import com.github.shy526.devenvarrange.oo.ToolRoute;
import com.github.shy526.devenvarrange.oo.ToolVersion;
import com.github.shy526.http.HttpClientService;
import com.github.shy526.http.HttpResult;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用下载处理器
 */
@Component
@Slf4j
public class CommonProcess extends AbsDownloadProcess {

    @Autowired
    private Config config;

    private static final int VERSION_LENGTH = 4;

    private static final int VERSION_INDEX = 0;
    private static final int VERSION_DATE_INDEX = 1;

    @Autowired
    public CommonProcess(HttpClientService httpClientService) {
        super(httpClientService);
    }

    /**
     * @param url
     * @return
     */
    @Override
    public List<ToolVersion> getVersion(ToolRoute toolRoute, Integer number) {
        List<ToolVersion> toolVersions = new ArrayList<>();
        ToolRoute.Download download = toolRoute.getDownload();
        List<String> rows = html2rows(download.getUrlRoot().get(0), "\n");
        Properties envProperties = config.getEnvProperties(toolRoute);
        envProperties.setProperty("urlRoot", download.getUrlRoot().get(0));
        for (String row : rows) {
            String[] col = row.trim().split("\\s+");
            if (col.length != VERSION_LENGTH) {
                continue;
            }
            String version = col[VERSION_INDEX].trim().replace("/", "");
            String dateStr = col[VERSION_DATE_INDEX].trim();
            Pattern compile = Pattern.compile(download.getVersionPattern());
            Matcher matcher = compile.matcher(version);
            if (!matcher.matches()) {
                continue;
            }
            DateConverter.ConverterResult to = DateConverter.to(dateStr);
            if (to == null) {
                continue;
            }
            envProperties.setProperty("version", version);
            toolVersions.add(new ToolVersion(version, to.getDate(), to.getDateStr(), PlaceholderHelper.to(download.getUrl(), envProperties)));
        }
        return versionSort(toolVersions, number);
    }


    @Override
    public String getDownloadUrl(ToolRoute toolRoute, String version) {
        Properties envProperties = config.getEnvProperties(toolRoute);
        ToolRoute.Download download = toolRoute.getDownload();
        envProperties.setProperty("urlRoot", download.getUrlRoot().get(0));
        envProperties.setProperty("version", version);
        return PlaceholderHelper.to(download.getUrl(), envProperties);
    }


    private List<String> html2rows(String url, String separate) {
        List<String> result = new ArrayList<>();
        try (HttpResult httpResult = httpClientService.get(url)) {
            Document parse = Jsoup.parse(httpResult.getEntityStr());
            Elements pre = parse.body().select("pre");
            String text = pre.text();
            String[] rows = text.split(separate);
            return Lists.newArrayList(rows);
        } catch (Exception e) {
            log.error(e.getMessage() + ":" + url, e);

        }
        return result;
    }
}
