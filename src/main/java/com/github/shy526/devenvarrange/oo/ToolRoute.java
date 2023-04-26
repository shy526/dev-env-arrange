package com.github.shy526.devenvarrange.oo;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ToolRoute {
    private String name;

    private Map<String,String> variable;
    private List<String> operate;

    private String check;

    private Download download;


    @Data
    public static class Download {
        private List<String> url;
        private String process;
    }
}
