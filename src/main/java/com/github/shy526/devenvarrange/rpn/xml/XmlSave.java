package com.github.shy526.devenvarrange.rpn.xml;

import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.google.common.collect.Lists;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class XmlSave implements XmlSymbol {
    @Override
    public OperateResult execute(List<OperateItem> items) {
        OperateItem path = items.get(0);
        OperateItem doc = items.get(1);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter xmlWriter = null;
        boolean temp = false;
        try {
            xmlWriter = new XMLWriter(Files.newOutputStream(Paths.get(path.getVal(String.class))), format);
            xmlWriter.write(doc.getVal());
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
        return OperateResult.of(Lists.newArrayList(doc), Lists.newArrayList(doc, path), temp);
    }

    @Override
    public String getSymbolStr() {
        return "<";
    }
}
