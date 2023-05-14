package com.github.shy526.devenvarrange.rpn;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.shy526.devenvarrange.config.RunContent;
import com.github.shy526.devenvarrange.help.ClassHelp;
import com.github.shy526.devenvarrange.help.JarFileHelp;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.github.shy526.devenvarrange.rpn.oo.OperateType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedType;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;


@Component
@Slf4j
public class RpnProcessor {

    private static final Map<String, Class<? extends Symbol>> SYMBOL_MAP;

    /**
     * 初始化符号
     */
    static {
        SYMBOL_MAP = new HashMap<>();
        Package aPackage = RpnProcessor.class.getPackage();
        String packagePath = ClassHelp.package2packagePath(aPackage.getName());
        URL location = RpnProcessor.class.getProtectionDomain().getCodeSource().getLocation();
        String protocol = location.getProtocol();
        if ("jar".equals(protocol)) {
            try {
                //URI uri = URI.create("jar:file:E:/java-project/dev-env-arrange/target/dev-env-arrange-0.0.1-SNAPSHOT.jar!/BOOT-INF/");
                //location = uri.toURL();
                JarFile jarFile = ((JarURLConnection) location.openConnection()).getJarFile();
                List<String> subPackages = JarFileHelp.getSubPackages(jarFile, packagePath);
                for (String subPackage : subPackages) {
                    List<String> subClass = JarFileHelp.getSubClass(jarFile, subPackage);
                    initRpn(subClass, subPackage.substring(subPackage.lastIndexOf("/") + 1));
                }
            } catch (Exception ignored) {
            }

        } else {
            String url = location.getPath() + packagePath;
            File file = new File(url);
            File[] packs = file.listFiles(File::isDirectory);
            if (packs != null) {
                for (File pack : packs) {
                    String key = pack.getName();
                    File[] calzzFile = pack.listFiles(File::isFile);
                    if (calzzFile == null) {
                        continue;
                    }
                    List<String> classPaths = Arrays.stream(calzzFile).map(item ->
                            item.getAbsolutePath().replaceAll("\\\\","/").replace(location.getPath().substring(1), "")
                    ).collect(Collectors.toList());
                    initRpn(classPaths,key);
                }
            }
        }
        SYMBOL_MAP.forEach((k,v)->{
            log.error("逆波兰符号体系加载-->"+k+"-->"+v.getSimpleName());
        });

    }


    public static void initRpn(List<String> classPaths, String key) {
        try {
            for (String classPath : classPaths) {
                String className = classPath.substring(0, classPath.lastIndexOf(".")).replaceAll("/", ".");
                Class<?> aClass = Class.forName(className);
                if (!aClass.isInterface() && Symbol.class.isAssignableFrom(aClass)) {
                    Class<?>[] interfaces = aClass.getInterfaces();
                    SYMBOL_MAP.put(key, (Class<? extends Symbol>) interfaces[0]);
                    return;
                }
            }
        } catch (Exception ignored) {
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
            System.out.println(JSONObject.toJSON(result));
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
