/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import com.neology.net.states.Transport;
import com.neology.net.states.Closed;
import com.neology.net.states.Established;
import com.neology.net.states.State;
import abstracts.LocalEnvironment;
import com.neology.data.ConnectionDataHandler;
import com.neology.data.ImageDataHandler;
import com.neology.exceptions.TransportException;
import enums.Local;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author obsidiam
 */
public class TCPThread extends Thread implements Runnable{
    private Thread TH;
    private ConnectionDataHandler cdh = ConnectionDataHandler.getInstance();
    private String NAME;
    private BaudrateMeter MTR;
    private final ImageDataHandler IDH = ImageDataHandler.getInstance();
    private final ConnectionDataHandler CDH = ConnectionDataHandler.getInstance();
    private final LocalEnvironment LOCAL = new LocalEnvironment() {};
    private static volatile int closedCount = 0;
    
    @Override
    public void start(){
        if(TH == null){
            TH = new Thread(this,"TCPThread");
            TH.start();
        }
    }
    
    @Override
    public void run(){
        
        while(TH != null){
            if(!TH.isInterrupted()){
                    ArrayList<Connection> connections = cdh.getConnectionList();
                    for(int i = 0; i < connections.size(); i++){
                            Transport t = null;
                            Connection c = connections.get(i);
                            
                            MTR = c.getTranportInstance().getBaudrateMeter();
                            MTR.startMeasuringCycle();
                            if(c.getState() == com.neology.net.states.State.OPENED){
                               Established e = new Established();
                               c.changeState(e);
                            }

                            if(c.getState()  == com.neology.net.states.State.CLOSED){
                                continue;
                            }
                            t = c.getTranportInstance();
                            System.out.println("TRANSPORTER_IP: "+t.getIp());

                            byte[] buffer;

                            try {
                                   buffer = c.read(t);
                                   Image im = processData(buffer);
                                   IDH.getImagesMap().put(NAME, im);

                                   String data = "";
                                   data += "Client name: "+NAME+",";
                                   data += "IP: "+t.getIp().substring(1)+",";
                                   data += "Speed: "+t.getBaudrateMeter().kBPS()+"kB/s,";
                                   data += "Is connected: "+t.isConnected()+",";
                                   data += "Was connected earlier: "+c.wasConnected();
                                   CDH.getData().put(t.getIp(), data);
                                   if(!c.isNameSet()){
                                      c.setConnectionName(NAME);
                                   }
                                   CDH.putConnectionName(NAME, t.getIp());
                            } catch (TransportException | IOException ex) {
                                System.err.println("LOCALIZED_ERR_MSG:"+ex.getLocalizedMessage());
                                c.changeState(new Closed());
                                break;
                            }  
                    }
                
            }else{
                break;
            }
        }
    }
    
    @Override
    public void interrupt(){
        if(TH != null){
            TH.interrupt();
            TH = null;
        }
    }
    
    private Image processData(byte[] buffer) throws IOException {
            ByteArrayInputStream is;
            byte[] result;
            int len = (int)buffer[0]+1;
            char[] c = readMetaData(len, buffer);

            String name = String.valueOf(c).trim();
            NAME = name;
            System.out.println("FILE_NAME_RECEIVED: "+name);
            result = Arrays.copyOfRange(buffer,len,8192);
            is = new ByteArrayInputStream(result);

            BufferedImage bf = ImageIO.read(is);
            System.out.println("AVAILABLE_BYTE_COUNT: "+is.available());
            ImageIO.write(bf, "JPG", new FileOutputStream(LOCAL.getLocalVar(Local.TMP)+File.separator+name+".jpg"));
            Image out = new Image("file:///"+LOCAL.getLocalVar(Local.TMP)+File.separator+name+".jpg",250,200,true,true);
            System.err.println("Is error: "+out.isError());
            System.out.println("IMAGE_LOADER_STATE: "+out.getProgress()+"\n");
            MTR.count(buffer.length);
            MTR.stopMeasuringCycle();
            return out;
        }

    private char[] readMetaData(int len, byte[] buffer) {
        char[] c = new char[len];

        for(int i = 1; i<len; i++){
            c[i] = (char)((int)buffer[i]);
        }
        return c;
    }
    
    public synchronized static int getClosedCount(){
        return closedCount;
    }
}
