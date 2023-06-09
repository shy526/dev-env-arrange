package com.github.shy526.devenvarrange.rpn.env;

import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.github.shy526.regedit.RegOperate;
import com.github.shy526.regedit.obj.RegValue;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author shy526
 */
@Component
public class EnvAppend extends AbsEnv {
    @Override
    public OperateResult execute(List<OperateItem> items) {
        String val = getStrVal(items,0);
        String key = getStrVal(items,1);
        RegOperate regOperate = getRegOperate();
        RegValue regValue = regOperate.getRegValue(key);
        boolean flag = regValue != null;
        if (flag) {
            String sourceVal = regValue.getValue();
            String[] sourceValArray = sourceVal.split(";");
            if (!Arrays.asList(sourceValArray).contains(val)) {
                char c = sourceVal.charAt(sourceVal.length()-1);
                if (";".equals(c+"")) {
                    sourceVal += val;
                } else {
                    sourceVal += ";" + val;
                }
                System.out.println(key+":"+regValue.getValue()+"->"+sourceVal);
                regValue.setValue(sourceVal);
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
