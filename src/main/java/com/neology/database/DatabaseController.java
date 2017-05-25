/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.database;

import com.neology.interfaces.Connectable;
import com.neology.net.Transport;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Obsidiam
 * @deprecated
 */
public class DatabaseController implements Connectable{
    private boolean IS_CONNECTED = false;
    private boolean WAS_CONNECTED = false;
    protected Connection CON;
    protected Statement STMT;
    protected ResultSet RESULT;
    protected PreparedStatement PREP_STMT;
    boolean var;
    
    
    public void createConnection(String db) throws ClassNotFoundException, SQLException{
        try{
            CON = DriverManager.getConnection("jdbc:sqlite:"+db);
            STMT = CON.createStatement();
            
        }finally{
            IS_CONNECTED = true;
            WAS_CONNECTED = true;
            System.out.println("Connection to database succesfull!"); 
        }        
    }
    
    public boolean isConnected() {
        return IS_CONNECTED;
    }

    public boolean wasConnected() {
        return WAS_CONNECTED;
    }

    public boolean tableExists(String name) {
        try {
            RESULT = STMT.executeQuery("SHOW TABLES LIKE '"+name+"'");
            if(RESULT.wasNull()){
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
   
    
    public String getUserFromUsersTable(String login,String pass) throws SQLException{
        String query ="SELECT * FROM users";
        String result = "";
        RESULT = STMT.executeQuery(query);
        
        while(RESULT.next()){
            if(RESULT.getString("login").equals(login)){
                result = RESULT.getString("login");
            }
        }
        return result;
    }
    
    
    public boolean deleteRecord(int index,String brand,String model) throws SQLException{
             boolean out = false;
             if(IS_CONNECTED){
                 try{
                     String query = "DELETE FROM wypozyczenia WHERE id = "+index;
                     PREP_STMT = CON.prepareStatement(query);
                     PREP_STMT.execute();
                 }finally{
                     out = true;
                 }
             }else{
                 System.err.print("Database not connected.");
                 return false;
             }
             return out;
    }
    
    
    public void addToUsersTable(String login,String pass,boolean isAdmin) throws SQLException{
             if(login != null&pass != null){
                 String command = "INSERT INTO users(login,pass,is_admin) VALUES('"+login+"','"+pass+"','"+String.valueOf(isAdmin)+"')";
                 PREP_STMT = CON.prepareStatement(command);
                 PREP_STMT.execute();
             }
    } 

    public boolean tablesExist() throws SQLException{
            RESULT = STMT.executeQuery("SHOW TABLES");
            if(RESULT.wasNull()){
                return false;
            }
       return true;
    }



    @Override
    public boolean isConnected(Transport t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean wasConnected(Transport t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void closeConnection(Transport t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void haltConnection(Transport t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendPacket(Transport t, byte[] buffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void openConnection(InputStream in, OutputStream out) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}    
    

