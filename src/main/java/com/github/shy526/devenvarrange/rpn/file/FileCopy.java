package com.github.shy526.devenvarrange.rpn.file;

import com.github.shy526.devenvarrange.help.IoHelp;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * @author sy526
 */
@Component
public class FileCopy implements FileSymbol {
    @Override
    public OperateResult execute(List<OperateItem> operateItems) {
        Path source = getVal(operateItems, 0);
        Path target = getVal(operateItems, 1);
        IoHelp.copy(source, target);
        File file = target.toFile();
        return OperateResult.of(operateItems, file.exists(),target.toString());
    }

    @Override
    public String getSymbolStr() {
        return ">>";
    }
}
