package com.github.shy526.devenvarrange.rpn.file;

import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @author sy526
 */
@Component
public class FileCreateDir implements FileSymbol {
    @Override
    public OperateResult execute(List<OperateItem> items) {
        String pathStr = getStrVal(items, 0);
        File file = new File(pathStr);
        boolean flag = file.exists();
        if (!file.exists()) {

            flag = file.mkdirs();
        }

        return OperateResult.of(items, flag, pathStr);
    }

    @Override
    public String getSymbolStr() {
        return "+";
    }
}
