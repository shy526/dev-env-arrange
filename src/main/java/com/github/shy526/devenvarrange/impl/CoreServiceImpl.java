package com.github.shy526.devenvarrange.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.shy526.devenvarrange.config.Config;
import com.github.shy526.devenvarrange.config.LocalFileCache;
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
import com.github.shy526.gather.GatherUtils;
import com.github.shy526.http.HttpClientService;
import com.github.shy526.http.HttpResult;
import com.github.shy526.regedit.shell.ShellClient;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shy526
 */
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
    private LocalFileCache localFileCache;
    @Autowired
    private RpnProcessor rpnProcessor;

    @Override
    public List<ToolRoute> getToolRoutes() {
        List<ToolRoute> toolRoutes = runContent.getToolRoute();
        if (!GatherUtils.isEmpty(toolRoutes)) {
            return toolRoutes;
        }
        String route = config.getRoute();

        Pattern compile = Pattern.compile("https?://.+");
        Matcher matcher = compile.matcher(route);

        if (matcher.matches()) {
            String key = "list-ToolRoute";
            long time = 1000 * 60 * 5;
            toolRoutes = localFileCache.process(key, time, ToolRoute.class, () -> remoteToolRoute(route));
        } else {
            toolRoutes = localToolRoute(route);
        }
        String format = "https://github.com/%s/%s/releases";
        for (ToolRoute toolRoute : toolRoutes) {
            log.error("tool->" + toolRoute.getName());
            String process = toolRoute.getDownload().getProcess();
            if (process.equals("githubProcess")) {
                Map<String, String> variable = toolRoute.getVariable();
                toolRoute.getDownload().setUrlRoot(Lists.newArrayList(String.format(format, variable.get("owner"), variable.get("repo"))));
            }

            runContent.putToolRoute(toolRoute);
        }
        return toolRoutes;
    }

    @Override
    public List<ToolVersion> getVersions(String name, Integer number) {
        List<ToolVersion> result = new ArrayList<>();
        ToolRoute toolRoute = runContent.getToolRoute(name);
        if (toolRoute == null) {
            return result;
        }
        ToolRoute.Download download = toolRoute.getDownload();
        DownloadProcess bean = runContent.getBean(DownloadProcess.class, download.getProcess());
        String format = "%s_%s_versions";
        String key = String.format(format, name, number);
        long time = 1000 * 60 * 5;
        List<ToolVersion> versions = new ArrayList<>();
        versions = localFileCache.process(key, time, ToolVersion.class, () -> bean.getVersion(toolRoute, number));
        return versions;
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
        String msg = "已安装->未知版本";
        int exec = ShellClient.exec(check, result -> {
            String versionPattern = download.getVersionPattern();
            Pattern compile = Pattern.compile(versionPattern);
            Matcher matcher = compile.matcher(result);
            if (matcher.find()) {
                sb.append("已安装->").append(matcher.group());
            } else {
                sb.append(msg);
            }
        },true);
        if (exec == ShellClient.CODE_SUCCESS) {
            System.out.println(sb.length() == 0 ? msg : sb);
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
        properties = config.getEnvProperties(toolRoute, properties);
        List<String> operate = toolRoute.getOperate();
        boolean result = true;
        if (operate != null) {
            for (String str : operate) {
                String rpn = PlaceholderHelper.to(str, properties);
                log.error(rpn);
                List<OperateItem> parse = rpnProcessor.parse(rpn);
                OperateResult execute = rpnProcessor.execute(parse);
                result = execute.getSuccess();
            }
        }
        return result;
    }

    @Override
    public boolean switchTool(String name, String version, String path) {
        return false;
    }

    /**
     * 获取本地route
     *
     * @param path 路径
     * @return ToolRoute
     */
    private List<ToolRoute> localToolRoute(String path) {
        List<ToolRoute> result = new ArrayList<>();
        File routeDir = new File(path);
        if (!routeDir.exists()) {
            return result;
        }
        if (routeDir.isFile()) {
            return result;
        }
        File[] files = routeDir.listFiles();
        if (files == null) {
            return result;
        }
        for (File file : files) {
            String str = IoHelp.readStr(file.toPath());
            if (StringUtils.isEmpty(str)) {
                continue;
            }
            result.add(JSON.parseObject(str, ToolRoute.class));
        }
        return result;
    }

    /**
     * 获取远程route
     *
     * @param url url
     * @return ToolRoute
     */
    private List<ToolRoute> remoteToolRoute(String url) {
        List<ToolRoute> result = new ArrayList<>();
        String routesStr = httpEntityStr(url);
        List<JSONObject> routes = JSONObject.parseArray(routesStr, JSONObject.class);
        for (JSONObject route : routes) {
            String downloadUrl = route.getString("url");
            String type = route.getString("type");
            String name = route.getString("name");
            if (!"file".equals(type) || !".route".equals(name.substring(name.lastIndexOf(".")))) {
                continue;
            }
            String temp = httpEntityStr(downloadUrl);
            if(StringUtils.isEmpty(temp)){
                continue;
            }
            JSONObject tempObj = JSON.parseObject(temp);
            String content = tempObj.getString("content");
            if(StringUtils.isEmpty(content)){
                continue;
            }
            try {
                temp = new String(Base64.decodeBase64(content), StandardCharsets.UTF_8);
                result.add(JSONObject.parseObject(temp, ToolRoute.class));
            }catch (Exception ig){
                log.error(temp);
            }
        }
        return result;
    }

    private String httpEntityStr(String url) {
        String result = "";
        try (HttpResult httpResult = httpClientService.get(url)) {
            result = httpResult.getEntityStr();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }
}
