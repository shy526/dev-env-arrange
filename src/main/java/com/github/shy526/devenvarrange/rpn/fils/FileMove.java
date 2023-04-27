package com.github.shy526.devenvarrange.rpn.fils;

import com.github.shy526.devenvarrange.help.IoHelp;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * 文件移动
 */
@Component
public class FileMove implements FileSymbol {
    @Override
    public OperateResult execute(List<OperateItem> operateItems) {
        OperateItem source = operateItems.get(0);
        OperateItem target = operateItems.get(1);
        boolean temp = IoHelp.copy(source.getVal(Path.class), source.getVal(Path.class));
        File file = source.getVal(Path.class).toFile();
        if (temp && file.exists()) {
            temp = file.delete();
        }
        return OperateResult.of(Lists.newArrayList(target), Lists.newArrayList(source, target), temp);
    }

    @Override
    public String getSymbolStr() {
        return "->";
    }
}
