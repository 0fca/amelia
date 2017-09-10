/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Obsidiam
 */
public class AboutFormController extends Dialog implements Initializable{

    private Parent root = null; 
    @FXML
    private Hyperlink AMELIA_WEBSITE_LINK,GH_LINK;
    
    public AboutFormController(){}
    
    public AboutFormController(Parent root){
        this.root = root;
            
    }
    
    public void showAbout(){
        DialogPane p = new DialogPane();
        System.out.println(root);
        p.setContent(root);
        this.setDialogPane(p);
        this.setTitle("About");
        this.setResizable(false);
        p.getButtonTypes().add(ButtonType.OK);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.show(); 
    }
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GH_LINK.setOnAction(event ->{
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
        
        
        AMELIA_WEBSITE_LINK.setOnAction(event ->{
            try {
                if(System.getProperty("os.name").equals("Linux")){
                    Runtime.getRuntime().exec("xdg-open http://obsidiam.github.io/amelia");
                }else{
                    Desktop.getDesktop().browse(URI.create("http://obsidiam.github.io/amelia"));
                }
            } catch (IOException ex) {
                Logger.getLogger(AboutFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
