package com.github.shy526.devenvarrange.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@ConfigurationProperties(prefix = "config")
@Data
public class Config {
    private String route;
    private String env;
    private final String osName=System.getProperty("os.name");
    private final String userHome=System.getProperty("user.home");
    private final String userName=System.getProperty("user.name");
    private final String osArch=System.getProperty("os.arch");

    public Properties getEnvProperties(){
        Properties properties = new Properties();
        properties.put("osName",osName);
        properties.put("userHome",userHome);
        properties.put("userName",osArch);
        properties.put("osArch",osArch);
        return properties;
    }
}
