/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Deprecated as is the activeIpChecker class is used instead.
 * @author Obsidiam
 * 
 */
@Deprecated
 public class Connector extends Thread implements Runnable{
        private boolean isConnected = false;
        private Thread inside;
        private static ArrayList<String> POOL = new ArrayList<>();
        private static ArrayList<String> ACTV_POOL = new ArrayList<>();
   
        
        Transporter TRANS;
        NetController NET = new NetController();
        
       
        
        public void setTransporter(Transporter trans){
            this.TRANS = trans;
        }
        
        boolean getConnectionState(){
            return isConnected;
        }
        
        @Override
        public void run(){
            try{
              // ACTV_POOL = NET.prepareActiveIpsList(POOL);
            }finally{
                tryConnection(TRANS);
                System.out.println("Connected.");
                isConnected = true;
            }
        }
        
        @Override 
        public void start(){
            if(inside == null){
                 inside = new Thread(this);
                 inside.start();
            }
        }
        

        void tryConnection(Transporter trans){
                try {
                    //trans.prepareConnection(ACTV_POOL);
                } catch (Exception ex) {
                    Logger.getLogger(Connector.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        
    }
