package com.github.shy526.devenvarrange.help;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

/**
 * @author shy526
 */
public class ClassHelp {
    private static final String PATH_SEPARATE = "/";
    private static final String NAME_SEPARATE = ".";
    private static final String CLASS_EXTEND = ".class";

    public static String package2packagePath(String packageName) {
        return packageName.replace(NAME_SEPARATE, PATH_SEPARATE);
    }

    public static Class<?> uriPath2Class(String path, List<URL> rootDir) {
        Class<?> result = null;
        for (URL url : rootDir) {
            path = path.replace(url.getPath(), StringUtils.EMPTY);
        }
        String className = path.replace(PATH_SEPARATE, NAME_SEPARATE).replace(CLASS_EXTEND, StringUtils.EMPTY);
        try {
            result = Class.forName(className);
        } catch (Exception ignored) {
        }
        return result;

    }

    public static List<URL> getRootDir() {
        List<URL> urls = new ArrayList<>();
        try {
            Enumeration<URL> resources = ClassHelp.class.getClassLoader().getResources(NAME_SEPARATE);
            while (resources.hasMoreElements()) {
                urls.add(resources.nextElement());
            }
        } catch (IOException ignored) {
        }
        return urls;
    }

    public static List<URL> getResources(String packagePath) {
        ClassLoader loader = ClassHelp.class.getClassLoader();
        ArrayList<URL> result = new ArrayList<>();
        try {
            Enumeration<URL> resources = loader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                result.add(resources.nextElement());
            }
        } catch (Exception ignored) {
        }
        return result;
    }
}
