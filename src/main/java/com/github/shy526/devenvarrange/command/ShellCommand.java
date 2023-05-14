package com.github.shy526.devenvarrange.command;

import com.github.shy526.devenvarrange.config.Config;
import com.github.shy526.devenvarrange.download.DownloadProcess;
import com.github.shy526.devenvarrange.impl.CoreService;
import com.github.shy526.devenvarrange.oo.ToolRoute;
import com.github.shy526.devenvarrange.oo.ToolVersion;
import com.github.shy526.devenvarrange.rpn.RpnProcessor;
import lombok.extern.slf4j.Slf4j;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Shell;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.*;
import org.springframework.util.FileCopyUtils;

import java.io.PrintWriter;
import java.util.*;

@ShellComponent
@Slf4j
public class ShellCommand extends AbstractShellComponent {
    @Autowired
    Config config;
    @Autowired
    private CoreService coreService;

    @ShellMethod(value = "支持一键不是的工具列表", key = {"list", "l"})
    public Table getToolRoutes() {
        List<ToolRoute> toolRoutes = coreService.getToolRoutes();
        LinkedHashMap<String, Object> head = new LinkedHashMap<>();
        head.put("name", "name");
        head.put("download.urlRoot[0]", "downloadRootUrl");
        Terminal terminal = getTerminal();
        PrintWriter writer = terminal.writer();
        return buildTable(toolRoutes, head);
    }

    @ShellMethod(value = "列出工具支持的版本", key = {"versions", "v"})
    public Table getVersions(String name,@ShellOption(defaultValue ="5") Integer number) {
        List<ToolVersion> versions = coreService.getVersions(name,number);
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
        coreService.insert(name,version,config.getEnv());
        return false;
    }
}
