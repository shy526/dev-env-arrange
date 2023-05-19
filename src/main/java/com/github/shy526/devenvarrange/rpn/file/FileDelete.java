package com.github.shy526.devenvarrange.rpn.file;

import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Component
public class FileDelete implements FileSymbol {
    @Override
    public OperateResult execute(List<OperateItem> operateItems) {
        Path source = getVal(operateItems, 0);
        File file = source.toFile();
        boolean temp = true;
        if (file.exists()) {
            temp = file.delete();
        }
        return OperateResult.of(operateItems, temp);
    }

    @Override
    public String getSymbolStr() {
        return "-";
    }
}
