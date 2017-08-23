package com.neology.todosaver;

import abstracts.LocalEnvironment;
import com.neology.data.ConnectionDataHandler;
import com.neology.data.ImageDataHandler;
import com.neology.interfaces.Viewable;
import com.neology.net.Connection;
import com.neology.net.ConnectionManager;
import com.neology.net.NetController;
import com.neology.net.TCPService;
import com.neology.xml.XMLController;
import enums.Local;
import javafx.scene.image.Image;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
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
    
    private ImageDataHandler IDH = ImageDataHandler.getInstance();
    private ConnectionDataHandler CDH = ConnectionDataHandler.getInstance();
    private int INDEX;
    
    {
        INDEX = IDH.getIndex();
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
            if(c.getState() == Service.State.CANCELLED){
                Platform.runLater(() ->{
                   c.restart();
                });

                if(c.getState() == Service.State.READY){
                    initConnectionManager();
                }
            }
            if(c.getState() == Service.State.READY){
                initConnectionManager();
            }
            CONNECT.setDisable(true);
            DISCONNECT.setDisable(false);
           
        });
        
        DISCONNECT.setOnAction(event ->{
            IDH.getImagesMap().clear();
            for(Service t : CDH.getThreadsMap().values()){
                t.cancel();
                t = null;
            }
            CDH.getThreadsMap().clear();
            CDH.getData().clear();
            
            Platform.runLater(() ->{
                VIEWER_PANEL.getItems().clear(); 
            });
            c.setOnCancelled(canc_evt ->{
                try {
                    INDEX = 0;
                    c.closeConnection();
                } catch (IOException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            c.cancel();
            System.gc();
            
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
            PORT = 7999;
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
        ViewUpdater v = new ViewUpdater();
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
                    System.err.println("ERROR: "+out.isError());
                    System.out.println("LDR_STATE: "+out.getProgress());
                    setGraphic(new ImageView(out));
                    this.setOnMouseClicked(evt ->{
                       int index = VIEWER_PANEL.getSelectionModel().getSelectedIndex();
                       String data = CDH.getData().get(index);

                       String[] splitted = data.split(",");
                       INFO_VIEW.getItems().clear();
                       INFO_VIEW.getItems().addAll(Arrays.asList(splitted));

                    });
                    this.setTextAlignment(TextAlignment.CENTER);
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
                    if(map.size() > 0){
                        Platform.runLater(() ->{
                            map.forEach((x,y) ->{
                                //System.out.println("file:///"+getLocalVar(Local.TMP)+File.separator+x+".jpg");
                                if(VIEWER_PANEL.getItems().isEmpty() || VIEWER_PANEL.getItems().size() == INDEX){
                                    VIEWER_PANEL.getItems().add("file://"+getLocalVar(Local.TMP)+File.separator+x+".jpg");
                                }else{
                                    VIEWER_PANEL.getItems().set(INDEX,"file://"+getLocalVar(Local.TMP)+File.separator+x+".jpg");
                                }
                            });
                        });
                   }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
