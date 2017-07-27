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
import enums.Local;
import java.awt.image.BufferedImage;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
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
    private volatile ListView VIEWER_PANEL;
    String ACTUAL_NAME;
    private LocalEnvironment TMP_DIR = new LocalEnvironment() {
        @Override
        public String preparePath(String path) {
            return path.replace(path.contains("\\") == true ? "/" : "\\"  , File.separator);
        }
    };
    
    
    static ServerSocket ss;
    static Socket s;
    static String msgout;
    private static volatile int PORT;
    protected ArrayList<String> INIT;
    protected boolean IS_LOGGED_IN = false;
    protected String LOGGED_IN = "";
    protected boolean IS_CONNECTED = false;
    private int INDEX = 0;
    //static ConcurrentHashMap<Connection,BaudrateMeter>  TRANSPORTERS = new ConcurrentHashMap<>();
    private HashMap<String,Image> IMAGES = new HashMap<>();
    
    private HashMap<String,Service> THREADS = new HashMap<>();
    final Executor EXEC = Executors.newSingleThreadExecutor();            
    ObservableList<Label> list = FXCollections.observableArrayList();
    
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
            initConnectionManager();
        });
        
        DISCONNECT.setOnAction(event ->{
            IMAGES.clear();
            THREADS.forEach((desc,serv) ->{
                serv.cancel();
            });

            THREADS.clear();
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

    private boolean checkFolders() {
       String tmp = TMP_DIR.getLocalVar(Local.TMP);
       return new File(tmp).mkdir();  
    }

    private void initConnectionManager() {
        ConnectionManager c = new ConnectionManager();
        c.start();
    }
    
    
    public class ConnectionManager extends Thread implements Runnable{
      private Thread T;
      
      @Override
      public void start(){
          if(T == null){
              T = new Thread(this,"ConnectionManager");
              T.setDaemon(true);
              T.start();
          }
      }
      
      @Override
      public void run(){
            try {
                if(ss == null){
                  ss = new ServerSocket(PORT);
                }
                s = ss.accept();
                IS_CONNECTED = true;
                System.out.println("Accepted.");
                    Connection c;
                    try {
                        c = initConnection();
                        TCPService t = new TCPService(c,INDEX);
                        t.start();
                        THREADS.put(c.getTranportInstance().getIp(), t);
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
     
                INDEX++;
            } catch (IOException ex) {
                  Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
             }
      }
      
      private Connection initConnection() throws IOException{
            Opened o = new Opened();
            Connection c = new Connection();
            c.changeState(o);
            c.open(s.getInputStream(), s.getOutputStream());
            c.setIp(s.getRemoteSocketAddress().toString());
            return c;
      }
  }
    
    public class TCPService extends Service{
      DataInputStream din;
      DataOutputStream dout;
      private Connection CON;
      private String NAME;
      private int INDEX = 0;
      
      public TCPService(Connection c,int index){
          this.CON = c;
          this.INDEX = index;
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
                //System.out.println(bf.toString());
                System.out.println("AVAILABLE_BYTE_COUNT: "+is.available());
                //System.out.println("Y: "+bf.getHeight());
                ImageIO.write(bf, "JPG", new FileOutputStream(TMP_DIR.getLocalVar(Local.TMP)+File.separator+name+".jpg"));
                Image out = new Image("file:///"+TMP_DIR.getLocalVar(Local.TMP)+File.separator+NAME+".jpg",150,100,true,true);
                System.err.println("ERROR: "+out.isError());
                System.out.println("LDR_STATE: "+out.getProgress());
                return out;
        }

        @Override
        protected Task createTask() {
            return new Task<Void>(){
                @Override
                protected Void call() throws Exception {
                    while(!this.isCancelled()){
                       Established e = new Established();
                       CON.changeState(e);

                       Transport t = CON.getTranportInstance();
                       System.out.println("TRANSPORTER_IP: "+t.getIp());

                       byte[] buffer;
                       try {
                           buffer = CON.read(t);
                           Image im = processData(buffer);
                           IMAGES.put("file:///"+TMP_DIR.getLocalVar(Local.TMP)+File.separator+NAME+".jpg", im);
                           Platform.runLater(() ->{
                               VIEWER_PANEL.getItems().clear();
                               VIEWER_PANEL.getItems().add(INDEX,"file:///"+TMP_DIR.getLocalVar(Local.TMP)+File.separator+NAME+".jpg");
                           });
                       } catch (TransportException | IOException ex) {
                           System.err.println("LOCALIZED_ERR_MSG:"+ex.getLocalizedMessage());
                           THREADS.forEach((desc,serv) ->{
                               if(desc.equals(t.getIp())){
                                   serv.cancel();
                               }
                           });
                           INDEX = 0;
                       }
                    }
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
                setText(null);
                
                Image out = IMAGES.get(item.toString());
                System.err.println("ERROR: "+out.isError());
                System.out.println("LDR_STATE: "+out.getProgress());
      
                setGraphic(new ImageView(out));
               
            }
        }
    }
}
