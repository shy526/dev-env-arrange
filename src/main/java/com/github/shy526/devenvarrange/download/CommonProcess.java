package com.github.shy526.devenvarrange.download;

import com.github.shy526.devenvarrange.help.PlaceholderHelper;
import com.github.shy526.devenvarrange.config.Config;
import com.github.shy526.devenvarrange.help.IoHelp;
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
            if (versionPattern == null) {
                versionPattern = "(\\d+\\.){2}\\d+";
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

            toolVersions.add(new ToolVersion(version, to.getDate(), to.getDateStr(), getDownloadUrl(toolRoute, version)));
        }
        toolVersions.sort((ToolVersion t, ToolVersion t1) -> t.getDate().compareTo(t1.getDate()) * -1);
        return toolVersions;
    }

    @Override
    public Path downloadFile(ToolRoute toolRoute, String version, String path) {
        String downloadUrl = getDownloadUrl(toolRoute, version);
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
        Path filePath = Paths.get(path).resolve(fileName);
        File file = filePath.toFile();
        boolean temp = file.exists() && file.delete();
        try (
                HttpResult httpResult = httpClientService.get(downloadUrl);
                CloseableHttpResponse response = httpResult.getResponse();
                InputStream in = response.getEntity().getContent();
                BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(filePath))
        ) {
            IoHelp.copy(in, out, true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return filePath;
    }


    private String getDownloadUrl(ToolRoute toolRoute, String version) {
        ToolRoute.Download download = toolRoute.getDownload();
        String osName = config.getOsName();
        String osArch = config.getOsArch();
        Properties properties = new Properties();
        properties.put("urlRoot", download.getUrlRoot().get(0));
        properties.put("version", version);
        String os = osName.toLowerCase().startsWith("win") ? "win" : "linux";
        String win = String.format("os-%s-format", os);
        String osFormat = download.getOsFormat().get(win);
        properties.put("osFormat", osFormat);
        properties.put("os", os);
        properties.put("arch", osArch.matches("64") ? "x64" : "x86");
        return PlaceholderHelper.to(download.getUrl(), properties);
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
