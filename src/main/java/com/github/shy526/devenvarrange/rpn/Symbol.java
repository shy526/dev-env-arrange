package com.github.shy526.devenvarrange.rpn;

import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;

import java.util.List;

public interface Symbol{

    int OPERAND_NUM = 2;

    OperateResult execute(List<OperateItem> items);

    String getSymbolStr();

    default int getOperandNum() {
        return OPERAND_NUM;
    }

    default String getStrVal(List<OperateItem> operateItems, int index) {
        return operateItems.get(index).getVal(String.class);
    }

}
