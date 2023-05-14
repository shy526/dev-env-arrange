package com.github.shy526.devenvarrange.impl;


import com.alibaba.fastjson.JSON;
import com.github.shy526.devenvarrange.command.ShellCommand;
import com.github.shy526.devenvarrange.config.Config;
import com.github.shy526.devenvarrange.config.RunContent;
import com.github.shy526.devenvarrange.download.CommonProcess;
import com.github.shy526.devenvarrange.download.DownloadProcess;
import com.github.shy526.devenvarrange.help.IoHelp;
import com.github.shy526.devenvarrange.help.PlaceholderHelper;
import com.github.shy526.devenvarrange.oo.ToolRoute;
import com.github.shy526.devenvarrange.oo.ToolVersion;
import com.github.shy526.devenvarrange.rpn.RpnProcessor;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.github.shy526.http.HttpClientService;
import com.github.shy526.regedit.shell.ShellClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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


    @Autowired
    private RpnProcessor rpnProcessor;

    @Override
    public List<ToolRoute> getToolRoutes() {
        List<Path> toolRoutePaths = getToolRoutePaths();
        return buildToolRoutes(toolRoutePaths);
    }

    @Override
    public List<ToolVersion> getVersions(String name,Integer number) {
        List<ToolVersion> result = new ArrayList<>();
        ToolRoute toolRoute = runContent.getToolRoute(name);
        if (toolRoute == null) {
            return result;
        }
        ToolRoute.Download download = toolRoute.getDownload();
        DownloadProcess bean = runContent.getBean(DownloadProcess.class, download.getProcess());
        return bean.getVersion(toolRoute,number);
    }

    @Override
    public boolean insert(String name, String version, String path) {
        ToolRoute toolRoute = runContent.getToolRoute(name);
        if (toolRoute == null) {
            return false;
        }
        ToolRoute.Download download = toolRoute.getDownload();
        StringBuilder sb = new StringBuilder();
        String check = "cmd /c " + toolRoute.getCheck();
        String msg="已安装->未知版本";
        int exec = ShellClient.exec(check, result -> {
            String versionPattern = download.getVersionPattern();
            Pattern compile = Pattern.compile(versionPattern);
            Matcher matcher = compile.matcher(result);
            if (matcher.find()) {
                sb.append("已安装->"+matcher.group());
            }else {
                sb.append(msg);
            }
        });
        if (exec == ShellClient.CODE_SUCCESS) {
            System.out.println(sb.length()==0?msg:sb);
            return false;
        }
        DownloadProcess bean = runContent.getBean(DownloadProcess.class, download.getProcess());
        Path zipPath = bean.downloadFile(toolRoute, version, path);
        System.out.println("unZip:" + zipPath);
        Path toolRoot = IoHelp.unZip(zipPath, Paths.get(path));
        if (toolRoot == null) {
            return false;
        }
        System.out.println("root:" + toolRoot);
        Properties properties = new Properties();
        properties.put("root", toolRoot.toString());
        properties=config.getEnvProperties(toolRoute,properties);
        List<String> operate = toolRoute.getOperate();
        if (operate != null) {
            for (String str : operate) {
                String rpn = PlaceholderHelper.to(str, properties);
                System.out.println("rpn = " + rpn);
                List<OperateItem> parse = rpnProcessor.parse(rpn);
                OperateResult execute = rpnProcessor.execute(parse);
                System.out.println("execute = " + execute);
                System.out.println(rpn + "    :    " + execute.getSuccess());
            }
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
                log.error(e.getMessage(), e);
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
