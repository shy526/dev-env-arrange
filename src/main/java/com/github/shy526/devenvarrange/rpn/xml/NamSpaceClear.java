package com.github.shy526.devenvarrange.rpn.xml;

import org.dom4j.*;
import org.dom4j.tree.DefaultElement;

public class NamSpaceClear extends VisitorSupport {
    @Override
    public void visit(Document document) {
        ((DefaultElement) document.getRootElement())
                .setNamespace(Namespace.NO_NAMESPACE);
        document.getRootElement().additionalNamespaces().clear();
        super.visit(document);
    }

    @Override
    public void visit(Namespace namespace) {
        namespace.detach();
        super.visit(namespace);
    }

    @Override
    public void visit(Attribute node) {
        if (node.toString().contains("xmlns")
                || node.toString().contains("xsi:")) {
            node.detach();
        }
        super.visit(node);
    }

    @Override
    public void visit(Element node) {
        if (node instanceof DefaultElement) {
            ((DefaultElement) node).setNamespace(Namespace.NO_NAMESPACE);
        }
        super.visit(node);
    }
}
