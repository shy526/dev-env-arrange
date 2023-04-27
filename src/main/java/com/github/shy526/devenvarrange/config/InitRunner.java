package com.github.shy526.devenvarrange.config;

import com.github.shy526.devenvarrange.impl.CoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(-1)
public class InitRunner implements ApplicationRunner {
    @Autowired
    private CoreService coreService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        coreService.getToolRoutes();
    }
}
