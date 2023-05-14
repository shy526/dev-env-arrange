package com.github.shy526.devenvarrange.help;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * jarFile处理
 * @author shy526
 */
public class JarFileHelp {

    public static List<String> getSubPackages(JarFile jarFile, String packName) {
        List<String> packages = new ArrayList<>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.isDirectory()) {
                String name = jarEntry.getName();
                if (name.contains(packName)) {
                    name = name.substring(name.lastIndexOf(packName), name.length() - 1);
                    if (name.equals(packages)) {
                        continue;
                    }
                    packages.add(name);
                }
            }
        }
        return packages;
    }

    public static List<String> getSubClass(JarFile jarFile, String packName) {
        List<String> result = new ArrayList<>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (!jarEntry.isDirectory()) {
                String name = jarEntry.getName();
                if (name.contains(packName)) {
                    name = name.substring(name.lastIndexOf(packName));
                    result.add(name);
                }
            }
        }
        return result;
    }

}
