/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 负责对XML文件的读操作
 *
 * @author Liyu.Cai@Audaque.com
 */
public class XmlUtil {

    public static final String XML_ENCODING = "UTF-8";

    public static Document getDocument(String file) {
        SAXReader reader = new SAXReader();
        reader.setEntityResolver(IgnoreDtdResolver.getInstance());
        try {
            return reader.read(file);
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Document getDocumentByString(String str) {
        SAXReader reader = new SAXReader();
        reader.setEntityResolver(IgnoreDtdResolver.getInstance());
        try {
            return reader.read(new StringReader(str));
        } catch (DocumentException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    public static Element getUniqueElement(Element root, String uniqueXPath) {
        List<Element> eles = root.selectNodes(uniqueXPath);
        if (eles.size() == 1) {
            return eles.get(0);
        } else if (eles.size() > 1) {
            throw new IllegalArgumentException("存在多个符合该XPath的Element");
        }
        return null;
    }

    public static void toFile(File file, Document document) {
        BufferedWriter fw;
        try {
            fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), XML_ENCODING));
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setIndent("\t");
            format.setLineSeparator("\n");
            XMLWriter writer = new XMLWriter(fw, format);
            writer.write(document);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            throw new IllegalStateException("Xml文件写入失败:" + ex.getMessage());
        }
    }
}

class IgnoreDtdResolver implements EntityResolver {

    private static IgnoreDtdResolver instance;

    public static IgnoreDtdResolver getInstance() {
        if (instance == null) {
            instance = new IgnoreDtdResolver();
        }
        return instance;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        return new InputSource(new StringReader(""));
    }
}
