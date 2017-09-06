/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author obsidiam
 */
public class Opened extends TransportState{
    private Transport T;
    @Override
    public void openConnection(InputStream in, OutputStream out) {
        T = new Transport(in,out);
    }
    
    public boolean wasConnected(){
        return T.wasConnected();
    }
    
    @Override
    public Transport getTransportInstance(){
        return T;
    }
}
