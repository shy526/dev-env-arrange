package com.github.shy526.devenvarrange.impl;


import com.alibaba.fastjson.JSON;
import com.github.shy526.devenvarrange.config.Config;
import com.github.shy526.devenvarrange.config.RunContent;
import com.github.shy526.devenvarrange.download.CommonProcess;
import com.github.shy526.devenvarrange.download.DownloadProcess;
import com.github.shy526.devenvarrange.oo.ToolRoute;
import com.github.shy526.devenvarrange.oo.ToolVersion;
import com.github.shy526.http.HttpClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class CoreServiceImpl implements CoreService {


    @Autowired
    private Config config;
    @Autowired
    private HttpClientService httpClientService;

    @Autowired
    private RunContent runContent;
    @Autowired
    private CommonProcess commonProcess;

    @Override
    public List<ToolRoute> getToolRoutes() {
        List<Path> toolRoutePaths = getToolRoutePaths();
        return buildToolRoutes(toolRoutePaths);
    }

    @Override
    public List<ToolVersion> getVersions(String name) {
        List<ToolVersion> result = new ArrayList<>();
        ToolRoute toolRoute = runContent.getToolRoute(name);
        if (toolRoute==null){
            return result;
        }
        ToolRoute.Download download = toolRoute.getDownload();
        DownloadProcess bean = runContent.getBean(DownloadProcess.class, download.getProcess());
        return bean.getVersion(toolRoute);
    }

    @Override
    public boolean insert(String name, String version, String path) {
        ToolRoute toolRoute = runContent.getToolRoute(name);
        if (toolRoute==null){
            return false;
        }
        ToolRoute.Download download = toolRoute.getDownload();
        DownloadProcess bean = runContent.getBean(DownloadProcess.class, download.getProcess());
        Path zipPath = bean.downloadFile(toolRoute, version, path);
        File zipFile = zipPath.toFile();
        if (!zipFile.exists()){
           return false;
        }

        try(ZipInputStream zis = new ZipInputStream(new BufferedInputStream(Files.newInputStream(zipPath)))) {
            ZipEntry ze =null;
            while ((ze=zis.getNextEntry())!=null){
                String fileName = ze.getName();
                System.out.println("fileName = " + fileName);
                zis.closeEntry();
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return false;
    }


    private List<ToolRoute> buildToolRoutes(List<Path> toolRoutePaths) {
        List<ToolRoute> toolRoutes = new ArrayList<>(toolRoutePaths.size());
        for (Path toolRoutePath : toolRoutePaths) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(toolRoutePath)))) {
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                if (sb.length() > 0) {
                    ToolRoute toolRoute = JSON.parseObject(sb.toString(), ToolRoute.class);
                    runContent.putToolRoute(toolRoute);
                    toolRoutes.add(toolRoute);
                }
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        }
        return toolRoutes;
    }

    private List<Path> getToolRoutePaths() {
        List<Path> toolRoutePath = new ArrayList<>();
        String route = config.getRoute();
        File routeDir = new File(route);
        if (!routeDir.exists()) {
            return toolRoutePath;
        }
        if (routeDir.isFile()) {
            return toolRoutePath;
        }
        File[] files = routeDir.listFiles();
        if (files == null) {
            return toolRoutePath;
        }
        return Arrays.stream(files).map(File::toPath).collect(Collectors.toList());
    }
}
