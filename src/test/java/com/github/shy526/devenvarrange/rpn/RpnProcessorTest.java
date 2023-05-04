package com.github.shy526.devenvarrange.rpn;

import com.github.shy526.devenvarrange.DevEnvArrangeApplication;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest( classes = DevEnvArrangeApplication.class ,properties = {"spring.shell.interactive.enabled=false", "spring.shell.command.script.enabled=false"})
class RpnProcessorTest {
    @Autowired
    private RpnProcessor rpnProcessor;

    @Test
    void execute() {
    }

    @Test
    void parse() {
        List<OperateItem> parse = rpnProcessor.parse("1111");
    }
}