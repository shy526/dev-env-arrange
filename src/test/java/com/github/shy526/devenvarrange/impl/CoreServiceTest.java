package com.github.shy526.devenvarrange.impl;

import com.github.shy526.devenvarrange.oo.ToolRoute;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest(properties = {"spring.shell.interactive.enabled=false", "spring.shell.command.script.enabled=false"})
class CoreServiceTest {

    @Autowired
    CoreService coreService;

    @Test
    void getToolRoutes() {
        List<ToolRoute> toolRoutes = coreService.getToolRoutes();
        System.out.println("toolRoutes = " + toolRoutes);
    }

    @Test
    void getVersions() {
        coreService.getToolRoutes();
        coreService.insert("scala3","3.3.0",null);
    }

    @Test
    void insert() {
    }
}