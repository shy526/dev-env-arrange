package com.github.shy526.devenvarrange.rpn.xml;

import com.github.shy526.devenvarrange.rpn.Symbol;
import com.github.shy526.devenvarrange.rpn.oo.OperateItem;
import org.dom4j.Document;

import java.util.List;

/**
 * @author shy526
 */
public interface XmlSymbol extends Symbol {

    default Document getDocVal(List<OperateItem> operateItems, int index) {
        return operateItems.get(index).getVal(Document.class);
    }
}
