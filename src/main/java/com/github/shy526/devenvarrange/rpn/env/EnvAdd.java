package com.github.shy526.devenvarrange.rpn.env;

import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.github.shy526.regedit.RegOperate;
import com.github.shy526.regedit.obj.RegTypeEnum;
import com.github.shy526.regedit.obj.RegValue;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnvAdd extends AbsEnv {
    @Override
    public OperateResult execute(List<OperateItem> items) {
        String val = getStrVal(items,0);
        String key = getStrVal(items,1);
        RegOperate regOperate = getRegOperate();
        RegValue regValue = regOperate.getRegValue(key);
        if (regValue == null) {
            regValue = val.contains("%") ? RegTypeEnum.REG_EXPAND_SZ.of(key, val) : RegTypeEnum.REG_SZ.of(key, val);
        }else {
            System.out.println(key+":"+regValue.getValue()+"->"+val);
        }
        regValue.setValue(val);
        boolean flag = regOperate.setRegValue(regValue);
        return OperateResult.of(items, flag);
    }

    @Override
    public String getSymbolStr() {
        return "+";
    }
}
