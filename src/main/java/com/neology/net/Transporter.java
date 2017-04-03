/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import abstracts.LocalEnvironment;
import com.neology.interfaces.Reachable;
import enums.Local;
import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author Obsidiam
 * @deprecated 
 */
public class Transporter extends LocalEnvironment implements Reachable{
    private int ADDRESS_COUNT = 0;
    String ACTUAL_NAME = "";
    final CopyOnWriteArrayList<Socket> TABLE = new CopyOnWriteArrayList<>();
    private ArrayList<String> LIST;
    private static volatile Transporter TRANS;
    
    private Transporter(){}
    
    public static synchronized Transporter getInstance(){
        if(TRANS == null){
            TRANS = new Transporter();
        }
        return TRANS;
    }
    
    public int getAddressCount(){
        return ADDRESS_COUNT;
    }
    
    public int getIpIndex(String ip){
        return TABLE.indexOf(ip);
    }
    
    public synchronized void prepareConnection(CopyOnWriteArrayList<String> pool) throws Exception{
            pool.forEach(ip ->{
                Socket con = new Socket();
                try{
                    System.out.println("Checking if still active...");
                    if(isReachable(ip)){
                        con.connect(new InetSocketAddress(ip,7999));
                        con.setTcpNoDelay(true);
                        TABLE.add(con);
                       
                    }else{
                        System.out.println(ip+" is unreachable.");
                    }
                }catch (Exception ex) {
                        System.err.println("Error while trying to connect IP: "+ip);
                }finally{
                    if(con.isConnected()){
                        System.out.println(ip+" is active!\n");
                    }
                }  
            });

        ADDRESS_COUNT = TABLE.size();
    }
   
    public void downloadScreen() throws AWTException, IOException {
            TABLE.forEach(socket ->{
                try {
                    if(TABLE.size() > 0&isReachable(socket.getInetAddress().getHostAddress())){
                        try{
                            OutputStream ou = null;
                            InputStream in = null;
                            System.out.println("Buffer set up.");
                            System.out.println(socket);
                            in = socket.getInputStream();
                            byte[] buffer = new byte[8192];
                            in.read(buffer);
                            System.out.println("Reading stream to buffer...");
                            int len = (int)buffer[0]+1;
                            char[] c = new char[len];
                            
                            for(int i = 1; i<len; i++){
                                c[i] = (char)((int)buffer[i]);
                            }
                            
                            String name = String.valueOf(c);
                            this.ACTUAL_NAME = name;
                            System.out.println("Name received: "+name);
                            System.out.println("OutputStream directed to: "+Paths.get(".").toAbsolutePath().normalize().toString()+File.separatorChar+name+".jpg");
                            ou = new FileOutputStream(new File(getLocalVar(Local.LOCAL)+getLocalVar(Local.SEPARATOR)+name.trim()+".jpg"));
                            
                            try{
                                ou.write(buffer,len,8192-len);
                            }finally{
                                ou.flush();
                                ou.close();
                                new File(Paths.get(".").toAbsolutePath().normalize().toString()+File.separatorChar+this.ACTUAL_NAME+".jpg").delete();
                                System.gc();
                                System.out.println("Downloaded.");
                            }
                            
                        }catch(IOException e){
                            System.err.println("Error while doing I/O operation. Cause: "+e.getMessage());
                            if(new File(Paths.get(".").toAbsolutePath().normalize().toString()+File.separatorChar+this.ACTUAL_NAME+".jpg").exists()){
                                new File(Paths.get(".").toAbsolutePath().normalize().toString()+File.separatorChar+this.ACTUAL_NAME+".jpg").delete();
                                socket.close();
                            }
                        }
                    }else{
                        socket.close();
                        TABLE.remove(socket);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Transporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            setFilesList();
        
     }

    private void setFilesList() throws IOException{
        Stream<Path> files = Files.list(new File(getLocalVar(Local.LOCAL)).toPath());
        ArrayList<String> list = new ArrayList<>();
        files.forEachOrdered(img ->{
            if(img.toFile().getName().endsWith(".jpg")){
               list.add(img.toFile().getAbsolutePath());
            }
        });  
        this.LIST = list;
    }
    
    public ArrayList getFilesList(){
        return this.LIST;
    }
    
    public synchronized void stop() throws IOException{
         TABLE.forEach(ip ->{
             try {
                 System.out.print(ip);
                 Socket soc = ip;
                 if(soc.isConnected()){
                    soc.close();
                 }
             } catch (IOException ex) {
                 Logger.getLogger(Transporter.class.getName()).log(Level.SEVERE, null, ex);
             }
         });
    }
    
    public static BufferedImage toBufferedImage(Image img){
        if (img instanceof BufferedImage){
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    @Override
    public boolean isReachable(String ip) throws Exception {
        if(InetAddress.getByName(ip).isReachable(1000)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isConnected(String ip) {
        boolean isConnected = false;
        Socket test = TABLE.get(TABLE.indexOf(ip));
        
        if(test.isConnected()){
            isConnected = true;
        }
        return isConnected;
    }

    @Override
    public String preparePath(String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
