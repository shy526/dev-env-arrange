package com.github.shy526.devenvarrange.rpn.files;

import com.github.shy526.devenvarrange.rpn.Symbol;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public interface FileSymbol extends Symbol {

    default Path getVal(List<OperateItem> operateItems, int index) {
        String val = getStrVal(operateItems, index);
        return Paths.get(val);
    }
}
