package com.github.shy526.devenvarrange.config;

import com.alibaba.fastjson.JSONObject;
import com.github.shy526.devenvarrange.help.IoHelp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

@Component
public class LocalFileCache {
    @Autowired
    private Config config;

    private Path getCachePath() {
        String cachePathStr = config.getRouteCache();
        Path path = null;
        if (StringUtils.isEmpty(cachePathStr)) {
            path = Paths.get(config.getEnv()).resolve("dev-env-arrange-cache");
        } else {
            path = Paths.get(cachePathStr);
        }
        File file = path.toFile();
        boolean temp = file.exists() && file.mkdirs();
        return path;
    }

    public void set(String key, String val) {
        try {
            Path cachePath = getCachePath();
            Path resolve = cachePath.resolve(key);
            IoHelp.copy(new ByteArrayInputStream(val.getBytes()), Files.newOutputStream(resolve), true, null);
        } catch (Exception ignored) {
        }
    }


    public String get(String key, long time) {
        Path cachePath = getCachePath();
        Path resolve = cachePath.resolve(key);
        File file = resolve.toFile();
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        try {
            BasicFileAttributes attrs = Files.readAttributes(resolve, BasicFileAttributes.class);
            long creatTime = attrs.creationTime().toMillis();
            long updateTime = attrs.lastModifiedTime().toMillis();
            long max = Math.max(creatTime, updateTime);
            long now = System.currentTimeMillis();
            if ((now - max) > time) {
                return null;
            }
        } catch (IOException ignored) {
        }
        return IoHelp.readStr(resolve);
    }


    public <T> T get(String key, long time, Class<T> tClass) {
        String str = get(key, time);
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return JSONObject.parseObject(str, tClass);
    }
}
