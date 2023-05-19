package com.github.shy526.devenvarrange.config;


import com.github.shy526.devenvarrange.help.PlaceholderHelper;
import com.github.shy526.devenvarrange.oo.ToolRoute;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

/**
 * @author shy526
 */
@Component
@ConfigurationProperties(prefix = "config")
@Data
public class Config {
    /**
     * route 文件下载地址
     */
    private String route;
    private String env;

    private String routeCache;

    private final String temp = System.getProperty("temp");
    private final String osName = System.getProperty("os.name").toLowerCase();
    private final String userHome = System.getProperty("user.home");
    private final String userName = System.getProperty("user.name");
    private final String osArch = System.getProperty("os.arch");

    public Properties getEnvProperties(ToolRoute toolRoute) {
        return getEnvProperties(toolRoute,null);
    }

    public Properties getEnvProperties(ToolRoute toolRoute, Properties properties) {
        Properties result = new Properties();
        result.setProperty("os-win-format", "zip");
        result.setProperty("os-linux-format", "tar.gz");
        result.setProperty("os-linux", "linux");
        result.setProperty("os-win", "win");
        result.setProperty("os-arch-x64", "x64");
        result.setProperty("os-arch-x86", "x86");
        Map<String, String> variable = toolRoute.getVariable();
        if (variable != null) {
            variable.forEach((key, val) -> {
                if (properties != null) {
                    val = PlaceholderHelper.to(val, properties);
                }
                result.setProperty(key, val);
            });
        }
        if (properties!=null){
            properties.forEach((key, value) -> result.setProperty((String) key, (String) value));
        }
        String os = osName.startsWith("win") ? "win" : "linux";
        String osFormatKey = String.format("os-%s-format", os);
        result.setProperty("os-format", result.getProperty(osFormatKey));
        String osNameKey = String.format("os-%s", os);
        result.setProperty("os-name", result.getProperty(osNameKey));
        String arch = osArch.matches("64") ? "x64" : "x86";
        String archKey = String.format("os-arch-%s", arch);
        result.put("os-arch", result.getProperty(archKey));
        result.put("user-home",userHome);
        return result;
    }
}
