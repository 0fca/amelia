/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.environment;

import com.neology.parsing.XMLController;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Obsidiam
 */
public abstract class LocalEnvironment {
    public String getLocalVar(Local l) throws NullPointerException{
        String var = null;
        XMLController xml = new XMLController();
        
        switch(l){
            case LOCAL:
                var = Paths.get(".").toAbsolutePath().normalize().toString();
                break;
            case USER_NAME:
                var = System.getProperty("user.name");
                break;
            case OS:
                var = System.getProperty("os.name");
                break;
            case SEPARATOR:
                var = File.separator;
                break;
            case SUBNET:{
                try {
                    var = xml.parseInitFile().get(0).toString();
                } catch (SAXException | IOException | ParserConfigurationException ex) {
                    Logger.getLogger(LocalEnvironment.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                break;
            case USER_HOME:
                var = System.getProperty("user.home");
                break;
            
            case TMP:
                if(System.getProperty("os.name").contains("Linux")){
                    var = "/tmp/amelia";
                }else if(System.getProperty("os.name").contains("Windows")){
                    var = "C:\\Users\\"+System.getProperty("user.name")+"\\AppData\\Local\\Temp\\Amelia";
                }
                break;
        }
       return var;
    }
}
