package com.github.shy526.devenvarrange.config;


import com.github.shy526.devenvarrange.oo.ToolRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RunContent {
    @Autowired
    private ApplicationContext applicationContext;
    private static final Map<String, ToolRoute> TOOL_ROUTE_MAP = new ConcurrentHashMap<>();

    public ToolRoute getToolRoute(String name) {
        return TOOL_ROUTE_MAP.get(name);
    }

    public void putToolRoute(ToolRoute toolRoute) {
        TOOL_ROUTE_MAP.put(toolRoute.getName(), toolRoute);
    }

    public <T> T getBean(Class<T> tClass, String name) {
        return applicationContext.getBean(tClass, name);
    }

    public <T>  Map<String, T> getBean(Class<T> tClass) {
        return applicationContext.getBeansOfType(tClass);

    }
}
