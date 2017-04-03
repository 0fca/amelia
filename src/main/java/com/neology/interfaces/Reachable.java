/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.interfaces;

import java.nio.file.Paths;


/**
 *
 * @author Obsidiam
 */
public interface Reachable {
    public boolean isReachable(String ip) throws Exception;
    public boolean isConnected(String ip);
}
