package com.example.test.GpLog.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlCheckReplace {
    public static void replace() {
        String base = "/Users/rejig/AndroidStudioProjects/test/app/src/main/res/";
        String fileName = "strings.xml";
        File defaultFile = new File(String.format("%s%s/%s", base, "values", fileName));
        File enFile = new File(String.format("%s%s/%s", base, "values-zh-rTW", fileName));
        checkTree(defaultFile, enFile);
    }

    private static Map<String, NodeInfo> getReplaceMap(File toReplace) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(toReplace);
            Node node = document.getFirstChild();
            NodeList list = node.getChildNodes();
            int len = list.getLength();
            Map<String, NodeInfo> valueMap = new HashMap<>();
            for (int i = 0; i < len; i++) {
                Node child = list.item(i);
                String nodeName = child.getNodeName();
                if ("string".equals(nodeName)) {
                    String key = child.getAttributes().getNamedItem("name").getNodeValue();
                    Node translateNode = child.getAttributes().getNamedItem("translatable");
                    boolean translate = translateNode == null || translateNode.getNodeValue().equals("true");
                    if (child.getFirstChild() == null) {
                        valueMap.put(child.getAttributes().getNamedItem("name").getNodeValue(), new NodeInfo(key, translate, translateNode, null));
                    } else {
                        String sValue = child.getFirstChild().getNodeValue();
                        valueMap.put(child.getAttributes().getNamedItem("name").getNodeValue(), new NodeInfo(key, translate, translateNode, sValue));
                    }
                } else if ("#text".equals(nodeName)) {
                    // do nothing
                } else if ("#comment".equals(nodeName)) {
                    // do nothing
                } else {
                    String key = child.getAttributes().getNamedItem("name").getNodeValue();
                    System.err.println("un-handle node: " + nodeName + "\t " + key);
                }
//                System.out.println(child.getAttributes());
            }
            return valueMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void checkTree(File ori, File toReplace) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(ori);
            Node node = document.getFirstChild();
            NodeList list = node.getChildNodes();
            int len = list.getLength();
            Map<String, NodeInfo> valueMap = getReplaceMap(toReplace);
            if (valueMap == null) {
                return;
            }
            for (int i = 0; i < len; i++) {
                Node child = list.item(i);
                String nodeName = child.getNodeName();
                if ("string".equals(nodeName)) {
                    String key = child.getAttributes().getNamedItem("name").getNodeValue();
                    Node translateNode = child.getAttributes().getNamedItem("translatable");
                    boolean translate = translateNode == null || translateNode.getNodeValue().equals("true");
                    Node textValue = child.getFirstChild();
                    if (textValue == null) {
//                        child.appendChild()
                        System.out.println("null text value jump: " + key);
                        continue;
                    }
                    String sValue = child.getFirstChild().getNodeValue();
                    if (sValue == null) {
                        System.err.println("sValue null: " + key);
                        continue;
                    }
                    if (translate) {
                        NodeInfo nodeInfo = valueMap.get(key);
                        if (nodeInfo != null) {
                            if (nodeInfo.sValue == null) {
                                System.err.println("value null: " + key + " " + sValue);
                            } else if (!sValue.equals(nodeInfo.sValue)) {
                                child.getFirstChild().setNodeValue(nodeInfo.sValue);
                            } else {
                                System.out.println("value equals no change: " + key + " " + sValue);
                            }
                        } else {
                            System.err.println("translate but node no found:\t " + key);
                        }
                    } else {
                        System.out.println("no translate: " + key);
                    }
                } else if ("#text".equals(nodeName)) {
                    // do nothing
                } else if ("#comment".equals(nodeName)) {
                    // do nothing
                } else {
                    String key = child.getAttributes().getNamedItem("name").getNodeValue();
                    System.err.println("un-handle node: " + nodeName + "\t " + key);
                }
            }
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(ori));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testNode() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            File file = new File("/Users/rejig/AndroidStudioProjects/test/app/src/main/res/layout/activity_launcher_test.xml");
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            Node node = document.getFirstChild();
            NodeList list = node.getChildNodes();
            int len = list.getLength();
            for (int i = 0; i < len; i++) {
                Node child = list.item(i);
                System.out.println(child);
            }
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class NodeInfo {
        String key;
        boolean translate;
        Node node;
        String sValue;

        public NodeInfo() {
        }

        public NodeInfo(String key, boolean translate, Node node, String sValue) {
            this.key = key;
            this.translate = translate;
            this.node = node;
            this.sValue = sValue;
        }
    }
}
