package com.neology.main;

import com.neology.Hasher;
import com.neology.environment.Local;
import com.neology.environment.LocalEnvironment;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;


public class MainApp extends Application{
    @Override
    public void start(Stage stage) throws Exception {
        double x = Toolkit.getDefaultToolkit().getScreenSize().width * 0.75;
        double y = Toolkit.getDefaultToolkit().getScreenSize().height * 0.75;
        
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        
        Scene scene = new Scene(root,x,y);
        
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setOnCloseRequest(event ->{
            File img = new File(LocalEnvironment.getLocalVar(Local.TMP));
            File[] files = img.listFiles();
            
            if(files != null){
                for(File f : files){
                    if(f.getName().endsWith(".jpg") || f.getName().endsWith(".JPG")){
                        try {
                            Files.delete(f.toPath());
                        } catch (IOException ex) {
                            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

            Thread[] tarray = new Thread[Thread.activeCount()];
            Thread.enumerate(tarray);
            for(Thread t : tarray){
                if(t.getName().equals("TCPThread")){
                    openLoginDialog(event);
                }
            }
        });
        
        stage.setMinWidth(x);
        stage.setMinHeight(y);
        stage.setTitle("Amelia Server");
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void openLoginDialog(WindowEvent evt) {
        // Create the custom dialog.
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Closing app...");
            dialog.setHeaderText("Root Credentials Needed");
            
            ButtonType loginButtonType = new ButtonType("Exit", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField username = new TextField();
            username.setPromptText("Username");
            PasswordField password = new PasswordField();
            password.setPromptText("Password");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(username, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(password, 1, 1);
            dialog.getDialogPane().getStylesheets().add("/styles/Styles.css");
            Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
            loginButton.setDisable(true);

            username.textProperty().addListener((observable, oldValue, newValue) -> {
                loginButton.setDisable(newValue.trim().isEmpty());
            });

            dialog.getDialogPane().setContent(grid);

            Platform.runLater(() -> username.requestFocus());

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return new Pair<>(username.getText(), password.getText());
                }else{
                    return new Pair<>("","");
                }
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(usernamePassword -> {
                if(Hasher.sha(usernamePassword.getValue()).equals(">:��ܰb-���ᦦ�sض5�Z��kxK") && usernamePassword.getKey().equals("root")){
                    System.exit(0);
                }else{
                    evt.consume();
                }
            });
    }
}
