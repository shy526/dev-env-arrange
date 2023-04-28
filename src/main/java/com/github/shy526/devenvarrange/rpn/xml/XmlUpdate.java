package com.github.shy526.devenvarrange.rpn.xml;


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
        String index = xPath.substring(0, 2);
        if (index.equals("//")) {
            return null;
        }
        Node node = doc.selectSingleNode(xPath);
        if (node == null) {
            int indexOf = xPath.lastIndexOf("/");
            String[] split = xPath.substring(indexOf + 1).split(":");
            String tagName = "";
            tagName = split.length > 1 ? split[1] : split[0];
            String paren = xPath.substring(0, indexOf);
            Node parenNode = doc.selectSingleNode(paren);
            if (parenNode != null) {
                node = ((Element) parenNode).addElement(tagName);
            }
        }
        boolean temp = node != null;
        if (temp) {
            node.setText(source.getVal(String.class));
        }

        return OperateResult.of(temp ? Lists.newArrayList(docSource) : null, Lists.newArrayList(source, xPathO, docSource), temp);
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
