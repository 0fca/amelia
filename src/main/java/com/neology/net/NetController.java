/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import com.neology.interfaces.Reachable;
import com.neology.xml.XMLController;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.net.util.SubnetUtils;
import org.xml.sax.SAXException;

/**
 *
 * @author Obsidiam
 */
public class NetController implements Reachable{
    
    public String[] getIpPool(String subnet) throws SocketException{
         SubnetUtils utils = new SubnetUtils(subnet);
         
         return utils.getInfo().getAllAddresses();
    }
    
    public String[] prepareIpsPool() throws NullPointerException, UnknownHostException, ParserConfigurationException, SAXException, IOException{
        XMLController xml = new XMLController();
        String subnet = xml.parseInitFile().get(0).toString();
        //int count = Integer.parseInt(xml.parseInitFile().get(2).toString());
        String[] pool = getIpPool(subnet);
        String[] actual = new String[pool.length]; 
        
        System.arraycopy(pool, 0, actual, 0, pool.length);
        return actual;
    }  
    
    public CopyOnWriteArrayList prepareActiveIpsList(CopyOnWriteArrayList<String> pool){
        CopyOnWriteArrayList<String> actv_pool = new CopyOnWriteArrayList<>();
        
        pool.forEach(ip ->{
            try {
                if(isReachable(ip)){
                    actv_pool.add(ip);
                }
            } catch (Exception ex) {
                Logger.getLogger(NetController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        System.out.println("List done!");
        return actv_pool;
    }

    @Override
    public boolean isReachable(String ip) throws Exception {
        try {
            if(InetAddress.getByName(ip).isReachable(10)){
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(NetController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }


    @Override
    public boolean isConnected(String ip) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }  
}
