package com.github.shy526.devenvarrange.rpn.xml;


import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.github.shy526.devenvarrange.rpn.oo.OperateType;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
import org.springframework.stereotype.Component;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.Files.newInputStream;

@Component
@Slf4j
public class XmlOpen implements XmlSymbol {
    @Override
    public OperateResult execute(List<OperateItem> items) {
        OperateItem source = items.get(0);
        SAXReader reader = new SAXReader();

        OperateItem item = null;
        try {
            Document doc = (Document) reader.read(source.getVal(String.class));
            Map<String, String> namespaceMap = new HashMap<>();
            Element rootElement = doc.getRootElement();
            namespaceMap.put("d", rootElement.getNamespaceURI());
            List<Namespace> namespaces = rootElement.additionalNamespaces();
            for (Namespace namespace : namespaces) {
                namespaceMap.put(namespace.getPrefix(), namespace.getURI());
            }
            reader.getDocumentFactory().setXPathNamespaceURIs(namespaceMap);
            item = OperateItem.of(doc, OperateType.VALUE);
        } catch (Exception ignored) {
        }
        boolean temp = item != null;
        return OperateResult.of(temp ? Lists.newArrayList(item) : null, Lists.newArrayList(source), temp);
    }

    @Override
    public String getSymbolStr() {
        return ">";
    }

    @Override
    public int getOperandNum() {
        return 1;
    }

}
