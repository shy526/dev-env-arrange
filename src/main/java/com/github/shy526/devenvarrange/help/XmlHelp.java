package com.github.shy526.devenvarrange.help;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

public class XmlHelp {
    private final static String SEPARATE = "/";
    private final static String PROTOCOL_SEPARATE = ":";
    private final static String ARRAY_SEPARATE_PREFIX = "[";
    private final static String ARRAY_SEPARATE_SUFFIX = "]";
    private final static String ARRAY_LAST = "last()";


    /**
     * 支持循环创建,会根据xpath去创建父标签
     * 不支持创建root标签
     * 父标签 数组 只支持last
     * @param document 文档
     * @param absXPath 父标签xpath
     * @param tag 创建标签
     * @return null
     */
    public static Element createTag(Document document, String absXPath, String tag) {
        Node node = document.selectSingleNode(absXPath);
        if (node == null) {
            String tagName = getAbsXPathTagName(absXPath);
            boolean flag=tagName.contains(ARRAY_SEPARATE_PREFIX);
            if (flag){
                if (tagName.contains(ARRAY_LAST)){
                    tagName=tagName.substring(0,tagName.lastIndexOf(ARRAY_SEPARATE_PREFIX));
                }else {
                    return null;
                }
            }
            String absXPathParen = getAbsXPathParen(absXPath);
            if ("".equals(absXPathParen)) {
                return null;
            }
            node = createTag(document, absXPathParen, tagName);
        }
        if (node != null) {
            return ((Element) node).addElement(tag);
        }
        return null;
    }

    public static String getAbsXPathTagName(String absXPath) {
        String[] split = absXPath.substring(absXPath.lastIndexOf(SEPARATE) + 1).split(PROTOCOL_SEPARATE);
        return split.length > 1 ? split[1] : split[0];
    }

    public static String getAbsXPathParen(String absXPath) {
        return absXPath.substring(0, absXPath.lastIndexOf(SEPARATE));
    }

    public static boolean ifAbsXpath(String XPath) {
        String index = XPath.substring(0, 2);
        return !index.equals(SEPARATE + SEPARATE);
    }
}
