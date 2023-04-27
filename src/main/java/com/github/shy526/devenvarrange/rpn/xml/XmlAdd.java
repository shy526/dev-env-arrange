package com.github.shy526.devenvarrange.rpn.xml;

import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class XmlAdd implements XmlSymbol {
    @Override
    public OperateResult execute(List<OperateItem> items) {
        OperateItem docSource = items.get(0);
        OperateItem path = items.get(1);
        OperateItem val = items.get(2);
        return null;
    }

    @Override
    public String getSymbolStr() {
        return "+";
    }

    @Override
    public int getOperandNum() {
        return 3;
    }
}
