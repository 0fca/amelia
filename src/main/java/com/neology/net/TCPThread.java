/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import com.neology.net.states.Transport;
import com.neology.net.states.Closed;
import com.neology.environment.LocalEnvironment;
import com.neology.data.ConnectionDataHandler;
import com.neology.data.ImageDataHandler;
import com.neology.exceptions.TransportException;
import com.neology.environment.Local;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author obsidiam
 */
public class TCPThread extends Thread implements Runnable{
    private Thread TH;
    private final ConnectionDataHandler cdh = ConnectionDataHandler.getInstance();
    private String NAME;
    private BaudrateMeter MTR;
    private final ImageDataHandler IDH = ImageDataHandler.getInstance();
    private final LocalEnvironment LOCAL = new LocalEnvironment() {};
    private String ext = "JPG";
    
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
            List<Connection> connections = cdh.getConnectionList();
            synchronized(connections){
                if(connections.size() > 0){
                    while(!cdh.isFree()){
                        try {
                            System.out.println("TCPThread#run() -> waiting on cdh monitor...");
                            connections.wait();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(TCPThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    if(!TH.isInterrupted()){
                            
                            System.out.println("TCPThread#run() -> starting reading...");
                            for(int i = 0; i < connections.size(); i++){
                                Transport t;
                                Connection c = connections.get(i);

                                MTR = c.getTranportInstance().getBaudrateMeter();
                                MTR.startMeasuringCycle();


                                if(c.getState() == com.neology.net.states.State.CLOSED){
                                    continue;
                                }

                                if(c.getState() == com.neology.net.states.State.ESTABLISHED){
                                    t = c.getTranportInstance();
                                    System.out.println("TRANSPORTER_IP: "+t.getIp());
                                    String ip = t.getIp().split(":")[0];
                                    byte[] inputDataBuffer;
                                    byte[] outputDataBuffer = new byte[256];
                                    outputDataBuffer[0] = 1;
                                    
                                    try {
                                        Connection udp = cdh.getUdpConnection();
                                        
                                            if(udp != null){
                                                if(ip.equals(udp.getTranportInstance().getIp())){
                                                   c.write(outputDataBuffer);
                                                }
                                            }
                                           inputDataBuffer = c.read();
                                           Image im = processData(inputDataBuffer);
                                           IDH.getImagesMap().put(ip, im);
                                           int b = t.getBaudrateMeter().kBPS();
                                           String data = "";
                                           data += "Client name: "+NAME+",";
                                           data += "IP: "+t.getIp().substring(1)+",";
                                           data += "Speed: "+( (b < 10000) ? (b +"kB/s,") : (t.getBaudrateMeter().Mbps())+"MB/s,");
                                           data += "Is connected: "+t.isConnected()+",";
                                           data += "Was connected earlier: "+c.wasConnected();
                                           cdh.getData().put(ip, data);
                                           if(!c.isNameSet()){
                                              c.setConnectionName(NAME);

                                           }
                                            
                                    } catch (TransportException | IOException ex) {
                                        System.err.println("LOCALIZED_ERR_MSG:"+ex.getLocalizedMessage());
                                        c.changeState(new Closed());
                                        break;
                                    }  
                                }
                            }
                        MTR.stopMeasuringCycle();
                        cdh.setFree(false);
                        System.out.println("TCPThread#run() -> releasing cdh monitor.");
                        connections.notifyAll();
                    }else{
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    public void interrupt(){
        if(TH != null){
            if(TH.getState() != State.WAITING){
                TH.interrupt();
                TH = null;
                cdh.setFree(false);
            }
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
            System.out.println("AVAILABLE_BYTE_COUNT: "+is.available());
            BufferedImage bf = ImageIO.read(is);
           
            ImageIO.write(bf, ext, new FileOutputStream(LOCAL.getLocalVar(Local.TMP)+File.separator+name+"."+ext));
            Image out = new Image("file:///"+LOCAL.getLocalVar(Local.TMP)+File.separator+name+"."+ext,250,150,true,true);
            System.err.println("Is error: "+out.isError());
            System.out.println("IMAGE_LOADER_STATE: "+out.getProgress()+"\n");
            MTR.count(buffer.length);
            
            return out;
        }

    private char[] readMetaData(int len, byte[] buffer) {
        char[] c = new char[len];

        for(int i = 1; i<len; i++){
            c[i] = (char)((int)buffer[i]);
        }
        return c;
    }
}
