package com.github.shy526.devenvarrange.download;

import com.alibaba.fastjson.JSON;
import com.github.shy526.date.DateFormat;
import com.github.shy526.devenvarrange.MyPropertyPlaceholderHelper;
import com.github.shy526.devenvarrange.config.Config;
import com.github.shy526.devenvarrange.oo.ToolRoute;
import com.github.shy526.devenvarrange.oo.ToolVersion;
import com.github.shy526.http.HttpClientService;
import com.github.shy526.http.HttpResult;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.SystemPropertyUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用下载处理器
 */
@Component
@Slf4j
public class CommonProcess implements DownloadProcess {

    @Autowired
    private Config config;
    @Autowired
    private HttpClientService httpClientService;
    private static final int VERSION_LENGTH = 4;

    private static final int VERSION_INDEX = 0;
    private static final int VERSION_DATE_INDEX = 1;

    /**
     * @param url
     * @return
     */
    @Override
    public List<ToolVersion> getVersion(ToolRoute toolRoute) {
        List<ToolVersion> toolVersions = new ArrayList<>();
        ToolRoute.Download download = toolRoute.getDownload();
        List<String> rows = html2rows(download.getUrlRoot().get(0), "\n");
        for (String row : rows) {
            String[] col = row.trim().split("\\s+");
            if (col.length != VERSION_LENGTH) {
                continue;
            }
            String version = col[VERSION_INDEX].trim().replace("/", "");
            String dateStr = col[VERSION_DATE_INDEX].trim();
            String versionPattern = download.getVersionPattern();
            if (versionPattern==null){
                versionPattern="(\\d+\\.){2}\\d+";
            }
            Pattern compile = Pattern.compile(versionPattern);
            Matcher matcher = compile.matcher(version);
            if (!matcher.matches()) {
                continue;
            }
            DateConverter.ConverterResult to = DateConverter.to(dateStr);
            if (to == null) {
                continue;
            }

            toolVersions.add(new ToolVersion(version, to.getDate(), to.getDateStr(),getDownloadUrl(toolRoute, version)));
        }
        toolVersions.sort((ToolVersion t, ToolVersion t1) -> t.getDate().compareTo(t1.getDate()) * -1);
        return toolVersions;
    }

    private String getDownloadUrl(ToolRoute toolRoute, String version) {
        ToolRoute.Download download = toolRoute.getDownload();
        String osName = config.getOsName();
        String osArch = config.getOsArch();
        Properties properties = new Properties();
        properties.put("urlRoot", download.getUrlRoot().get(0));
        properties.put("version", version);
        String os = osName.toLowerCase().startsWith("win") ? "win" : "linux";
        String win = String.format("os-%s-format",os );
        String osFormat = download.getOsFormat().get(win);
        properties.put("osFormat", osFormat);
        properties.put("os", os);
        properties.put("arch", osArch.matches("64")?"x64":"x86");
        return MyPropertyPlaceholderHelper.to(download.getUrl(), properties);
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
            log.error(e.getMessage()+":"+url, e);

        }
        return result;
    }
}
