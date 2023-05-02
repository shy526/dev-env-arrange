package com.github.shy526.devenvarrange.rpn.files;

import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Component
public class FileDelete implements FileSymbol {
    @Override
    public OperateResult execute(List<OperateItem> operateItems) {
        OperateItem source = operateItems.get(0);
        File file = source.getVal(Path.class).toFile();
        boolean temp = true;
        if (file.exists()) {
            temp = file.delete();
        }
        return OperateResult.of(null, Lists.newArrayList(source), temp);
    }

    @Override
    public String getSymbolStr() {
        return "!>";
    }
}
