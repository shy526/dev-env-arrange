package com.github.shy526.devenvarrange.help;

import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Properties;

/**
 * 占位符处理工具
 * @author shy526
 */
public class PlaceholderHelper {
    private static final PropertyPlaceholderHelper pph = new PropertyPlaceholderHelper("${", "}");

    public static String to(String str,Properties properties) {
        return pph.replacePlaceholders(str, properties);
    }


}
