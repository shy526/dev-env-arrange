package com.github.shy526.devenvarrange.constant;

import java.util.regex.Pattern;

/**
 * 常量
 * @author shy526
 */
public class Constant {
    public static final String DEFAULT_VERSION_PATTERN_STR="(\\d+\\.){2}\\d+";
    public static final Pattern DEFAULT_VERSION_PATTERN=Pattern.compile(DEFAULT_VERSION_PATTERN_STR);
}
