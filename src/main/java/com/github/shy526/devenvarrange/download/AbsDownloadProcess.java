package com.github.shy526.devenvarrange.download;

import com.github.shy526.devenvarrange.constant.Constant;
import com.github.shy526.devenvarrange.help.IoHelp;
import com.github.shy526.devenvarrange.oo.ToolRoute;
import com.github.shy526.devenvarrange.oo.ToolVersion;
import com.github.shy526.http.HttpClientService;
import com.github.shy526.http.HttpResult;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author shy526
 */
@Slf4j
public abstract class AbsDownloadProcess implements DownloadProcess {
    protected HttpClientService httpClientService;

    public AbsDownloadProcess(HttpClientService httpClientService) {
        this.httpClientService = httpClientService;
    }

    private static final Comparator<ToolVersion> VERSION_COMPARATOR;

    static {
        VERSION_COMPARATOR = (o1, o2) -> {
            List<String> versions1 = parseVersions(o1);
            List<String> versions2 = parseVersions(o2);
            int temp = 0;
            for (int i = 0; i < versions1.size(); i++) {

                Integer v1 = Integer.parseInt(versions1.get(i));
                Integer v2 = Integer.parseInt(versions2.get(i));
                temp = v1.compareTo(v2);
                if (temp != 0) {
                    break;
                }


            }
            return temp * -1;
        };
    }

    protected static List<String> parseVersions(ToolVersion o1) {
        Matcher matcher1 = Constant.DEFAULT_VERSION_PATTERN.matcher(o1.getVersion());
        if (matcher1.find()) {
            return Lists.newArrayList(matcher1.group().split("\\."));
        }
        return new ArrayList<>();
    }

    protected List<ToolVersion> versionSort(List<ToolVersion> toolVersions, Integer number) {
        toolVersions.sort(VERSION_COMPARATOR);
        if (toolVersions.size() > number) {
            toolVersions = toolVersions.subList(0, number);
        }
        return toolVersions;
    }

    @Override
    public Path downloadFile( ToolRoute toolRoute, String version, String path) {
        String downloadUrl = getDownloadUrl(toolRoute, version);
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
        Path filePath = Paths.get(path).resolve(fileName);
        File file = filePath.toFile();
        if (file.exists()) {
            return filePath;
        }
        try {
            HttpResult httpResult = httpClientService.get(downloadUrl);
            String totalMb = String.format("%.2f", httpResult.getResponse().getEntity().getContentLength() / 1024f);
            CloseableHttpResponse response = httpResult.getResponse();
            InputStream in = response.getEntity().getContent();
            BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(filePath));
            System.out.println(downloadUrl + "  ->  " + filePath.toString());
            String format = totalMb + "kb/" + "%skb";
            IoHelp.copy(in, out, true, speed -> {
                String speedMb = String.format("%.2f", speed / 1024f);
                String str = String.format(format, speedMb);
                System.out.print(str.replaceAll(".{1}", "\b"));
                System.out.print(str);

            });
            System.out.print("\r\n");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return filePath;
    }
}
