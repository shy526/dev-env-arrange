package com.github.shy526.devenvarrange.rpn.xml;


import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import com.github.shy526.devenvarrange.rpn.oo.OperateType;
import com.google.common.collect.Lists;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class XmlOpen implements XmlSymbol {
    @Override
    public OperateResult execute(List<OperateItem> items) {
        OperateItem source = items.get(0);
        SAXReader reader = new SAXReader();
        OperateItem item = null;
        try {
            Document doc = (Document) reader.read(source.getVal(String.class));
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
}
