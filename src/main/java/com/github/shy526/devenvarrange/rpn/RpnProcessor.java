package com.github.shy526.devenvarrange.rpn;

import com.github.shy526.devenvarrange.config.RunContent;
import com.github.shy526.devenvarrange.rpn.files.FileSymbol;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.github.shy526.devenvarrange.rpn.oo.OperateType;
import com.github.shy526.devenvarrange.rpn.xml.XmlSymbol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Slf4j
public class RpnProcessor {
    @Autowired
    private RunContent runContent;

    public OperateResult execute(List<OperateItem> items) {
        Stack<OperateItem> runTimeStack = new Stack<>();
        OperateResult result = null;
        for (OperateItem item : items) {
            if (item.getType().equals(OperateType.VALUE)) {
                runTimeStack.push(item);
                continue;
            }
            Symbol symbol = item.getVal(Symbol.class);
            int operandNum = symbol.getOperandNum();
            Stack<OperateItem> params = new Stack<>();
            for (int i = 0; i < operandNum; i++) {
                params.push(runTimeStack.pop());
            }
            result = symbol.execute(params);
            Boolean success = result.getSuccess();
            log.error(symbol.getSymbolStr()+":"+success);
            if (!success) {
                break;
            }
            List<OperateItem> temp = result.getResult();
            if (temp == null) {
                continue;
            }
            for (OperateItem operateItem : temp) {
                runTimeStack.push(operateItem);
            }
        }
        return result;
    }

    public List<OperateItem> parse(String str) {
        List<OperateItem> result = new ArrayList<>();
        String[] items = str.split("\\s+");
        String sysRpn = items[0];
        int sysRpnLen = sysRpn.length();
        List<Symbol> symbols = null;
        if (sysRpn.equals("xml")) {
            Map<String, XmlSymbol> bean = runContent.getBean(XmlSymbol.class);
            symbols = new ArrayList<>(bean.values());
        } else if (sysRpn.equals("file")) {
            Map<String, FileSymbol> bean = runContent.getBean(FileSymbol.class);
            symbols = new ArrayList<>(bean.values());
        }
        if (symbols == null) {
            return result;
        }
        String prnStr = str.substring(sysRpnLen + 1);
        items = prnStr.split("\\s+");
        for (String item : items) {
            Optional<Symbol> any = symbols.stream().filter(symbol -> item.equals(symbol.getSymbolStr())).findAny();
            if (any.isPresent()) {
                result.add(OperateItem.of(any.get(), OperateType.SYMBOL));
            } else {
                result.add(OperateItem.of(item, OperateType.VALUE));
            }

        }
        return result;
    }
}
