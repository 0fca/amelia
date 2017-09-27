/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Obsidiam
 */
public class SettingsForm extends Application {
    public boolean CHANGED = false;
    public boolean SAVED = false;
    private Stage STAGE;
    
    @Override
    public void start(Stage stage) throws Exception {
        this.STAGE = stage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/SettingsForms.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setOnCloseRequest(event ->{
            if(CHANGED&SAVED){
                //viewAlert("Settings","Closing settings window.","Settings were changed, but hasn't been saved!",AlertType.INFORMATION);
            }else{
                stage.close();
            }
        });
        
        stage.setTitle("Amelia - Settings");
        stage.setScene(scene);
        stage.show();
        
    }
}
