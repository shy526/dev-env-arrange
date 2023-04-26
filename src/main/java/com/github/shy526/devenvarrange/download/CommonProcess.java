package com.github.shy526.devenvarrange.download;

import com.github.shy526.date.DateFormat;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 通用下载处理器
 */
@Component
@Slf4j
public class CommonProcess implements DownloadProcess {
    @Autowired
    private HttpClientService httpClientService;
    private static final int VERSION_LENGTH = 6;

    private static final int VERSION_INDEX = 0;
    private static final int VERSION_DATE_INDEX = 4;

    @Override
    public List<ToolVersion> getVersion(String url) {
        List<ToolVersion> toolVersions = new ArrayList<>();
        try (HttpResult httpResult = httpClientService.get(url)) {
            String entityStr = httpResult.getEntityStr();
            if (entityStr == null) {
                return toolVersions;
            }
            Document parse = Jsoup.parse(entityStr);
            Elements pre = parse.body().select("pre");
            String text = pre.text();
            String[] rows = text.split("\n");
            for (String row : rows) {
                String[] col = row.split("\\s{4}");
                if (col.length == VERSION_LENGTH) {
                    String version = col[VERSION_INDEX].trim().replace("/", "");
                    String dateStr = col[VERSION_DATE_INDEX].trim();
                    Date date = DateFormat.parse(dateStr, "yyyy-MM-dd HH:mm");
                    toolVersions.add(new ToolVersion(version, date, dateStr));
                }
            }
            toolVersions.sort((ToolVersion t, ToolVersion t1) -> t.getDate().compareTo(t1.getDate())*-1);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return toolVersions;
    }
}
