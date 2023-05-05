package com.github.shy526.devenvarrange.rpn.env;

import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.github.shy526.regedit.RegOperate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnvRefresh extends AbsEnv {

    @Override
    public OperateResult execute(List<OperateItem> items) {
        RegOperate regOperate = getRegOperate();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        regOperate.refreshEnvironment();
        return OperateResult.of(items, true);
    }

    @Override
    public String getSymbolStr() {
        return "<";
    }

    @Override
    public int getOperandNum() {
        return 0;
    }
}
