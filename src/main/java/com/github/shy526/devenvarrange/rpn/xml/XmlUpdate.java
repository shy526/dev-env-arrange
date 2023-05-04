package com.github.shy526.devenvarrange.rpn.xml;


import com.github.shy526.devenvarrange.help.XmlHelp;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.google.common.collect.Lists;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class XmlUpdate implements XmlSymbol {
    @Override
    public OperateResult execute(List<OperateItem> items) {
        OperateItem source = items.get(0);
        OperateItem xPathO = items.get(1);
        OperateItem docSource = items.get(2);
        Document doc = docSource.getVal(Document.class);
        String xPath = xPathO.getVal(String.class);
        if (!XmlHelp.ifAbsXpath(xPath)) {
            return null;
        }
        Node node = doc.selectSingleNode(xPath);
        if (node == null) {
            String tagName = XmlHelp.getAbsXPathTagName(xPath);
            String xPathParen = XmlHelp.getAbsXPathParen(xPath);
            node=XmlHelp.createTag(doc, xPathParen,tagName);
        }
        boolean temp = node != null;
        if (temp) {
            node.setText(source.getVal(String.class));
        }

        return OperateResult.of(temp ? Lists.newArrayList(docSource) : null,items, temp);
    }

    @Override
    public String getSymbolStr() {
        return "+=";
    }

    @Override
    public int getOperandNum() {
        return 3;
    }
}
