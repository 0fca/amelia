/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.interfaces;

import java.sql.SQLException;

/**
 *
 * @author Obsidiam
 */
public interface Connectable {
    public boolean isConnected();
    public boolean wasConnected();
    public boolean tableExists(String name);
    public boolean tablesExist() throws SQLException;
}
