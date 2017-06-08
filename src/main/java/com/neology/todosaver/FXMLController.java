package com.neology.todosaver;

import abstracts.LocalEnvironment;
import com.neology.exceptions.TransportException;
import com.neology.interfaces.Viewable;
import com.neology.net.BaudrateMeter;
import com.neology.net.Connection;
import com.neology.net.Established;
import com.neology.net.NetController;
import com.neology.net.Opened;
import com.neology.net.Transport;
import com.neology.xml.XMLController;
import java.awt.image.BufferedImage;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class FXMLController extends LocalEnvironment implements Initializable,Viewable {
    
    @FXML
    private Label VER;
    @FXML
    private ListView MAIN_VIEW;
    @FXML
    private TextArea NOTE_VIEW;
    @FXML
    private TextField LOGIN;
    @FXML
    private PasswordField PASS;
    @FXML
    private Button LOGIN_BUTTON,SETTINGS,ABOUT,CONNECT,DISCONNECT,LOG_OUT_BUTTON,REFRESH;
    @FXML
    private GridPane VIEWER_PANEL;
    String ACTUAL_NAME;
    
    protected ArrayList<String> INIT;
    protected ArrayList<String> POOL = new ArrayList<>();
    protected boolean IS_LOGGED_IN = false;
    protected String LOGGED_IN = "";
    protected boolean IS_CONNECTED = false;
    //final Connector connector = new Connector();
    //private ArrayList<Image> IMG_BUFFER = new ArrayList<>();
    static ConcurrentHashMap<Connection,BaudrateMeter>  TRANSPORTERS = new ConcurrentHashMap<>();
    
    final Executor EXEC = Executors.newSingleThreadExecutor();            
    ObservableList<Label> list = FXCollections.observableArrayList();
    
    {
        if(checkIfInitExists()){
            try {
                setUpConfiguration();
                initTransporters("192.168.0.0/24",7999);
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
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        REFRESH.setOnAction(event ->{
            try {
                initTransporters("192.168.0.0/24",7999);
            } catch (SocketException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        LOG_OUT_BUTTON.setOnAction(event ->{
            LOGGED_IN = "root";
            DISCONNECT.fire();
            IS_LOGGED_IN = false;
            CONNECT.setDisable(true);
            DISCONNECT.setDisable(true);
            SETTINGS.setDisable(true);
            REFRESH.setDisable(true);
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
            System.out.println(IS_CONNECTED);
                if(IS_CONNECTED){ 
                    try {
                        System.out.println("Referring to downloader method");
                        downloadAndSetToModel();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
        });
        
        DISCONNECT.setOnAction(event ->{
            IS_CONNECTED = false;
        });
        
    }   

    
    private boolean checkIfInitExists(){
        return new File("init.xml").exists();
    }
    
    private void setUpConfiguration() throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException, SQLException{
            XMLController xml = new XMLController();
            INIT = xml.parseInitFile();
    }

    private void loginUser() throws SQLException, ClassNotFoundException {
        if(LOGIN.getText() != null&PASS.getText() != null){
            if(LOGIN.getText().equals("root")&PASS.getText().equals("q@wertyuiop")){
                viewAlert("Login","Logging in","Logged in as root.",AlertType.INFORMATION);
                IS_LOGGED_IN = true;
                CONNECT.setDisable(false);
                DISCONNECT.setDisable(false);
                SETTINGS.setDisable(false);
                REFRESH.setDisable(false);
            }else{
               //XML parser here
                if(!LOGGED_IN.isEmpty()){
                    viewAlert("Login","Logging in","Logged in as "+LOGGED_IN,AlertType.INFORMATION);
                    CONNECT.setDisable(false);
                    DISCONNECT.setDisable(false);
                    SETTINGS.setDisable(false);
                    REFRESH.setDisable(false);
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
    public String preparePath(String path) {
        return path;
    }

    @Override
    public void viewError(String text) {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText("An error occured.");
        a.setContentText(text);
        a.showAndWait();
    } 

    private void downloadAndSetToModel() throws InterruptedException {
        
          Service<String> transport_ctrl = new Service<String>() {
          @Override 
          protected Task<String> createTask() {
              return new Task<String>() {
                  @Override protected String call() throws InterruptedException {
                       System.out.println(IS_CONNECTED);
                        while(IS_CONNECTED){
                           System.out.println("Task is processing... using count of: "+TRANSPORTERS.size());
                           TRANSPORTERS.forEach((x,y) ->{
                               Connection c = (Connection)x;
                               Established e = new Established();
                               c.changeState(e);
                               
                               Transport t = c.getTranportInstance();
                               System.out.println(t.getIp());
                              
                               byte[] buffer;
                                //System.out.println(buffer.length);
                               try {
                                   buffer = c.read(t);
                                  
                                   System.out.println("Data has been read from input stream...");
                                   processData(buffer);
                               } catch (TransportException | IOException ex) {
                                   Logger.getLogger(FXMLController.class.getName()).log(Level.ALL, null, ex);
                                   System.err.println("Error reading buffer.");
                               }
                           });
                        }   
                      return "";
                  }
              };
          }
          
          private Image processData(byte[] buffer) throws IOException {
                    Image out = null;
                    ByteArrayInputStream is = null;
                    byte[] result = null;
                    System.out.println("Reading stream to buffer...");
                    int len = (int)buffer[0]+1;
                    char[] c = new char[len];

                    for(int i = 1; i<len; i++){
                        c[i] = (char)((int)buffer[i]);
                    }

                    String name = String.valueOf(c);
                    System.out.println("Name received: "+name);
                    result = Arrays.copyOf(buffer, 8192-len);
                    is = new ByteArrayInputStream(result);
                   // out = new Image(is,100,50,true,true);
                    
                    BufferedImage bf = ImageIO.read(is);
                    System.out.println(is.available());
                    System.out.println("Y: "+bf.getHeight());
                    ImageIO.write(bf, name, new FileOutputStream("/home/lukas/Desktop"));
                    System.out.println(out.getWidth());
                    
                    return out;
            }
    };
    
 
        transport_ctrl.start();
        System.out.println("Thread started.");
    }

    private void initTransporters(String subnet, int port) throws SocketException  {
        NetController net = new NetController();
        String[] pool = net.getIpPool(subnet);
        for(String ip : pool){
            try{
                if(net.isReachable(ip)){
                    Socket s = new Socket(ip,port);
                    s.setTcpNoDelay(true);
                    Opened o = new Opened();
                    Connection c = new Connection();
                    c.changeState(o);
                    c.open(s.getInputStream(), s.getOutputStream());
                    c.setIp(ip);
                    Transport tr  = c.getTranportInstance();
                    BaudrateMeter bd = new BaudrateMeter();

                    tr.setBaudrateMeter(bd);
                    
                    TRANSPORTERS.put(c,bd);
                    System.out.println("Transporter at "+ip+" added.");
                }
            }catch(Exception e){
                //e.printStackTrace();
                System.err.println(ip+" unreachable!");
            }
        }  
        if(TRANSPORTERS.size() > 0){
            IS_CONNECTED = true;
        }
    }
}
