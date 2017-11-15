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
import com.neology.log.Log;
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
    private Thread th;
    private final ConnectionDataHandler cdh = ConnectionDataHandler.getInstance();
    private String name;
    private BaudrateMeter mtr;
    private final ImageDataHandler idh = ImageDataHandler.getInstance();
    private String ext = "JPG";
    
    @Override
    public void start(){
        if(th == null){
            th = new Thread(this,"TCPThread");
            th.start();
        }
    }
    
    @Override
    public void run(){
        while(th != null){
            List<Connection> connections = cdh.getConnectionList();
            synchronized(connections){
                if(connections.size() > 0){
                    while(!cdh.isFree()){
                        try {
                            Log.log("TCPThread#run()","waiting on cdh monitor...");
                            connections.wait();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(TCPThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    if(!th.isInterrupted()){
                            
                            Log.log("TCPThread#run()","starting reading...");
                            for(int i = 0; i < connections.size(); i++){
                                Transport t;
                                Connection c = connections.get(i);

                                mtr = c.getTranportInstance().getBaudrateMeter();
                                mtr.startMeasuringCycle();


                                if(c.getState() == com.neology.net.states.State.CLOSED){
                                    continue;
                                }

                                if(c.getState() == com.neology.net.states.State.ESTABLISHED){
                                    t = c.getTranportInstance();
                                    Log.log("Transporter IP",t.getIp());
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
                                           c.getTranportInstance().flush();
                                           Image im = processData(inputDataBuffer);
                                           idh.getImagesMap().put(ip, im);
                                           int b = t.getBaudrateMeter().kBPS();
                                           String data = "";
                                           data += "Client name: "+name+",";
                                           data += "IP: "+t.getIp().substring(1)+",";
                                           data += "Speed: "+( (b < 10000) ? (b +"kB/s,") : (t.getBaudrateMeter().Mbps())+"MB/s,");
                                           data += "Is connected: "+t.isConnected()+",";
                                           data += "Was connected earlier: "+c.wasConnected();
                                           cdh.getData().put(ip, data);
                                           if(!c.isNameSet()){
                                              c.setConnectionName(name);
                                           }
                                            
                                    } catch (TransportException | IOException ex) {
                                        Log.log(System.err,"LOCALIZED_ERR_MSG",ex.getLocalizedMessage());
                                        c.changeState(new Closed());
                                        break;
                                    }  
                                }
                            }
                        mtr.stopMeasuringCycle();
                        cdh.setFree(false);
                        Log.log("TCPThread#run()","releasing cdh monitor.");
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
        if(th != null){
            if(th.getState() != State.WAITING){
                th.interrupt();
                th = null;
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
            this.name = name;
            Log.log("File name received",name);
            result = Arrays.copyOfRange(buffer,len,8192);
            is = new ByteArrayInputStream(result);
            Log.log("Available byte count",String.valueOf(is.available()));
            BufferedImage bf = ImageIO.read(is);
           
            ImageIO.write(bf, ext, new FileOutputStream(LocalEnvironment.getLocalVar(Local.TMP)+File.separator+name+"."+ext));
            Image out = new Image("file:///"+LocalEnvironment.getLocalVar(Local.TMP)+File.separator+name+"."+ext,250,150,true,true);
            Log.log("Is error",String.valueOf(out.isError()));
            Log.log("Image loader state", String.valueOf(out.getProgress()));
            Log.signAsLast();
            mtr.count(buffer.length);
            
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
