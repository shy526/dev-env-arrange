package com.github.shy526.devenvarrange.rpn.files;

import com.github.shy526.devenvarrange.help.IoHelp;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
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
        Path source = getVal(operateItems, 0);
        Path target = getVal(operateItems, 1);
        File sourceFile = source.toFile();
        if (!sourceFile.exists()) {
            return OperateResult.fail(operateItems);
        }
        boolean temp = IoHelp.copy(source, target);
        File targetFile = target.toFile();
        temp = targetFile.exists();
        if (temp) {
            sourceFile.delete();
        }
        return OperateResult.of(operateItems, temp, target.toString());
    }

    @Override
    public String getSymbolStr() {
        return "->";
    }
}
