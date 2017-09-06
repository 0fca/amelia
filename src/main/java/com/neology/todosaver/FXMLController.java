package com.neology.todosaver;

import abstracts.LocalEnvironment;
import com.neology.data.ConnectionDataHandler;
import com.neology.data.ImageDataHandler;
import com.neology.interfaces.Viewable;
import com.neology.net.ConnectionManager;
import com.neology.net.NetController;
import com.neology.xml.XMLController;
import enums.Local;
import javafx.scene.image.Image;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class FXMLController extends LocalEnvironment implements Initializable,Viewable {
    @FXML
    private TextField LOGIN;
    @FXML
    private PasswordField PASS;
    @FXML
    private Button LOGIN_BUTTON,SETTINGS,ABOUT,CONNECT,DISCONNECT,LOG_OUT_BUTTON;
    @FXML
    private volatile ListView VIEWER_PANEL,INFO_VIEW;
    @FXML
    private Label TIME_STARTED_LABEL,TIME_STOPPED_LABEL,TIME_PASSAGE_LABEL;
    String ACTUAL_NAME;
    
    
    NetController n = null;
    volatile int PORT;
    protected ArrayList<String> INIT;
    protected boolean IS_LOGGED_IN = false;
    protected String LOGGED_IN = "";
    protected boolean IS_CONNECTED = false;
    final Executor EXEC = Executors.newSingleThreadExecutor();            
    ObservableList<Label> list = FXCollections.observableArrayList();
    private ConnectionManager c = new ConnectionManager();
    private  ViewUpdater v = new ViewUpdater();
    private ImageDataHandler IDH = ImageDataHandler.getInstance();
    private ConnectionDataHandler CDH = ConnectionDataHandler.getInstance();
    private int SELECTED = -1;
    
    
    {
        if(checkIfInitExists()){
            try {
                System.out.println("CHCK_DIRS: "+checkFolders());
                setUpConfiguration();
            } catch (SocketException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException | ParserConfigurationException | ClassNotFoundException | SQLException | IOException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
       
        
        LOGIN_BUTTON.setOnAction(event ->{
            try {
                loginUser();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        LOG_OUT_BUTTON.setOnAction(event ->{
            LOGGED_IN = null;
            DISCONNECT.fire();
            IS_LOGGED_IN = false;
            CONNECT.setDisable(true);
            DISCONNECT.setDisable(true);
            SETTINGS.setDisable(true);
            
        });
        
        SETTINGS.setOnAction(event ->{
            SettingsForm settings = new SettingsForm();
            try {
                settings.start(new Stage());
            } catch (Exception ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        CONNECT.setOnAction(event ->{
            System.out.println(c.getState().toString());
            if(c.getState() == Service.State.CANCELLED || c.getState() == Service.State.SUCCEEDED){
                
                Platform.runLater(() ->{
                   c.restart();
                });
            }
            if(c.getState() == Service.State.READY){
                System.out.println("ConnectionManager -> attempting to init.");
                initConnectionManager();
            }
            TIME_STARTED_LABEL.setText("Time started: "+new SimpleDateFormat("HH:mm:ss").format(new Date()));
            CONNECT.setDisable(true);
            DISCONNECT.setDisable(false);
        });
        
        DISCONNECT.setOnAction(event ->{
            int selected_index = VIEWER_PANEL.getSelectionModel().getSelectedIndex();
                IDH.getImagesMap().clear();
                c.cancel();
                CDH.getData().clear();
                  
                Platform.runLater(() ->{
                    VIEWER_PANEL.getItems().clear(); 
                    INFO_VIEW.getItems().clear();
                });
                System.gc();
                TIME_STOPPED_LABEL.setText("Time stopped: "+new SimpleDateFormat("HH:mm:ss").format(new Date()));
                DISCONNECT.setDisable(true);
                CONNECT.setDisable(false);
        });
        VIEWER_PANEL.setCellFactory(new CallbackImpl());
    }  
    
    private boolean checkIfInitExists(){
        return new File("init.xml").exists();
    }
    
    private void setUpConfiguration() throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException, SQLException{
            XMLController xml = new XMLController();
            INIT = xml.parseInitFile();
            PORT = Integer.parseInt(INIT.get(0));
    }

    private void loginUser() throws ClassNotFoundException {
        if(LOGIN.getText() != null&PASS.getText() != null){
            if(LOGIN.getText().equals("root")&PASS.getText().equals("q@wertyuiop")){//read from JSON(?)
                viewAlert("Login","Logging in","Logged in as root.",AlertType.INFORMATION);
                IS_LOGGED_IN = true;
                CONNECT.setDisable(false);
                SETTINGS.setDisable(false);

            }else{
                if(!LOGGED_IN.isEmpty()){
                    viewAlert("Login","Logging in","Logged in as "+LOGGED_IN,AlertType.INFORMATION);
                    CONNECT.setDisable(false);
                    DISCONNECT.setDisable(false);
                    SETTINGS.setDisable(false);
                }else{
                    viewError("Error. This user hasn't been registered yet.");
                }
            }
            LOGIN.setText(null);
            PASS.setText(null);
        }
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
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText("An error occured.");
        a.setContentText(text);
        a.showAndWait();
    } 

    private boolean checkFolders() {
       String tmp = getLocalVar(Local.TMP);
       return new File(tmp).mkdir();  
    }

    private void initConnectionManager() {
        c.start();
        v.start();
    }
    
    
    private class CallbackImpl implements Callback<ListView<String>, ListCell<String>> {
        @Override
        public ListCell<String> call(ListView<String> list) {
            return new DefaultListCell();
        }
    }
    
    public class DefaultListCell<T> extends ListCell<T> {
        @Override 
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
                if (empty) {
                    setText(null);

                    setGraphic(null);

                } else {
                    setText(new File(item.toString()).getName().split("\\.")[0]);

                    Image out = IDH.getImagesMap().get(new File(item.toString()).getName().split("\\.")[0]);

                    if(out != null){
                        setGraphic(new ImageView(out));
                        this.setOnMouseClicked(evt ->{
                           int index = VIEWER_PANEL.getSelectionModel().getSelectedIndex();
                           String item2 = item.toString();
                           
                           HashMap<String,String> data = CDH.getData();
                           String line;
                           if(System.getProperty("os.name").contains("Windows")){
                                line = data.get(item2.split(":")[3]);
                           }else{
                                line = data.get(item2.split(":")[2]);
                           }
                           if(line != null){
                               INFO_VIEW.getItems().clear();
                               INFO_VIEW.getItems().addAll(Arrays.asList(line.split(",")));
                               SELECTED = index;
                           }
                        });
                        setPrefHeight(95);
                        backgroundProperty().bind(Bindings.when(this.hoverProperty())
                        .then(new Background(new BackgroundFill(Color.valueOf("#9E9E9E"), CornerRadii.EMPTY, Insets.EMPTY)))
                        .otherwise(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))));
                        setContentDisplay(ContentDisplay.TOP);
                    }
                }
        }
    }
    
    public class ViewUpdater extends Thread implements Runnable{
        private Thread T = null;

        @Override
        public void start(){
            if(T == null){
                T = new Thread(this,"ViewUpdater");
                T.start();
            }
        }
        
        @Override
        public void run(){
            while(!T.isInterrupted()){
                    HashMap<String, Image> map = IDH.getImagesMap();
                    HashMap<String,String> data = CDH.getData();
                    
                    if(map.size() > 0){
                        Platform.runLater(() ->{
                            if(c.getState() == javafx.concurrent.ScheduledService.State.RUNNING){
                                
                                if(VIEWER_PANEL.getItems().size() > 0){
                                    VIEWER_PANEL.getItems().clear();
                                }

                                map.forEach((x,y) ->{
                                    VIEWER_PANEL.getItems().add("file://"+getLocalVar(Local.TMP)+File.separator+x+".jpg:"+x);
                                });

                                data.forEach((x,y) ->{
                                    if(SELECTED > -1 && VIEWER_PANEL.getItems().size() > SELECTED){
                                        Object item = VIEWER_PANEL.getItems().get(SELECTED);
                                        //System.out.println(item);
                                        if(item != null){
                                            if(item.toString().contains(x)){
                                                String line;
                                                if(System.getProperty("os.name").contains("Windows")){
                                                   line = item.toString().split(":")[3];
                                                }else{
                                                   line = item.toString().split(":")[2];
                                                }
                                                if(line.equals(x)){
                                                    System.out.println("Updating info view");
                                                    INFO_VIEW.getItems().clear();
                                                    INFO_VIEW.getItems().addAll(Arrays.asList(y.split(",")));
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });
                   }
                    
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
