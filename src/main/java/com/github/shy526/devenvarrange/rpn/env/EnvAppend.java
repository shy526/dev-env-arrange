package com.github.shy526.devenvarrange.rpn.env;

import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.github.shy526.regedit.RegOperate;
import com.github.shy526.regedit.obj.RegValue;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class EnvAppend extends AbsEnv {
    @Override
    public OperateResult execute(List<OperateItem> items) {
        OperateItem keyParma = items.get(0);
        OperateItem valParma = items.get(1);
        String key = keyParma.getVal(String.class);
        String val = valParma.getVal(String.class);
        RegOperate regOperate = getRegOperate();
        RegValue regValue = regOperate.getRegValue(key);
        boolean flag = regValue != null;
        if (flag) {
            String sourceVal = regValue.getValue();
            String[] sourceValArray = sourceVal.split(";");
            if (!Arrays.asList(sourceValArray).contains(val)) {
                regValue.setValue(sourceVal + ";" + val);
                flag = regOperate.setRegValue(regValue);
            }

        }
        return OperateResult.of(items, flag);
    }

    @Override
    public String getSymbolStr() {
        return "+=";
    }
}
