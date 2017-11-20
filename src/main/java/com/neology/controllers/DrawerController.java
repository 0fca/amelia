/*
 * Copyright (C) 2017 zsel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.neology.controllers;

import com.neology.RestClient;
import com.neology.controllers.alert.AlertController;
import com.neology.controllers.alert.AlertMethod;
import com.neology.data.model.Session;
import com.neology.environment.Local;
import com.neology.environment.LocalEnvironment;
import com.neology.google.GoogleService;
import com.neology.main.SettingsForm;
import com.neology.views.drawer.Drawer;
import com.neology.views.drawer.DrawerFactory;
import com.neology.views.drawer.Status;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author obsidiam
 */
public class DrawerController implements Initializable {
    @FXML
    private Pane DRAWER;
    @FXML
    private Label USERNAME_LOGIN;
    @FXML
    private Label USER_IMG;
    @FXML
    private Label LOGIN_TYPE_LABEL;
    @FXML
    private Label GM_LABEL;
    @FXML
    private Label LD_LABEL;
    @FXML
    private TextField LOGIN;
    @FXML
    private PasswordField PASS;
    @FXML
    private Button LOGIN_BUTTON;
    @FXML
    private Button SETTINGS;
    @FXML
    private Button ABOUT;
    @FXML
    private TextField LOGIN_REG;
    @FXML
    private TextField EMAIL;
    @FXML
    private PasswordField PASS_REG;
    @FXML
    private Button REGISTER_BUTTON;

    private static Drawer drawer = DrawerFactory.getInstance().getDrawer("/fxml/Drawer.fxml");
    private AlertController ac = new AlertController();
    private boolean IS_LOGGED_IN;
    private String loginType;
    private LoginController lc;
    private RestClient rest;
    private String ACTUAL_NAME,LOGGED_IN;
    private final static DrawerObservable dob = new DrawerObservable();
    private static Status st = new Status();
    FXMLLoader fxmlLoader = new FXMLLoader();
    /**
     * Initializes the controller class.
     */

    static{
        dob.addObserver(new MainViewController.MainObserver());
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGIN_BUTTON.setOnAction(event ->{
            if(!IS_LOGGED_IN){
                if(loginType != null && LOGIN.getText() != null){
                    rest = new RestClient();
                    st.setShouldShowIndicator(true);
                    dob.valuesChanged();
                    if(loginType.equals("LD")){
                        lc = new LoginController(rest);
                        try {
                            if(lc.loginWithLD(LOGIN.getText(), PASS.getText())){
                                viewConfirmationDialog(LOGIN.getText());
                                setProfileImage();
                                LOGIN_BUTTON.setText("Log out");
                                st.setUnlockAnyButtons(true);
                                st.setButtonNames(new String[]{"CONNECT","DISCONNECT"});
                                st.setShouldShowIndicator(false);
                                dob.valuesChanged();
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
                            st.setUnlockAnyButtons(true);
                            st.setButtonNames(new String[]{"CONNECT","DISCONNECT"});
                            st.setShouldShowIndicator(false);
                            dob.valuesChanged();
                        });
                        lc = new LoginController(g);
                        lc.loginWithGoogleAccount();
                        LOGIN_BUTTON.setText("Log out");
                    }
                }
            }else{
                LOGGED_IN = null;
                //transitToDisconnectedMode();
                IS_LOGGED_IN = false;
                st.setButtonNames(new String[]{"CONNECT","DISCONNECT"});
                st.setUnlockAnyButtons(false);
                dob.valuesChanged();
                SETTINGS.setDisable(true);
                LOGIN_BUTTON.setText("Log in");
                USER_IMG.setGraphic(null);
                USERNAME_LOGIN.setText(null);
                LOGIN.setDisable(true);
                PASS.setDisable(true);
            }
        });

        REGISTER_BUTTON.setOnAction(event ->{
            if(LOGIN_REG.getText() != null && PASS_REG.getText() != null && EMAIL.getText() != null){
                if(LoginController.validLoginDataFormat(PASS_REG.getText(), EMAIL.getText(), PASS_REG.getText())){
                    rest.init();
                    //progressIndicator.setVisible(true);
                    if(rest.registerUser(LOGIN_REG.getText(), PASS_REG.getText(),EMAIL.getText())){
                        ac.prepareViewable(new Object[]{"Register","Registering Last Days' account","Registering to Last Days successful!",AlertType.INFORMATION});
                        ac.viewAlert(AlertMethod.INFO);
                        PASS_REG.setText(null);
                        LOGIN_REG.setText(null);
                        EMAIL.setText(null);
                        //progressIndicator.setVisible(false);
                    }else{
                        ac.prepareViewable(new Object[]{"Error. Couldn't register account "+LOGIN_REG.getText()});
                        ac.viewAlert(AlertMethod.ERROR);
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
        ABOUT.setOnAction(listener ->{
            AboutFormController about;
            try {
                about = new AboutFormController(FXMLLoader.load(getClass().getResource("/fxml/AboutForm.fxml")));
                about.showAbout();
            } catch (IOException ex) {
                Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
        
        PASS.textProperty().addListener(changed ->{
            if(PASS.getText() != null){
                LOGIN_BUTTON.setDisable(false);
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
        drawer = (Drawer)DRAWER.getParent();
        
    }

    private void setProfileImage() {
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
    
    private void viewConfirmationDialog(String text) {
        ac.prepareViewable(new Object[]{"Login","Logged in as "+text,5, Pos.BASELINE_RIGHT});
        ac.viewAlert(AlertMethod.NOTIFICATION);
        IS_LOGGED_IN = true;
        SETTINGS.setDisable(false);
        LOGIN.setText(null);
        PASS.setText(null);
        USERNAME_LOGIN.setText(text);
        ACTUAL_NAME = text; 
        animateDrawerMove();
    }
    
    static void animateDrawerMove() {
        TranslateTransition openNav = new TranslateTransition(new Duration(350), drawer);
        openNav.setToX(drawer.getWidth());
        TranslateTransition closeNav = new TranslateTransition(new Duration(350), drawer);
        
        if(drawer.getTranslateX() < drawer.getWidth()){
            openNav.play();
        }else{
            closeNav.setToX(-(drawer.getWidth()));
            closeNav.play();
        }
    }
    
    static Drawer getDrawerInstance(){
        return drawer;
    }
    
    
    
    static class DrawerObservable extends Observable{
        void valuesChanged(){
            setChanged();
            notifyObservers(st);
        }
    }
    
    
}
