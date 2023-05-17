package com.github.shy526.devenvarrange.oo;


import com.github.shy526.devenvarrange.constant.Constant;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author shy526
 */
@Data
public class ToolRoute {
    private String name;

    private Map<String, String> variable;
    private List<String> operate;

    private String check;

    private Download download;


    @Data
    public static class Download {
        private List<versionMap> versions;
        private List<String> urlRoot;
        private String process;
        private String url;
        private String versionPattern = Constant.DEFAULT_VERSION_PATTERN_STR;
    }

    @Data
    public static class versionMap {
        private String version;
        private String url;

    }
}
