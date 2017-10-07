package com.neology.controllers;

import com.neology.Hasher;
import com.neology.RestClient;
import com.neology.controllers.alert.AlertController;
import com.neology.controllers.alert.AlertMethod;
import com.neology.environment.LocalEnvironment;
import com.neology.data.ConnectionDataHandler;
import com.neology.data.ImageDataHandler;
import com.neology.data.Session;
import com.neology.data.UDPConnectorResources;
import com.neology.net.ConnectionManager;
import com.neology.net.ConnectionReceiver;
import com.neology.net.TCPThread;
import com.neology.parsing.XMLController;
import com.neology.environment.Local;
import com.neology.google.GoogleService;
import com.neology.lastdays.TodoTicket;
import com.neology.main.SettingsForm;
import com.neology.net.UDPConnector;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class FXMLController extends LocalEnvironment implements Initializable{
    @FXML
    private TextField LOGIN,LOGIN_REG,EMAIL;
    @FXML
    private PasswordField PASS,PASS_REG;
    @FXML
    private Button LOGIN_BUTTON,SETTINGS,ABOUT,CONNECT,DISCONNECT,REGISTER_BUTTON,DESKTOP_BUTTON;
    @FXML
    private volatile ListView VIEWER_PANEL,INFO_VIEW;
    @FXML
    private Label TIME_STARTED_LABEL,TIME_STOPPED_LABEL, MENU_LABEL, USERNAME_LOGIN, USER_IMG,GM_LABEL,LOGIN_TYPE_LABEL,LD_LABEL;
    @FXML
    private Pane DRAWER,MAIN_BAR;  
    @FXML
    private AnchorPane MAIN_PANE;
    @FXML
    private ToggleButton TODO_BUTTON;
    
    String ACTUAL_NAME = "",ADDR,loginType;
    
    
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
    private LocalEnvironment env = new LocalEnvironment() {};
    private static Session s;
    
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
            if(!IS_LOGGED_IN){
                if(loginType != null && LOGIN.getText() != null){
                    if(loginType.equals("LD")){
                        try {
                            loginWithLD();
                        } catch (ClassNotFoundException | IOException ex) {
                            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    if(loginType.equals("GM")){
                        loginWithGoogleAccount(LOGIN.getText());
                    }
                }
                LOGIN_BUTTON.setText("Log out");
            }else{
                LOGGED_IN = null;
                transitToDisconnectedMode();
                IS_LOGGED_IN = false;
                CONNECT.setDisable(true);
                DISCONNECT.setDisable(true);
                SETTINGS.setDisable(true);
                LOGIN_BUTTON.setText("Log in");
            }
        });

        REGISTER_BUTTON.setOnAction(event ->{
            if(LOGIN_REG.getText() != null && PASS_REG.getText() != null && EMAIL.getText() != null){
                if(validLoginDataFormat(PASS_REG.getText(), EMAIL.getText(), PASS_REG.getText())){
                    RestClient rest = new RestClient();
                    rest.init();

                    try {
                        if(rest.registerUser(LOGIN_REG.getText(), PASS_REG.getText(),EMAIL.getText())){
                            ac.prepareViewable(new Object[]{"Register","Registering Last Days' account","Registering to Last Days successful!",AlertType.INFORMATION});
                            ac.viewAlert(AlertMethod.INFO);
                            PASS_REG.setText(null);
                            LOGIN_REG.setText(null);
                            EMAIL.setText(null);
                        }else{
                            ac.prepareViewable(new Object[]{"Error. Couldn't register account "+LOGIN_REG.getText()});
                            ac.viewAlert(AlertMethod.ERROR);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
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
            if(SELECTED > -1){
                DESKTOP_BUTTON.setDisable(false);
            }
        });
        
        MAIN_PANE.setOnMouseClicked( listener ->{
            animateDrawerMove();
        });
        
        DESKTOP_BUTTON.setOnAction( listener ->{
            UDPConnector udp = new UDPConnector("192.168.0.108",7998);
            try {
                udp.prepareConnection();
                udp.startThread();
                
                viewRemoteDesktopView(udp);
            } catch (SocketException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
  
        TODO_BUTTON.setOnAction(event ->{
            if(TODO_BUTTON.isSelected()){
                if(loginType.equals("LD")){
                    RestClient rest = new RestClient();
                    rest.init();
                    try {
                        setTodoData(rest.getTodoTickets(s.getToken()));
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    ac.prepareViewable(new Object[]{"Error","Login type mismatch","You have logged in using "+loginType+" login type."});
                    ac.viewAlert(AlertMethod.ERROR);
                }
            }else{
                INFO_VIEW.getItems().clear();
            }
        });

        Image gac_img = new Image(SettingsFormsController.class.getResourceAsStream("/images/gac.png"),32,32,true,true);
        GM_LABEL.setGraphic(new ImageView(gac_img));
        GM_LABEL.setOnMouseClicked(clicked ->{
            loginType = "GM";
            LOGIN_TYPE_LABEL.setText("Log in with Google account.");
            LOGIN.setDisable(false);
        });
        
        LD_LABEL.setOnMouseClicked( listener->{
            loginType = "LD";
            LOGIN_TYPE_LABEL.setText("Log in with Last Days account");
            PASS.setDisable(false);
            LOGIN.setDisable(false);
        });

        MENU_LABEL.setGraphic(new ImageView(this.getClass().getResource("/images/menu.png").toString()));
        VIEWER_PANEL.setCellFactory(new CallbackImpl());
        INFO_VIEW.setCellFactory(new InfoViewCallbackImpl());
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

    private void loginWithLD() throws ClassNotFoundException, IOException {
        if(LOGIN.getText() != null && PASS.getText() != null){
            if(LOGIN.getText().equals("root")&& Hasher.sha(PASS.getText()).equals(">:��ܰb-���ᦦ�sض5�Z��kxK")){
                viewConfirmationDialog(LOGIN.getText());
                setProfileImage();
            }else{
                RestClient rest = new RestClient();
                rest.init();
                
                if(rest.loginUser(LOGIN.getText(), PASS.getText(), 10)){
                    s = rest.getSession();
                    viewConfirmationDialog("Logged in as "+LOGIN.getText());
                    setProfileImage();
                    TODO_BUTTON.setDisable(false);
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
        CDH.clearAllConnections();
        
        Platform.runLater(() ->{
            VIEWER_PANEL.getItems().clear(); 
            INFO_VIEW.getItems().clear();
        });
        
        
        TIME_STOPPED_LABEL.setText("Time stopped: "+new SimpleDateFormat("HH:mm:ss").format(new Date()));
        DISCONNECT.setDisable(true);
        CONNECT.setDisable(false);
        SELECTED = -1;
        System.gc();
    }

    private void transitToConnectedMode() {
        System.out.println("ConnectionManager.State -> "+c.getState().toString());
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

    private void viewRemoteDesktopView(UDPConnector udp) throws IOException {
        UDPConnectorResources r  = new UDPConnectorResources();
        r.putObject("UDPConnector", udp);
        
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/RemoteDesktopScene.fxml"),r);
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/remotedesktopscene.css");
        stage.setOnCloseRequest(event ->{
            
        });
        
        //stage.setTitle("Remote Desktop - "+VIEWER_PANEL.getSelectionModel().getSelectedItem().toString().split("-")[1]);
        stage.setScene(scene);
        stage.show();
    }

    private void loginWithGoogleAccount(String nick) {
        GoogleService g = new GoogleService();
        g.setNickName(nick);
        g.start();
        g.setOnSucceeded( listener ->{
            viewConfirmationDialog(g.getValue().toString());
            setProfileImage();
        });
    }

    private void viewConfirmationDialog(String text) {
        ac.prepareViewable(new Object[]{"Login","Logging in","Logged in as "+text, AlertType.INFORMATION});
        ac.viewAlert(AlertMethod.INFO);
        IS_LOGGED_IN = true;
        CONNECT.setDisable(false);
        SETTINGS.setDisable(false);
        LOGIN.setText(null);
        PASS.setText(null);
        USERNAME_LOGIN.setText(text);
        ACTUAL_NAME = text; 
        animateDrawerMove();
    }

    private void setProfileImage() {
        System.out.println(ACTUAL_NAME.toLowerCase());
        File file = new File(env.getLocalVar(Local.TMP)+File.separator+ACTUAL_NAME.toLowerCase()+".png");
        Image i = null;
        if(!file.exists()){
           i = new Image(this.getClass().getResource("/images/user.png").toString(), 32,32, true,true);
        }else{
           i = new Image("file:///"+file.getAbsolutePath(), 48,48, true,true);
        }
        System.out.println("file:///"+env.getLocalVar(Local.TMP)+File.separator+ACTUAL_NAME.toLowerCase()+".png");
        ImageView userProfile = new ImageView(i);
        Circle s = new Circle();
        
        s.setRadius(50d);
        s.setCenterX(USER_IMG.getWidth() / 2);
        USER_IMG.setShape(s);
        
        USER_IMG.setGraphic(userProfile);
    }

    private boolean validLoginDataFormat(String password, String email, String login) {
        return password.matches("[a-zA-Z_0-9]{3,30}+") && (password.length() >= 3 && password.length() <= 30) && login.length() >= 3 && login.length() <= 30 && email.contains("@");
    }

    private void setTodoData(ArrayList<TodoTicket> todoTickets) {
        INFO_VIEW.getItems().clear();
        INFO_VIEW.getItems().setAll(todoTickets);
    }
    
    
    private class CallbackImpl implements Callback<ListView<String>, ListCell<String>> {
        @Override
        public ListCell<String> call(ListView<String> list) {
            return new DefaultListCell();
        }
    }
    
    private class InfoViewCallbackImpl implements Callback<ListView<String>, ListCell<String>> {
        @Override
        public ListCell<String> call(ListView<String> list) {
            return new DefaultInfoViewListCell();
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
                    Image out = IDH.getImagesMap().get(item.toString());

                    if(out != null){
                        im.setImage(out);
                        im.setSmooth(true);
                        im.setPreserveRatio(true);
                        setGraphic(im);
                        this.setOnMouseClicked(evt ->{
                            if(!TODO_BUTTON.isSelected()){
                               SELECTED = VIEWER_PANEL.getSelectionModel().getSelectedIndex();
                               setInfoViewData(CDH.getData().get(item.toString().split(":")[0]));
                            }
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
    
    public class DefaultInfoViewListCell<T> extends ListCell<T> {
        ImageView im = new ImageView();
        @Override 
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if(item instanceof TodoTicket){
                    TodoTicket ticket = (TodoTicket)item;
                    setText(ticket.getName()+"\n"+ticket.getState()+"\n"+ticket.getPriority().getName());
                    this.setBackground(new Background(new BackgroundFill(Color.valueOf("#FFFFFF"), new CornerRadii(2d), Insets.EMPTY)));
                }else{
                    setText(item.toString().replace(",","\n"));
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
                if(!TODO_BUTTON.isSelected()){
                    HashMap<String, Image> map = IDH.getImagesMap();
                    HashMap<String,String> data = CDH.getData();
                    
                    Platform.runLater(() ->{
                        VIEWER_PANEL.getItems().clear();

                        if(map.size() > 0){
                            if(c.getState() == javafx.concurrent.ScheduledService.State.RUNNING){
                                map.forEach((x,y) ->{
                                    VIEWER_PANEL.getItems().add(x);
                                });

                                if(SELECTED > -1 && VIEWER_PANEL.getItems().size() > SELECTED){
                                    data.forEach((x,y) ->{
                                            VIEWER_PANEL.getSelectionModel().select(SELECTED);
                                            Object item = VIEWER_PANEL.getItems().get(SELECTED);
                                            if(item != null){
                                                
                                                if(CDH.isConnectionRegistered(item.toString())){
                                                    setInfoViewData(CDH.getData().get(item.toString()));
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
