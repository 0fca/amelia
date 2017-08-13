/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import com.neology.exceptions.TransportException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author obsidiam
 */
public class Closed extends TransportState{
    @Override
    public void closeConnection(Transport t) {
        try {
            if(t.isConnected()){
                t.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Closed.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void haltConnection(Transport t) {
        try {
            t.close();
        } catch (IOException ex) {
            Logger.getLogger(Closed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean wasConnected(Transport t) {
        return t.wasConnected();
    }

    @Override
    public boolean isConnected(Transport t) {
        return t.isConnected();
    }
}
