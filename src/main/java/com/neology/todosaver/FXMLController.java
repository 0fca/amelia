package com.neology.todosaver;

import abstracts.LocalEnvironment;
import com.neology.exceptions.TransportException;
import com.neology.interfaces.Viewable;
import com.neology.net.BaudrateMeter;
import com.neology.net.Closed;
import com.neology.net.Connection;
import com.neology.net.Established;
import com.neology.net.NetController;
import com.neology.net.Opened;
import com.neology.net.Transport;
import com.neology.xml.XMLController;
import enums.Local;
import java.awt.image.BufferedImage;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventType;
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
import javax.imageio.ImageIO;
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
    private LocalEnvironment TMP_DIR = new LocalEnvironment() {
        @Override
        public String preparePath(String path) {
            return path.replace(path.contains("\\") == true ? "/" : "\\"  , File.separator);
        }
    };
    
    
    static ServerSocket ss = null;
    static Socket s = null;
    NetController n = null;
    private static volatile int PORT;
    protected ArrayList<String> INIT;
    protected boolean IS_LOGGED_IN = false;
    protected String LOGGED_IN = "";
    protected boolean IS_CONNECTED = false;
    private int INDEX = 0;
    private HashMap<String,Image> IMAGES = new HashMap<>();
    private ArrayList<String> DATA = new ArrayList<>();
    private HashMap<String,Service> THREADS = new HashMap<>();
    final Executor EXEC = Executors.newSingleThreadExecutor();            
    ObservableList<Label> list = FXCollections.observableArrayList();
    private ConnectionManager c = new ConnectionManager();
    
    {
        s = new Socket();
        n = new NetController(s);
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
                s = new Socket();
                if(c.getState() == Service.State.READY){
                    initConnectionManager();
                }
            }else{
                initConnectionManager();
            }
        });
        
        DISCONNECT.setOnAction(event ->{
            IMAGES.clear();
            for(Service t : THREADS.values()){
                t.cancel();
                t = null;
            }
            THREADS.clear();
            Platform.runLater(() ->{
                VIEWER_PANEL.getItems().clear(); 
            });
            c.setOnCancelled(canc_evt ->{
                try {
                    ss.close();
                    s.close();
                    ss = null;
                    s = null;
                } catch (IOException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            });
            c.cancel();
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
                DISCONNECT.setDisable(false);
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

    private boolean checkFolders() {
       String tmp = TMP_DIR.getLocalVar(Local.TMP);
       return new File(tmp).mkdir();  
    }

    private void initConnectionManager() {
        c.start();
    }
    
    
    public class ConnectionManager extends Service {

      private Connection initConnection() throws IOException{
            Opened o = new Opened();
            Connection c = new Connection();
            c.changeState(o);
            c.open(s.getInputStream(), s.getOutputStream());
            c.setIp(s.getRemoteSocketAddress().toString());
            return c;
      }

        @Override
        protected Task createTask() {
            return new Task<Void>(){
                @Override
                public Void call(){
                    try {
                        if(ss == null){
                          ss = new ServerSocket(PORT,16,new InetSocketAddress(n.getIp(),7999).getAddress());
                          System.out.println("ServerSocket prepared.");
                        }
                        while(!this.isCancelled()){
                            s = ss.accept();
                            IS_CONNECTED = true;
                            System.out.println("Accepted.");
                                Connection c;
                                try {
                                    c = initConnection();
                                    BaudrateMeter meter = new BaudrateMeter();
                                    c.getTranportInstance().setBaudrateMeter(meter);
                                    TCPService t = new TCPService(c,INDEX);
                                    t.start();

                                    THREADS.put(c.getTranportInstance().getIp(), t);
                                    INDEX++;
                                } catch (IOException ex) {
                                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }

                        }
                        System.out.println("ConnectionManager stop.");
                    } catch (IOException ex) {
                          Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                return null;
                }
            };
        }
  }
    
    public class TCPService extends Service{
      DataInputStream din;
      DataOutputStream dout;
      private Connection CON;
      private String NAME;
      private int INDEX = 0;
      private BaudrateMeter MTR;
      
      public TCPService(Connection c,int index){
          this.CON = c;
          this.INDEX = index;
          this.MTR = CON.getTranportInstance().getBaudrateMeter();
      }
      
      private Image processData(byte[] buffer) throws IOException {
                ByteArrayInputStream is;
                byte[] result;
                int len = (int)buffer[0]+1;
                char[] c = new char[len];

                for(int i = 1; i<len; i++){
                    c[i] = (char)((int)buffer[i]);
                }

                String name = String.valueOf(c).trim();
                NAME = name;
                System.out.println("FILE_NAME_RECEIVED: "+name);
                result = Arrays.copyOfRange(buffer,len,8192);
                is = new ByteArrayInputStream(result);
                
                BufferedImage bf = ImageIO.read(is);
                System.out.println("AVAILABLE_BYTE_COUNT: "+is.available());
                ImageIO.write(bf, "JPG", new FileOutputStream(TMP_DIR.getLocalVar(Local.TMP)+File.separator+name+".jpg"));
                Image out = new Image("file:///"+TMP_DIR.getLocalVar(Local.TMP)+File.separator+NAME+".jpg",150,100,true,true);
                System.err.println("ERROR: "+out.isError());
                System.out.println("LDR_STATE: "+out.getProgress());
                
                
                System.out.println("Buffer: "+buffer.length);
                MTR.count(buffer.length);
                MTR.stopMeasuringCycle();
                return out;
        }

        @Override
        protected Task createTask() {
            return new Task<Void>(){
                @Override
                protected Void call() throws Exception {
                    while(!this.isCancelled()){
                       MTR.startMeasuringCycle();
                       Established e = new Established();
                       CON.changeState(e);

                       Transport t = CON.getTranportInstance();
                       System.out.println("TRANSPORTER_IP: "+t.getIp());

                       byte[] buffer;
                       try {
                           buffer = CON.read(t);
                           Image im = processData(buffer);
                           IMAGES.put("file:///"+TMP_DIR.getLocalVar(Local.TMP)+File.separator+NAME+".jpg", im);
                           
                           String data = "";
                           data += "IP: "+t.getIp().substring(1)+",";
                           data += "Speed: "+t.getBaudrateMeter().kBPS()+"kB/s,";
                           data += "Is connected: "+t.isConnected()+",";
                           data += "Was connected earlier: "+t.wasConnected();
                           //System.out.println(data);
                           DATA.add(data);
                           
                           Platform.runLater(() ->{
                               if(VIEWER_PANEL.getItems().isEmpty() || VIEWER_PANEL.getItems().size() == INDEX){
                                    VIEWER_PANEL.getItems().add("file:///"+TMP_DIR.getLocalVar(Local.TMP)+File.separator+NAME+".jpg");
                               }else{
                                    VIEWER_PANEL.getItems().set(INDEX,"file:///"+TMP_DIR.getLocalVar(Local.TMP)+File.separator+NAME+".jpg");
                               }
                           });
                          
                       } catch (TransportException | IOException ex) {
                           System.err.println("LOCALIZED_ERR_MSG:"+ex.getLocalizedMessage());
                           THREADS.forEach((desc,serv) ->{
                               if(desc.equals(t.getIp())){
                                   serv.cancel();
                               }
                           });
                           THREADS.remove(t.getIp());
                       }
                    }
                    Closed c = new Closed();
                    CON.changeState(c);
                    CON.close();
                    return null;
                }
            };
        }
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
                
                Image out = IMAGES.get(item.toString());
                System.err.println("ERROR: "+out.isError());
                System.out.println("LDR_STATE: "+out.getProgress());
                setGraphic(new ImageView(out));
                this.setOnMouseClicked(evt ->{
                   int index = VIEWER_PANEL.getSelectionModel().getSelectedIndex();
                   String data = DATA.get(index);
                   
                   String[] splitted = data.split(",");
                   INFO_VIEW.getItems().clear();
                   INFO_VIEW.getItems().addAll(Arrays.asList(splitted));
                   
                });
                this.setTextAlignment(TextAlignment.CENTER);
            }
        }
    }
}
