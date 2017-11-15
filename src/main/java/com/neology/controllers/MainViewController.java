package com.neology.controllers;

import com.neology.RestClient;
import com.neology.controllers.alert.AlertController;
import com.neology.controllers.alert.AlertMethod;
import com.neology.controllers.cells.ContentAdapter;
import com.neology.controllers.cells.DefaultInfoViewListCell;
import com.neology.controllers.cells.PlainTextAdapter;
import com.neology.controllers.cells.TodoAdapter;
import com.neology.controllers.cells.TodoListCell;
import com.neology.environment.LocalEnvironment;
import com.neology.data.ConnectionDataHandler;
import com.neology.data.ImageDataHandler;
import com.neology.data.model.Session;
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
import io.reactivex.schedulers.Schedulers;
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
import java.util.List;
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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import io.reactivex.disposables.CompositeDisposable;
import javafx.scene.control.ProgressIndicator;


public class MainViewController implements Initializable{
    @FXML
    private TextField LOGIN, LOGIN_REG, EMAIL, NAME, STATE;
    @FXML
    private PasswordField PASS,PASS_REG;
    @FXML
    private Button LOGIN_BUTTON,SETTINGS,ABOUT,CONNECT,DISCONNECT,REGISTER_BUTTON,DESKTOP_BUTTON,ACTION_TODO_BUTTON,COLOR_PICKER_BUTTON;
    @FXML
    private volatile ListView VIEWER_PANEL,INFO_VIEW,TODO_VIEW;
    @FXML
    private Label TIME_STARTED_LABEL,TIME_STOPPED_LABEL, MENU_LABEL, USERNAME_LOGIN, USER_IMG,GM_LABEL,LOGIN_TYPE_LABEL,LD_LABEL;
    @FXML
    private Pane DRAWER,MAIN_BAR;  
    @FXML
    private AnchorPane MAIN_PANE;
    @FXML
    private ToggleButton TODO_BUTTON;       
    @FXML
    private MenuItem ADD_ITEM,REMOVE_ITEM,UPDATE_ITEM;
    @FXML
    private VBox ADD_PANEL;        
    @FXML
    private ComboBox IMPORTANCE;        
    @FXML
    private ProgressIndicator progressIndicator;        
    
    ColorPicker clp = new ColorPicker();
    String ACTUAL_NAME = "",ADDR,loginType; 
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    
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
    private static Session s;
    private RestClient rest = new RestClient();
    private LoginController lc;
    
    {
        System.out.println("Does working dir exist: "+ConfigController.checkFolders());
        if(ConfigController.checkIfInitExists()){
            try {
                setUpConfiguration();
            } catch (SocketException ex) {
                Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException | ParserConfigurationException | ClassNotFoundException | SQLException | IOException ex) {
                Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGIN_BUTTON.setOnAction(event ->{
            if(!IS_LOGGED_IN){
                if(loginType != null && LOGIN.getText() != null){
                    progressIndicator.setVisible(true);
                    
                    if(loginType.equals("LD")){
                        lc = new LoginController(rest);
                        try {
                            if(lc.loginWithLD(LOGIN.getText(), PASS.getText())){
                                s = lc.getSession();
                                viewConfirmationDialog(LOGIN.getText());
                                setProfileImage();
                                LOGIN_BUTTON.setText("Log out");
                                TODO_BUTTON.setDisable(false);
                                progressIndicator.setVisible(false);
                            }else{
                                ac.prepareViewable(new Object[]{"No user like "+LOGIN.getText()});
                                ac.viewAlert(AlertMethod.ERROR);
                            }
                        } catch (ClassNotFoundException | IOException ex) {
                            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    if(loginType.equals("GM")){
                        GoogleService g = new GoogleService();
                        g.setNickName(LOGIN.getText());
                        g.setOnSucceeded( listener ->{
                            viewConfirmationDialog(LOGIN.getText());
                            setProfileImage();
                            progressIndicator.setVisible(false);
                        });
                        lc = new LoginController(g);
                        lc.loginWithGoogleAccount();
                        LOGIN_BUTTON.setText("Log out");
                    }
                }
            }else{
                LOGGED_IN = null;
                transitToDisconnectedMode();
                IS_LOGGED_IN = false;
                CONNECT.setDisable(true);
                DISCONNECT.setDisable(true);
                SETTINGS.setDisable(true);
                LOGIN_BUTTON.setText("Log in");
                USER_IMG.setGraphic(null);
                USERNAME_LOGIN.setText(null);
                TODO_BUTTON.setDisable(true);
                LOGIN.setDisable(true);
                PASS.setDisable(true);
            }
        });

        REGISTER_BUTTON.setOnAction(event ->{
            if(LOGIN_REG.getText() != null && PASS_REG.getText() != null && EMAIL.getText() != null){
                if(LoginController.validLoginDataFormat(PASS_REG.getText(), EMAIL.getText(), PASS_REG.getText())){
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
                        Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        SETTINGS.setOnAction(event ->{
            SettingsForm settings = new SettingsForm();
            try {
                settings.start(new Stage());
            } catch (Exception ex) {
                Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
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
        
        DESKTOP_BUTTON.setOnAction(listener ->{
            UDPConnector udp = new UDPConnector("192.168.0.13",7998);
            try {
                udp.prepareConnection();
                udp.startThread();
                
                viewRemoteDesktopView(udp);
            } catch (SocketException ex) {
                Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
  
        TODO_BUTTON.setOnAction(event ->{
            if(TODO_BUTTON.isSelected()){
                if(loginType != null){
                    if(loginType.equals("LD")){
                        rest.init();
                            try {
                                compositeDisposable.add(
                                    rest.getTodoTickets(s.getToken()).observeOn(Schedulers.single()).subscribeOn(Schedulers.io()).subscribe(items ->{
                                        setTodoData(items.getTickets());
                                    })
                                );
                            } catch (IOException ex) {
                                Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                    }else{
                        ac.prepareViewable(new Object[]{"You have logged in using "+loginType+" login type"});
                        ac.viewAlert(AlertMethod.ERROR);
                    }
                }
            }else{
                compositeDisposable.clear();
                TODO_VIEW.getItems().clear();
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

        ADD_ITEM.setOnAction( listener ->{
            animateActionPanelMove();
        });

        COLOR_PICKER_BUTTON.setOnAction( event ->{
            clp.show();
        });

        ACTION_TODO_BUTTON.setOnAction( event ->{
            TodoTicket t = new TodoTicket();
            t.setPriority("Important", "#0099FF");
            t.setName("Test");
            t.setState("Done");
            boolean result = rest.postTodo(lc.getSession().getToken().getBytes(),t);
            
            if(result){
                ac.prepareViewable(new Object[]{"Add todo","Add todo ticket","Adding successful!",AlertType.INFORMATION});
                ac.viewAlert(AlertMethod.INFO);
            }else{
                ac.prepareViewable(new Object[]{"Adding todo failed."});
                ac.viewAlert(AlertMethod.ERROR);
            }
        });
        
        MENU_LABEL.setGraphic(new ImageView(this.getClass().getResource("/images/menu.png").toString()));
        VIEWER_PANEL.setCellFactory(new CallbackImpl());
        INFO_VIEW.setCellFactory(new ViewCallbackImpl(new PlainTextAdapter()));
        TODO_VIEW.setCellFactory(new ViewCallbackImpl(new TodoAdapter()));
        
    }  
    
    private void setUpConfiguration() throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException, SQLException{
            XMLController xml = new XMLController();
            INIT = xml.parseInitFile();
            PORT = Integer.parseInt(INIT.get(0));
            ADDR = INIT.get(1);
    }

    private void initSession() {
        mgr.setAccessorInstance(new AccessorImpl(v));
        c.start();
        tcp.start();
        v.start();
        mgr.start();
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
    
    private void transitToDisconnectedMode() {
        if(c.isRunning()){
            IDH.getImagesMap().clear();

            c.cancel();
            tcp.interrupt();
            mgr.interruptThread();
            CDH.getData().clear();
            //CDH.clearAllConnections();
            TIME_STOPPED_LABEL.setText("Time stopped: "+new SimpleDateFormat("HH:mm:ss").format(new Date()));
            
        }
        Platform.runLater(() ->{
            VIEWER_PANEL.getItems().clear(); 
        });
        
        DISCONNECT.setDisable(true);
        CONNECT.setDisable(false);
        SELECTED = -1;
        System.gc();
    }
    
    private void setInfoViewData(String line) {
        if(line != null){
            INFO_VIEW.setItems(FXCollections.observableList(Arrays.asList(line.split(","))));
        }
    }

    private void reinitSession() {
        c.reset();
        System.out.println("ConnectionManager.State -> "+c.getState());
        c.start();
        tcp.start();
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

    private void animateActionPanelMove(){
        TranslateTransition openNav = new TranslateTransition(new Duration(350), ADD_PANEL);
        openNav.setToY(-ADD_PANEL.getHeight());
        TranslateTransition closeNav = new TranslateTransition(new Duration(350), ADD_PANEL);
        
        if(ADD_PANEL.getTranslateY() > ADD_PANEL.getHeight()){
            openNav.play();
        }else{
            closeNav.setToY((ADD_PANEL.getHeight()));
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

    private void viewConfirmationDialog(String text) {
        ac.prepareViewable(new Object[]{"Login","Logged in as "+text,10, Pos.BASELINE_RIGHT});
        ac.viewAlert(AlertMethod.NOTIFICATION);
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
        File file = new File(LocalEnvironment.getLocalVar(Local.TMP)+File.separator+ACTUAL_NAME.toLowerCase()+".png");
        Image i;
        if(!file.exists()){
           i = new Image(this.getClass().getResource("/images/user.png").toString(), 32,32, true,true);
        }else{
           i = new Image("file:///"+file.getAbsolutePath(), 48,48, true,true);
        }
        ImageView userProfile = new ImageView(i);
        
        USER_IMG.setGraphic(userProfile);
    }


    private void setTodoData(List<TodoTicket> todoTickets) {

        Platform.runLater(() ->{
            TODO_VIEW.setItems(FXCollections.observableArrayList(todoTickets));
        });
    }
    
    private class CallbackImpl implements Callback<ListView<String>, ListCell<String>> {
        @Override
        public ListCell<String> call(ListView<String> list) {
            return new MainViewListCell();
        }
    }
    
    private class ViewCallbackImpl implements Callback<ListView<String>, ListCell<String>> {
        private ContentAdapter c;
        
        ViewCallbackImpl(ContentAdapter c){
            this.c = c;
        }
        
        
        @Override
        public ListCell<String> call(ListView<String> list) {
            ListCell<String> cell;
            if(c instanceof PlainTextAdapter){
                cell = new DefaultInfoViewListCell<>();
                ((DefaultInfoViewListCell)cell).setContentAdapter(c);
            }else{
                cell = new TodoListCell((TodoAdapter)c);
            }
            return cell;
        }

    }
    
    class MainViewListCell<T> extends ListCell<T> {
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
                        Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
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
