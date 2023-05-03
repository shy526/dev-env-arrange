package com.github.shy526.devenvarrange.rpn.xml;

import com.github.shy526.devenvarrange.help.XmlHelp;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.google.common.collect.Lists;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class XmlAdd implements XmlSymbol {
    @Override
    public OperateResult execute(List<OperateItem> items) {
        OperateItem tagName = items.get(0);
        OperateItem xPath = items.get(1);
        OperateItem source = items.get(2);
        Document sourceVal = source.getVal(Document.class);
        String xPathVal = xPath.getVal(String.class);
        String tagNameVal = tagName.getVal(String.class);
        Element tag = XmlHelp.createTag(sourceVal, xPathVal, tagNameVal);
        if (tag == null) {
            return OperateResult.of(Lists.newArrayList(source), Lists.newArrayList(source, xPath, tagName), false);
        }
        return OperateResult.of(Lists.newArrayList(source), Lists.newArrayList(source, xPath, tagName), true);
    }

    @Override
    public String getSymbolStr() {
        return "+";
    }

    @Override
    public int getOperandNum() {
        return 3;
    }
}
