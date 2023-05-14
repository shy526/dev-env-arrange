package com.github.shy526.devenvarrange.rpn.xml;

import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.google.common.collect.Lists;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author shy526
 */
@Component
public class XmlSave implements XmlSymbol {
    @Override
    public OperateResult execute(List<OperateItem> items) {
        String path = getStrVal(items, 0);
        Document doc = getDocVal(items, 1);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter xmlWriter = null;
        boolean temp = false;
        try {
            xmlWriter = new XMLWriter(Files.newOutputStream(Paths.get(path)), format);
            xmlWriter.write(doc);
            temp = true;
        } catch (Exception ignored) {
        } finally {
            if (xmlWriter != null) {
                try {
                    xmlWriter.flush();
                } catch (Exception ignored) {
                }
                try {
                    xmlWriter.close();
                } catch (IOException ignored) {
                }
            }
        }
        return OperateResult.of(items, temp,doc);
    }

    @Override
    public String getSymbolStr() {
        return "<";
    }
}
