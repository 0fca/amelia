package com.neology.todosaver;

import abstracts.LocalEnvironment;
import com.neology.database.DatabaseController;
import com.neology.interfaces.Reachable;
import com.neology.interfaces.Viewable;
import com.neology.net.ActiveIpChecker;
import com.neology.net.NetController;
import com.neology.net.Transporter;
import com.neology.xml.XMLController;
import enums.Local;
import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
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
    
    
    final CopyOnWriteArrayList<Socket> TABLE = new CopyOnWriteArrayList<>();
    protected DatabaseController DB;
    protected ArrayList<String> INIT;
    protected ArrayList<String> POOL = new ArrayList<>();
    protected boolean IS_LOGGED_IN = false;
    protected String LOGGED_IN = "";
    protected boolean IS_CONNECTED = false;
    //final Connector connector = new Connector();
    final Transporter TRANSPORTER = new Transporter();
    
    final Executor EXEC = Executors.newSingleThreadExecutor();            
    ObservableList<Label> list = FXCollections.observableArrayList();
    private ArrayList<String> LIST = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGIN_BUTTON.setOnAction(event ->{
            try {
                if(checkIfInitExists()){
                    setUpConfiguration();
                }
                loginUser();
            } catch (SAXException | IOException | ParserConfigurationException | ClassNotFoundException | SQLException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        REFRESH.setOnAction(event ->{
            
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
            NetController net = new NetController();
                if(!IS_CONNECTED){ 
                    try {
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
        Task<String> list_controller = new Task<String>(){
            @Override
            protected String call() throws AWTException, IOException, SocketException, Exception {
                ActiveIpChecker ch = new ActiveIpChecker();
                ch.prepareList();
               
                System.out.println("IPs checked.");
                TRANSPORTER.downloadScreen();
                
                ArrayList<Label> labels = new ArrayList<>();
                LIST.forEach(image ->{
                    Label l = new Label(new File(image).getName());
                    l.setGraphic(new ImageView("file:///"+image));
                    labels.add(l);
                    System.out.println(LIST.size());
                });
                
                VIEWER_PANEL.getChildren().addAll(labels);
                System.out.println(VIEWER_PANEL.getChildren().size());
                System.out.println("Added.");
                return "Ldr ended.";
            }
        };
        Thread list_task = new Thread(list_controller,"ListUpdater");
        list_task.setDaemon(true);
        list_task.start();
      
    }
    
    public class ActiveIpChecker{
       
       private Thread inside = null;
       private CopyOnWriteArrayList<String> out = new CopyOnWriteArrayList<>();
       private boolean IS_CONNECTED = false;

       private NetController net = new NetController();

       LocalEnvironment local = new LocalEnvironment(){
           @Override
           public String preparePath(String path) {
               throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
           }
       };

       public void prepareList() throws UnknownHostException, SocketException, Exception{
           this.out = net.prepareActiveIpsList(updateIpInformation());
               //System.out.println(out.size());
           TRANSPORTER.prepareConnection(out);
       }
       

       public boolean getConnectionState(){
           return IS_CONNECTED;
       }

       private CopyOnWriteArrayList<String> updateIpInformation() throws NullPointerException, UnknownHostException, SocketException, Exception {
            CopyOnWriteArrayList<String> temp = new CopyOnWriteArrayList<>();
            List<String> list;

               list = Arrays.asList(net.getIpPool(local.getLocalVar(Local.SUBNET)));
                list.forEach(ip ->{
                    try {
                       temp.add(ip);
                    } catch (Exception ex) {
                        Logger.getLogger(ActiveIpChecker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            return temp;
       }
    }
    
    
    
    public class Transporter extends LocalEnvironment implements Reachable{
        private int ADDRESS_COUNT = 0;
        String ACTUAL_NAME = "";

        public int getAddressCount(){
            return ADDRESS_COUNT;
        }

        public int getIpIndex(String ip){
            return TABLE.indexOf(ip);
        }

        public synchronized void prepareConnection(CopyOnWriteArrayList<String> pool) throws Exception{
                pool.forEach(ip ->{
                    Socket con = new Socket();
                    try{
                        System.out.println("Checking if still active...");
                        if(isReachable(ip)){
                            con.connect(new InetSocketAddress(ip,7999));
                            con.setTcpNoDelay(true);
                            TABLE.add(con);

                        }else{
                            System.out.println(ip+" is unreachable.");
                        }
                    }catch (Exception ex) {
                            System.err.println("Error while trying to connect IP: "+ip);
                    }finally{
                        if(con.isConnected()){
                            System.out.println(ip+" is active!\n");
                            
                        }
                        
                    }  
                });
            IS_CONNECTED = true;
            ADDRESS_COUNT = TABLE.size();
        }

        public void downloadScreen() throws AWTException, IOException {
                TABLE.forEach(socket ->{
                    try {
                        if(TABLE.size() > 0&isReachable(socket.getInetAddress().getHostAddress())){
                            try{
                                OutputStream ou = null;
                                InputStream in = null;
                                System.out.println("Buffer set up.");
                                System.out.println(socket);
                                in = socket.getInputStream();
                                byte[] buffer = new byte[8192];
                                in.read(buffer);
                                System.out.println("Reading stream to buffer...");
                                int len = (int)buffer[0]+1;
                                char[] c = new char[len];

                                for(int i = 1; i<len; i++){
                                    c[i] = (char)((int)buffer[i]);
                                }

                                String name = String.valueOf(c);
                                this.ACTUAL_NAME = name;
                                System.out.println("Name received: "+name);
                                System.out.println("OutputStream directed to: "+Paths.get(".").toAbsolutePath().normalize().toString()+File.separatorChar+name+".jpg");
                                ou = new FileOutputStream(new File(getLocalVar(Local.LOCAL)+getLocalVar(Local.SEPARATOR)+name.trim()+".jpg"));

                                try{
                                    ou.write(buffer,len,8192-len);
                                }finally{
                                    ou.flush();
                                    ou.close();
                                    new File(Paths.get(".").toAbsolutePath().normalize().toString()+File.separatorChar+this.ACTUAL_NAME+".jpg").delete();
                                    System.gc();
                                    System.out.println("Downloaded.");
                                }

                            }catch(IOException e){
                                System.err.println("Error while doing I/O operation. Cause: "+e.getMessage());
                                if(new File(Paths.get(".").toAbsolutePath().normalize().toString()+File.separatorChar+this.ACTUAL_NAME+".jpg").exists()){
                                    new File(Paths.get(".").toAbsolutePath().normalize().toString()+File.separatorChar+this.ACTUAL_NAME+".jpg").delete();
                                    socket.close();
                                }
                            }
                        }else{
                            socket.close();
                            TABLE.remove(socket);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(Transporter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                setFilesList();
         }

        private void setFilesList() throws IOException{
            Stream<Path> files = Files.list(new File(getLocalVar(Local.LOCAL)).toPath());
            ArrayList<String> list = new ArrayList<>();
            files.forEachOrdered(img ->{
                if(img.toFile().getName().endsWith(".jpg")){
                   list.add(img.toFile().getAbsolutePath());
                }
            });  
            LIST = list;
        }

        public BufferedImage toBufferedImage(Image img){
            if (img instanceof BufferedImage){
                return (BufferedImage) img;
            }

            // Create a buffered image with transparency
            BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

            // Draw the image on to the buffered image
            Graphics2D bGr = bimage.createGraphics();
            bGr.drawImage(img, 0, 0, null);
            bGr.dispose();

            // Return the buffered image
            return bimage;
        }

        @Override
        public boolean isReachable(String ip) throws Exception {
            if(InetAddress.getByName(ip).isReachable(1000)){
                return true;
            }
            return false;
        }

        @Override
        public boolean isConnected(String ip) {
            boolean isConnected = false;
            Socket test = TABLE.get(TABLE.indexOf(ip));

            if(test.isConnected()){
                isConnected = true;
            }
            return isConnected;
        }

        @Override
        public String preparePath(String path) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
