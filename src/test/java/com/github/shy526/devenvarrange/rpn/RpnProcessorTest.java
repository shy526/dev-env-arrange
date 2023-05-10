package com.github.shy526.devenvarrange.rpn;

import com.github.shy526.devenvarrange.DevEnvArrangeApplication;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
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
        //MAVEN_HOME D:\javaEx\apache-maven-3.5.3
        //path D:\javaEx\apache-maven-3.5.3\bin
        String rpn1="env MAVEN_HOME D:\\javaEx\\apache-maven-3.5.3 + MAVEN_BIN %MAVEN_HOME%\\bin + < MAVEN_BIN2 %maven_bin%\\xx + <";
        String rpn="env MAVEN_HOME D:\\javaEx\\apache-maven-3.5.3 + Path %MAVEN_HOME%\\bin += <";
        List<OperateItem> parse = rpnProcessor.parse(rpn1);
        OperateResult execute = rpnProcessor.execute(parse);
        System.out.println("execute = " + execute.getSuccess());

    }
}