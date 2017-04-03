/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.todosaver;

import com.neology.interfaces.Viewable;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 *
 * @author Obsidiam
 */
public class SettingsForm extends Application implements Viewable{
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
                viewAlert("Settings","Closing settings window.","Settings were changed, but hasn't been saved!",AlertType.INFORMATION);
            }else{
                stage.close();
            }
        });
        
        stage.setTitle("Amelia - Settings");
        stage.setScene(scene);
        stage.show();
        
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
