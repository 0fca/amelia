/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.xml;

import abstracts.LocalEnvironment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.*;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Document;
import org.xml.sax.*;

/**
 *
 * @author Obsidiam
 */
public class XMLController extends LocalEnvironment{
    SAXParserFactory SPF = SAXParserFactory.newInstance();

    public void createInitXML(String xml_path,String subnet,String count) throws SAXException, ParserConfigurationException, IOException, XMLStreamException{
        XMLOutputFactory xof =  XMLOutputFactory.newInstance();
        XMLStreamWriter xtw = null;
        xtw = xof.createXMLStreamWriter(new FileWriter("init.xml"));
        xtw.writeStartDocument("utf-8","1.0");
        xtw.writeCharacters("\n");
        xtw.writeStartElement("SETTINGS");
        xtw.writeCharacters("\n");
        xtw.writeStartElement("XML_PATH");
        xtw.writeCharacters("\n");
        xtw.writeCharacters(xml_path);
        xtw.writeCharacters("\n");
        xtw.writeEndElement();
        xtw.writeCharacters("\n");
        xtw.writeStartElement("SUBNET");
        xtw.writeCharacters("\n");
        xtw.writeCharacters(subnet);
        xtw.writeCharacters("\n");
        xtw.writeEndElement();
        xtw.writeCharacters("\n");
        xtw.writeStartElement("count");
        xtw.writeCharacters("\n");
        xtw.writeCharacters(count);
        xtw.writeCharacters("\n");
        xtw.writeEndElement();
        xtw.writeCharacters("\n");
        xtw.writeEndElement();
        xtw.writeCharacters("\n");
        xtw.writeEndDocument();
        xtw.flush();
        xtw.close();
    }
    
    private String getRandomPID(){
        Random rand = new Random();
        int a = rand.nextInt(10);
        Random rand2 = new Random();
        int b = rand2.nextInt(10);
        Random rand3 = new Random();
        int c = rand3.nextInt(10);
        Random rand4 = new Random();
        int d = rand4.nextInt(10);
        String out = a+""+b+""+c+""+d;
        return out;
    }
    
    public ArrayList parseInitFile() throws SAXException, IOException, ParserConfigurationException{
        ArrayList<String> list = new ArrayList<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder(); 
        Document doc = db.parse(new File("init.xml"));
        String store = doc.getElementsByTagName("PORT").item(0).getTextContent();
        String xml_path = doc.getElementsByTagName("XML_PATH").item(0).getTextContent();
        String addr = doc.getElementsByTagName("ADDR").item(0).getTextContent();
        list.add(store);
        list.add(addr);
        return list;
    }
    
    public Document documentBuilder(String name) throws SAXException, IOException, ParserConfigurationException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new File(name));
    }
   
    
    public void writeXMLFile(HashMap<String,String> lines,String path) throws IOException, XMLStreamException{
        XMLOutputFactory xof =  XMLOutputFactory.newInstance();
        final XMLStreamWriter xtw = xof.createXMLStreamWriter(new FileWriter(path));
        xtw.writeStartDocument("utf-8","1.0");
        xtw.writeCharacters("\n");
        
        lines.forEach((key,value) ->{
            try {
                xtw.writeStartElement(key);
                xtw.writeCharacters(value);
                xtw.writeEndElement();
                xtw.writeCharacters("\n");
            } catch (XMLStreamException ex) {
                Logger.getLogger(XMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        xtw.writeEndDocument();
        xtw.flush();
        xtw.close();
    }
}
