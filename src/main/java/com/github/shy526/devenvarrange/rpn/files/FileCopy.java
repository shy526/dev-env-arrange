package com.github.shy526.devenvarrange.rpn.files;

import com.github.shy526.devenvarrange.help.IoHelp;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;


@Component
public class FileCopy implements FileSymbol {
    @Override
    public OperateResult execute(List<OperateItem> operateItems) {
        OperateItem source = operateItems.get(0);
        OperateItem target = operateItems.get(1);
        return OperateResult.of(Lists.newArrayList(target), operateItems, IoHelp.copy(source.getVal(Path.class), target.getVal(Path.class)));
    }

    @Override
    public String getSymbolStr() {
        return ">>";
    }
}
