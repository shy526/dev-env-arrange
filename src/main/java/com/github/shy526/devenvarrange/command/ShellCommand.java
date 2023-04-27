package com.github.shy526.devenvarrange.command;

import com.github.shy526.devenvarrange.download.DownloadProcess;
import com.github.shy526.devenvarrange.impl.CoreService;
import com.github.shy526.devenvarrange.oo.ToolRoute;
import com.github.shy526.devenvarrange.oo.ToolVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.*;
import org.springframework.util.FileCopyUtils;

import java.util.*;

@ShellComponent
@Slf4j
public class ShellCommand {

    @Autowired
    private CoreService coreService;

    @ShellMethod(value = "支持一键不是的工具列表", key = {"list", "l"})
    public Table getToolRoutes() {
        List<ToolRoute> toolRoutes = coreService.getToolRoutes();
        LinkedHashMap<String, Object> head = new LinkedHashMap<>();
        head.put("name", "name");
        head.put("download.urlRoot[0]", "downloadRootUrl");

        return buildTable(toolRoutes, head);
    }

    @ShellMethod(value = "列出工具支持的版本", key = {"versions", "v"})
    public Table getVersions(String name) {
        List<ToolVersion> versions = coreService.getVersions(name);
        if (versions.isEmpty()) {
            throw new RuntimeException("不支持或无法获取版本号", null);
        }
        LinkedHashMap<String, Object> head = new LinkedHashMap<>();
        head.put("version", "version");
        head.put("dateStr", "date");
        head.put("url", "url");
        return buildTable(versions, head);
    }

    private Table buildTable(List<?> data, LinkedHashMap<String, Object> head) {
        TableModel model = new BeanListTableModel<>(data, head);
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.fancy_double);
        return tableBuilder.build();
    }

    @ShellMethod(value = "配置工具", key = {"install", "i"})
    public boolean installTool(String name ,String version){
        coreService.insert(name,version,"E:\\java-project\\dev-env-arrange\\src\\test\\resources");

        return false;
    }
}
