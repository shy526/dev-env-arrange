package com.github.shy526.devenvarrange.impl;

import com.github.shy526.devenvarrange.oo.ToolRoute;
import com.github.shy526.devenvarrange.oo.ToolVersion;

import java.util.List;

/**
 * @author shy526
 */
public interface CoreService {

    /**
     * 获取可下载的配置文件
     * @return List<ToolRoute>
     */
    List<ToolRoute> getToolRoutes();

    /**
     * 获得某个工具的版本信息
     * @param name name
     * @param number number
     * @return  List<ToolVersion>
     */
    List<ToolVersion> getVersions(String name,Integer number);

    /**
     * 安装莫个工具的
     * @param name name
     * @param version version
     * @param path path  安装路径
     * @return boolean
     */
    boolean insert(String name,String version,String path);

    /**
     * 切换工具版本
     * @param name name
     * @param version version
     * @param path path
     * @return boolean
     */
    boolean switchTool(String name,String version,String path);
}
