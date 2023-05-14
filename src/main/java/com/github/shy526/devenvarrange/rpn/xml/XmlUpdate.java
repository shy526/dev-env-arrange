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

/**
 * @author shy526
 */
@Component
public class XmlUpdate implements XmlSymbol {
    @Override
    public OperateResult execute(List<OperateItem> items) {

        String source = getStrVal(items, 0);
        String xPath = getStrVal(items, 1);
        Document doc = getDocVal(items, 2);
        if (!XmlHelp.ifAbsXpath(xPath)) {
            return null;
        }
        Node node = doc.selectSingleNode(xPath);
        if (node == null) {
            String tagName = XmlHelp.getAbsXPathTagName(xPath);
            String xPathParen = XmlHelp.getAbsXPathParen(xPath);
            node = XmlHelp.createTag(doc, xPathParen, tagName);
        }
        boolean temp = node != null;
        if (temp) {
            node.setText(source);
        }
        return OperateResult.of(items, temp, doc);
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
