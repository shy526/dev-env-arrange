package com.github.shy526.devenvarrange.rpn;

import com.github.shy526.devenvarrange.config.RunContent;
import com.github.shy526.devenvarrange.help.ClassHelp;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.github.shy526.devenvarrange.rpn.oo.OperateType;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.*;


@Component
@Slf4j
public class RpnProcessor {

    private static final Map<String,Class<? extends Symbol>> SYMBOL_MAP;

    /**
     * 初始化符号
     */
    static {
        SYMBOL_MAP=new HashMap<>();
        Package aPackage =RpnProcessor.class.getPackage();
        String packagePath = ClassHelp.package2packagePath(aPackage.getName());
        List<URL> resources = ClassHelp.getResources(packagePath);
        List<File> packages = new ArrayList<>();
        for (URL resource : resources) {
            File file = new File(resource.getPath());
            File[] files = file.listFiles(File::isDirectory);
            if (files == null) {
                continue;
            }
            packages.addAll(Lists.newArrayList(files));
        }

        List<URL> rootDir = ClassHelp.getRootDir();
        for (File file : packages) {
            String key = file.getName();
            File[] files = file.listFiles(File::isFile);
            if (files == null) {
                continue;
            }
            for (File item : files) {
                Class<?> aClass = ClassHelp.uriPath2Class(item.toURI().getPath(), rootDir);
                if (Symbol.class.isAssignableFrom(aClass)) {
                    SYMBOL_MAP.put(key, (Class<? extends Symbol>) aClass);
                    break;
                }
            }
        }
    }

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
        Class<? extends Symbol> aClass = SYMBOL_MAP.get(sysRpn);
        if (aClass != null) {
            symbols = getSymbols(aClass);
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


    private List<Symbol> getSymbols(Class<? extends Symbol> symbolClass) {
        List<Symbol> symbols;
        Map<String, ? extends Symbol> bean = runContent.getBean(symbolClass);
        symbols = new ArrayList<>(bean.values());
        return symbols;
    }
}
