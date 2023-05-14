package com.github.shy526.devenvarrange.impl;

import com.github.shy526.devenvarrange.oo.ToolRoute;
import com.github.shy526.devenvarrange.oo.ToolVersion;

import java.util.List;

public interface CoreService {
    List<ToolRoute> getToolRoutes();

    List<ToolVersion> getVersions(String name,Integer number);

    boolean insert(String name,String version,String path);
}
