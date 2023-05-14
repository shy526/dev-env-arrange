package com.github.shy526.devenvarrange.rpn.xml;

import com.github.shy526.devenvarrange.help.XmlHelp;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import com.github.shy526.devenvarrange.rpn.oo.OperateResult;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shy526
 */
@Component
public class XmlAdd implements XmlSymbol {
    @Override
    public OperateResult execute(List<OperateItem> items) {
        Document sourceVal = getDocVal(items, 2);
        String xPathVal = getStrVal(items, 1);
        String tagNameVal = getStrVal(items, 0);
        Element tag = XmlHelp.createTag(sourceVal, xPathVal, tagNameVal);
        return OperateResult.of(items, tag != null, sourceVal);
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
