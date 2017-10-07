/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.controllers;

import com.neology.controllers.alert.AlertController;
import com.neology.controllers.alert.AlertMethod;
import com.neology.environment.LocalEnvironment;
import com.neology.environment.Local;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javax.xml.stream.XMLStreamException;
/**
 * FXML Controller class
 *
 * @author Obsidiam
 */
public class SettingsFormsController extends LocalEnvironment implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Button DATA_BUTTON,DO_TRANSMISSION_BUTTON;
    @FXML
    private Label BACKUP_LABEL;
    @FXML
    private TabPane TAB_PANE;
    @FXML
    private ComboBox OPTION_CHOOSER;
    @FXML
    private TextField VALUE_FIELD;
    
    
    private boolean CHANGED = false;
    private boolean SAVED = false;
    private int PORT = 7999;
    //private NetController n = new NetController(s);
    
    protected HashMap<String,String> SETTINGS = new HashMap<>();
    AlertController ac = new AlertController();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
      
        BACKUP_LABEL.setTooltip(new Tooltip("Backup settings"));
        
        BACKUP_LABEL.setOnMouseClicked( event ->{
                    
           
        });
       
        
        DO_TRANSMISSION_BUTTON.setOnAction( action ->{
            SAVED = true;
          
            try {
                saveSettings();
            } catch (NullPointerException | UnknownHostException ex) {
                Logger.getLogger(SettingsFormsController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | XMLStreamException ex) {
                Logger.getLogger(SettingsFormsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        OPTION_CHOOSER.setOnAction(evt ->{
            if(SETTINGS.size() > 0){
                String s = SETTINGS.get(OPTION_CHOOSER.getSelectionModel().getSelectedItem().toString());
                if(s != null){
                    VALUE_FIELD.setText(s);
                }else{
                    VALUE_FIELD.setText("No value set.");
                }
            }
        });
        
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("IP");
        items.add("Port");
        items.add("Buffer size");
        OPTION_CHOOSER.getItems().addAll(items);
        
        VALUE_FIELD.setOnKeyPressed(evt ->{
            if(evt.getCode() == KeyCode.ENTER){
                String item = OPTION_CHOOSER.getSelectionModel().getSelectedItem().toString();
                SETTINGS.put(item, VALUE_FIELD.getText());
                setToChanged();
            }
        });
    }    

    private void setToChanged() {
        CHANGED = true;
    }

    private void saveSettings() throws NullPointerException, UnknownHostException, IOException, XMLStreamException {
        TAB_PANE.getTabs().forEach(action ->{
            if(action.isSelected()){
                switch(action.getText()){
                    case "Connection settings":
                    {
                        Properties p = new Properties();
                        try {
                            OutputStream o = new FileOutputStream(new File(getLocalVar(Local.TMP)));
                            SETTINGS.forEach((x,y) ->{
                                p.setProperty(x, y);
                            });
                            p.storeToXML(o, new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(SettingsFormsController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(SettingsFormsController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                        break;
                    case "Users settings":
                        break;
                    case "Data":
                        break;
                }
            }
        });
        SAVED = true;
    }
    
    private class Listener implements ChangeListener{
        private String VALUE = "";
        private String NAME = "";
        
        private Listener(String name,String value){
            this.VALUE = value;
            this.NAME = name;
        }
        
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            if(newValue != null&!newValue.toString().isEmpty()){
                SETTINGS.put(NAME,VALUE);
            }
        }
        
    }
    
}
