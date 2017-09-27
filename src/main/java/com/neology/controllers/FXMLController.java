package com.neology.controllers;

import com.neology.controllers.alert.AlertController;
import com.neology.controllers.alert.AlertMethod;
import com.neology.environment.LocalEnvironment;
import com.neology.data.ConnectionDataHandler;
import com.neology.data.ImageDataHandler;
import com.neology.net.ConnectionManager;
import com.neology.net.ConnectionReceiver;
import com.neology.net.TCPThread;
import com.neology.parsing.XMLController;
import com.neology.environment.Local;
import com.neology.main.SettingsForm;
import javafx.scene.image.Image;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class FXMLController extends LocalEnvironment implements Initializable{
    @FXML
    private TextField LOGIN;
    @FXML
    private PasswordField PASS;
    @FXML
    private Button LOGIN_BUTTON,SETTINGS,ABOUT,CONNECT,DISCONNECT,LOG_OUT_BUTTON;
    @FXML
    private volatile ListView VIEWER_PANEL,INFO_VIEW;
    @FXML
    private Label TIME_STARTED_LABEL,TIME_STOPPED_LABEL, MENU_LABEL, USERNAME_LOGIN;
    @FXML
    private Pane DRAWER,MAIN_BAR;
    
    String ACTUAL_NAME,ADDR;
    
    
    volatile int PORT;
    protected ArrayList<String> INIT;
    protected boolean IS_LOGGED_IN = false;
    protected String LOGGED_IN = "";
    protected boolean IS_CONNECTED = false;         
    private ConnectionReceiver c = new ConnectionReceiver();
    private ConnectionManager mgr = new ConnectionManager();
    private  ViewUpdater v = new ViewUpdater();
    private ImageDataHandler IDH = ImageDataHandler.getInstance();
    private ConnectionDataHandler CDH = ConnectionDataHandler.getInstance();
    private int SELECTED = -1;
    private TCPThread tcp = new TCPThread();
    private AlertController ac = new AlertController();
    
    {
        System.out.println("Does working dir exist: "+checkFolders());
        if(checkIfInitExists()){
            try {
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
            transitToDisconnectedMode();
            IS_LOGGED_IN = false;
            CONNECT.setDisable(true);
            DISCONNECT.setDisable(true);
            SETTINGS.setDisable(true);
            LOGIN_BUTTON.setDisable(false);
            LOG_OUT_BUTTON.setDisable(true);
        });
        
        SETTINGS.setOnAction(event ->{
            SettingsForm settings = new SettingsForm();
            try {
                settings.start(new Stage());
            } catch (Exception ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        MENU_LABEL.setOnMouseClicked( listener ->{
            animateDrawerMove();
        });
        
        CONNECT.setOnAction(event ->{
            transitToConnectedMode();
        });
        
        DISCONNECT.setOnAction(event ->{
            transitToDisconnectedMode();
        });
        
        ABOUT.setOnAction(listener ->{
            AboutFormController about;
            try {
                about = new AboutFormController(FXMLLoader.load(getClass().getResource("/fxml/AboutForm.fxml")));
                about.showAbout();
            } catch (IOException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
        
        VIEWER_PANEL.setOnMouseClicked(listener ->{
            
        });
        
        MAIN_BAR.setOnMouseClicked( listener ->{
            animateDrawerMove();
        });
        
        MENU_LABEL.setGraphic(new ImageView(this.getClass().getResource("/images/menu.png").toString()));
        VIEWER_PANEL.setCellFactory(new CallbackImpl());
    }  
    
    private boolean checkIfInitExists(){
        return new File("init.xml").exists();
    }
    
    private void setUpConfiguration() throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException, SQLException{
            XMLController xml = new XMLController();
            INIT = xml.parseInitFile();
            PORT = Integer.parseInt(INIT.get(0));
            ADDR = INIT.get(1);
    }

    private void loginUser() throws ClassNotFoundException {
        if(LOGIN.getText() != null&PASS.getText() != null){
            if(LOGIN.getText().equals("root")&PASS.getText().equals("q@wertyuiop")){
                ac.prepareViewable(new Object[]{"Login","Logging in","Logged in as root", AlertType.INFORMATION});
                ac.viewAlert(AlertMethod.INFO);
                IS_LOGGED_IN = true;
                CONNECT.setDisable(false);
                SETTINGS.setDisable(false);
                LOGIN.setText(null);
                PASS.setText(null);
                LOGIN_BUTTON.setDisable(true);
                LOG_OUT_BUTTON.setDisable(false);
                USERNAME_LOGIN.setText("root");
                animateDrawerMove();
            }else{
                if(!LOGGED_IN.isEmpty()){
                    ac.prepareViewable(new Object[]{"Login","Logging in","Logged in as "+LOGGED_IN,AlertType.INFORMATION, AlertType.INFORMATION});
                    ac.viewAlert(AlertMethod.INFO);
                    IS_LOGGED_IN = true;
                    CONNECT.setDisable(false);
                    SETTINGS.setDisable(false);
                    LOGIN.setText(null);
                    PASS.setText(null);
                    LOGIN_BUTTON.setDisable(true);
                    LOG_OUT_BUTTON.setDisable(false);
                    USERNAME_LOGIN.setText(LOGGED_IN);
                    animateDrawerMove();
                }else{
                    ac.prepareViewable(new Object[]{"Error. The user hasn't been registered."});
                    ac.viewAlert(AlertMethod.ERROR);
                }
            }
            
        }
    }

    private boolean checkFolders() {
       String tmp = getLocalVar(Local.TMP);
       return !(new File(tmp).mkdir());  
    }

    private void initSession() {
        mgr.setAccessorInstance(new AccessorImpl(v));
        c.start();
        tcp.start();
        v.start();
        mgr.start();
    }

    private void transitToDisconnectedMode() {
        IDH.getImagesMap().clear();

        c.cancel();
        tcp.interrupt();
        mgr.interruptThread();
        CDH.getData().clear();

        Platform.runLater(() ->{
            VIEWER_PANEL.getItems().clear(); 
            INFO_VIEW.getItems().clear();
        });
        System.gc();
        TIME_STOPPED_LABEL.setText("Time stopped: "+new SimpleDateFormat("HH:mm:ss").format(new Date()));
        DISCONNECT.setDisable(true);
        CONNECT.setDisable(false);
        SELECTED = -1;
    }

    private void transitToConnectedMode() {
        System.out.println("ConnectionManager#State -> "+c.getState().toString());
        if(c.getState() == Service.State.CANCELLED || c.getState() == Service.State.SUCCEEDED){
            Platform.runLater(() ->{
               reinitSession();
            });
        }

        if(c.getState() == Service.State.READY){
            System.out.println("FXMLController -> attempting to init ConnectionManager");
            initSession();
        }
        TIME_STARTED_LABEL.setText("Time started: "+new SimpleDateFormat("HH:mm:ss").format(new Date()));
        TIME_STOPPED_LABEL.setText("Time stopped: ");
        CONNECT.setDisable(true);
        DISCONNECT.setDisable(false);
    }
    
    private void setInfoViewData(String line) {
        if(line != null){
            INFO_VIEW.getItems().clear();
            INFO_VIEW.getItems().addAll(Arrays.asList(line.split(",")));
        }
    }

    private void reinitSession() {
        c.reset();
        System.out.println("ConnectionManager#State -> "+c.getState());
        c.start();
        tcp.start();
        mgr.setAccessorInstance(new AccessorImpl(v));
        mgr.start();
    }

    private void animateDrawerMove() {
        TranslateTransition openNav = new TranslateTransition(new Duration(350), DRAWER);
        openNav.setToX(DRAWER.getWidth());
        TranslateTransition closeNav = new TranslateTransition(new Duration(350), DRAWER);
        
        if(DRAWER.getTranslateX() < DRAWER.getWidth()){
            openNav.play();
        }else{
            closeNav.setToX(-(DRAWER.getWidth()));
            closeNav.play();
        }
    }
    
    
    private class CallbackImpl implements Callback<ListView<String>, ListCell<String>> {
        @Override
        public ListCell<String> call(ListView<String> list) {
            return new DefaultListCell();
        }
    }
    
    public class DefaultListCell<T> extends ListCell<T> {
        ImageView im = new ImageView();
        @Override 
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String fn = Paths.get(item.toString().split("-")[1]).getFileName().toString();
                    //setText(fn);
                    Image out = IDH.getImagesMap().get(fn.split("\\.")[0]);

                    if(out != null){
                        im.setImage(out);
                        im.setSmooth(true);
                        im.setPreserveRatio(true);
                        setGraphic(im);
                        this.setOnMouseClicked(evt ->{
                           SELECTED = VIEWER_PANEL.getSelectionModel().getSelectedIndex();
                           setInfoViewData(CDH.getData().get(item.toString().split(":")[0]));
                        });
                        
                        setPrefHeight(155);
                        setPrefWidth(VIEWER_PANEL.getPrefWidth() - 5);
                        backgroundProperty().bind(Bindings.when(this.selectedProperty())
                        .then(new Background(new BackgroundFill(Color.valueOf("#616161"), new CornerRadii(2d), Insets.EMPTY)))
                        .otherwise(new Background(new BackgroundFill(Color.valueOf("#222222"), new CornerRadii(2d), Insets.EMPTY))));
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    }
                }
        }
    }
    
   class ViewUpdater extends Thread implements Runnable{
        private Thread T = null;
        private boolean wasSignaled = false;
        private SignalType type;
        private String msg = null;
        
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
                    
                    Platform.runLater(() ->{
                        VIEWER_PANEL.getItems().clear();
                        //System.out.println(map.size());
                        if(map.size() > 0){
                            if(c.getState() == javafx.concurrent.ScheduledService.State.RUNNING){
                                
                                map.forEach((x,y) ->{
                                    VIEWER_PANEL.getItems().add("file://"+getLocalVar(Local.TMP)+File.separator+x+".jpg-"+x);
                                });

                                if(SELECTED > -1 && VIEWER_PANEL.getItems().size() > SELECTED){
                                    data.forEach((x,y) ->{
                                            VIEWER_PANEL.getSelectionModel().select(SELECTED);
                                            Object item = VIEWER_PANEL.getItems().get(SELECTED).toString().split("-")[1];
                                            if(item != null){
                                                if(CDH.findConnectionName(item.toString()) != null){
                                                    setInfoViewData(CDH.getData().get(CDH.findConnectionName(item.toString())));
                                                }
                                            }
                                    });
                                }
                            }
                        }
                    });
                    
                    if(wasSignaled && msg != null){
                       Platform.runLater(() -> showNotification());
                       wasSignaled = false;
                    }
                try {
                    Thread.sleep(1000);
                    System.gc();
                } catch (InterruptedException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        synchronized void signal(SignalType type){
            this.type = type;
            wasSignaled = true;
        }
        
        synchronized void commitSignalData(String msg){
            this.msg = msg;
        }
        
        synchronized void showNotification(){
            ac.prepareViewable(new Object[]{type.toString(),msg,10,Pos.BOTTOM_RIGHT});
            ac.viewAlert(AlertMethod.NOTIFICATION);
        }
    }
}
