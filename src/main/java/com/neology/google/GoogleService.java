/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.google;

import com.neology.controllers.AboutFormController;
import com.neology.controllers.SettingsFormsController;
import com.neology.interfaces.Viewable;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

/**
 *
 * @author obsidiam
 */
public class GoogleService extends Service implements Viewable {

    @Override
    protected Task createTask() {
            try {
                Authorization.authorize();
                
            } catch (IOException ex) {
                Logger.getLogger(SettingsFormsController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        return null;
    }
    @Override
    public void viewAlert(String name, String header, String content, Alert.AlertType type) {
       Alert a = new Alert(type);
       a.setTitle(name);
       a.setHeaderText(header);
       a.setContentText(content);
       a.showAndWait();
    }
     
    @Override
    public void viewError(String text) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText("An error occured.");
        a.setContentText(text);
        a.showAndWait();
    } 

    @Override
    public void viewCustom() {
        Dialog dialog = new Dialog();
        dialog.setTitle("Google Drive Access");
        dialog.setHeaderText("App is trying to gain the access to your Google Drive account...");
        dialog.setHeight(300);
        dialog.setWidth(400);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        Hyperlink h = new Hyperlink();
        h.setOnAction( evt ->{
            try {
                if(System.getProperty("os.name").equals("Linux")){
                    Runtime.getRuntime().exec("xdg-open http://www.github.com/Obsidiam");
                }else{
                    Desktop.getDesktop().browse(URI.create("http://www.github.com/Obsidiam"));
                }
            } catch (IOException ex) {
                Logger.getLogger(AboutFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
}
