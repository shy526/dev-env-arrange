package com.github.shy526.devenvarrange;

import com.github.shy526.devenvarrange.oo.ToolRoute;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Properties;

public class MyPropertyPlaceholderHelper {
    private static final PropertyPlaceholderHelper pph = new PropertyPlaceholderHelper("${", "}");

    public static String to(String str,Properties properties) {
        return pph.replacePlaceholders(str, properties);
    }


}
