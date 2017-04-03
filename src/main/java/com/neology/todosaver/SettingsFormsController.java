/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.todosaver;

import abstracts.LocalEnvironment;
import com.neology.xml.XMLController;
import enums.Local;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

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
    private Pane NAV_BAR;
    @FXML
    private Label NAV_BAR_LABEL;
    @FXML
    private Button CLOSE_NAV_BUTTON,BROWSE_DIR_BUTTON,SAVE_BUTTON,BACKUP_BUTTON,DATA_BUTTON;
    @FXML
    private TextField XML_PATH_TXT,SUBNET_TXT;
    @FXML
    private Spinner PORT_SPINNER;
    @FXML
    private TabPane TAB_PANE;
    
    private boolean CHANGED = false;
    private boolean SAVED = false;
    
    protected HashMap<String,String> SETTINGS = new HashMap<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        NAV_BAR_LABEL.setOnMouseClicked(event ->{
            TranslateTransition openNav = new TranslateTransition(new Duration(350), NAV_BAR);
            openNav.setToY(0.0);
            TranslateTransition closeNav = new TranslateTransition(new Duration(350), NAV_BAR);
            if(NAV_BAR.getTranslateY() != 0){
                openNav.play();
            }else{
                closeNav.setToY((NAV_BAR.getHeight()));
                closeNav.play();
            }
        });
        
        BACKUP_BUTTON.setOnAction(event ->{
            
        });
        
        CLOSE_NAV_BUTTON.setOnAction(event ->{
            TranslateTransition openNav = new TranslateTransition(new Duration(350), NAV_BAR);

            openNav.setToY(-(NAV_BAR.getHeight()));
            
                TranslateTransition closeNav = new TranslateTransition(new Duration(350), NAV_BAR);
                if(NAV_BAR.getTranslateY() == 0.0){
                    openNav.play();
                }else{
                    closeNav.setToY(0.0);
                    closeNav.play();
                } 
        });
        
        SUBNET_TXT.textProperty().addListener(new Listener("subnet",SUBNET_TXT.getText()));
        XML_PATH_TXT.textProperty().addListener(new Listener("xml_path",XML_PATH_TXT.getText()));
        
        
        SAVE_BUTTON.setOnAction(event ->{
            SAVED = true;
            SettingsForm s = new SettingsForm();
            s.SAVED = true;
            
            try {
                saveSettings();
            } catch (NullPointerException | UnknownHostException ex) {
                Logger.getLogger(SettingsFormsController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | XMLStreamException ex) {
                Logger.getLogger(SettingsFormsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        BROWSE_DIR_BUTTON.setOnAction(event ->{
            setToChanged();
            DirectoryChooser dir = new DirectoryChooser();
            dir.setTitle("Choose a directory...");
            try {
                dir.setInitialDirectory(new File(getLocalVar(Local.USER_HOME)));
            } catch (NullPointerException ex) {
                Logger.getLogger(SettingsFormsController.class.getName()).log(Level.SEVERE, null, ex);
            }
            File get = dir.showDialog(null);
            
            if(get != null){
                if(get.isDirectory()){
                    XML_PATH_TXT.setText(get.getAbsolutePath());
                }
            }
        });
    }    

    @Override
    public String preparePath(String path) {
        return "";
    }

    private void setToChanged() {
        SettingsForm s = new SettingsForm();
        s.CHANGED = true;
        CHANGED = true;
    }

    private void saveSettings() throws NullPointerException, UnknownHostException, IOException, XMLStreamException {
        String local_xml = getLocalVar(Local.XML_PATH);
    
        XMLController xml_controller = new XMLController();
        
        TAB_PANE.getTabs().forEach(action ->{
            if(action.isSelected()){
                switch(action.getText()){
                    case "Connection settings":
                    {
                        try {
                            xml_controller.createInitXML(XML_PATH_TXT.getText(), SUBNET_TXT.getText(), "17");
                        } catch (SAXException | ParserConfigurationException | IOException | XMLStreamException ex) {
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
