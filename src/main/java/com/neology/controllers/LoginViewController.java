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

import com.neology.net.LoginExecutor;
import com.neology.net.RestClient;
import com.neology.controllers.alert.AlertMethod;
import com.neology.google.GoogleService;
import com.neology.main.SettingsForm;
import com.neology.views.Constants;
import com.neology.views.LoginView;
import com.neology.views.ViewFactory;
import com.neology.views.drawer.Status;
import java.io.IOException;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author obsidiam
 */
public class LoginViewController implements Initializable {

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

    private static LoginView lv;
    private AlertController ac = new AlertController();
    private boolean IS_LOGGED_IN;
    private String loginType;
    private LoginExecutor lc;
    private RestClient rest;
    private String ACTUAL_NAME,LOGGED_IN;
    private final static LoginViewController.LoginViewObservable DOB = new LoginViewController.LoginViewObservable();
    private static Status st = new Status();
    FXMLLoader fxmlLoader = new FXMLLoader();

    static{
        DOB.addObserver(new MainViewController.MainObserver());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGIN_BUTTON.setOnAction(event ->{
            if(!IS_LOGGED_IN){
                if(loginType != null && LOGIN.getText() != null){
                    rest = new RestClient();
                    st.setShouldShowIndicator(true);
                    DOB.valuesChanged();
                    if(loginType.equals("LD")){
                        lc = new LoginExecutor(rest);
                        try {
                            if(lc.loginWithLD(LOGIN.getText(), PASS.getText())){
                                viewConfirmationDialog(LOGIN.getText());
                                DrawerController.getViewInstance().getController().setProfileImage(ACTUAL_NAME);
                                DrawerController.getViewInstance().getController().setUserName(ACTUAL_NAME);
                                LOGIN_BUTTON.setText("Log out");
                                st.setUnlockAnyButtons(true);
                                st.setButtonNames(new String[]{"CONNECT"});
                                st.setShouldShowIndicator(false);
                                DOB.valuesChanged();
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
                            DrawerController.getViewInstance().getController().setProfileImage(ACTUAL_NAME);
                            DrawerController.getViewInstance().getController().setUserName(ACTUAL_NAME);
                            st.setUnlockAnyButtons(true);
                            st.setButtonNames(new String[]{"CONNECT"});
                            st.setShouldShowIndicator(false);
                            DOB.valuesChanged();
                        });
                        lc = new LoginExecutor(g);
                        lc.loginWithGoogleAccount();
                        LOGIN_BUTTON.setText("Log out");
                    }
                }
            }else{
                LOGGED_IN = null;
                st.setShouldDisconnect(true);
                IS_LOGGED_IN = false;
                DOB.valuesChanged();
                SETTINGS.setDisable(true);
                LOGIN_BUTTON.setText("Log in");
                DrawerController.getViewInstance().getController().setProfileImage(null);
                DrawerController.getViewInstance().getController().setUserName(null);
                animateViewMove();
                LOGIN.setDisable(true);
                PASS.setDisable(true);
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
            System.out.println("Clicked");
            PASS.setDisable(false);
            LOGIN.setDisable(false);
        });
    }

      
    
    private void viewConfirmationDialog(String text) {
        ac.prepareViewable(new Object[]{"Login","Logged in as "+text,5, Pos.BASELINE_RIGHT});
        ac.viewAlert(AlertMethod.NOTIFICATION);
        IS_LOGGED_IN = true;
        SETTINGS.setDisable(false);
        LOGIN.setText(null);
        PASS.setText(null);

        ACTUAL_NAME = text; 
        animateViewMove();
    }
    
    static void animateViewMove() {
        TranslateTransition openNav = new TranslateTransition(new Duration(350), lv);
        openNav.setToX(lv.getWidth());
        TranslateTransition closeNav = new TranslateTransition(new Duration(350), lv);
        
        if(lv.getTranslateX() < lv.getWidth()){
            lv.requestFocus();
            openNav.play();
        }else{
            closeNav.setToX(-(lv.getWidth()));
            closeNav.play();
        }
    }   
    
    static LoginView getViewInstance(){
        if(lv != null){
            return lv;
        }else{
            lv = (LoginView)ViewFactory.getInstance().getConcreteView(Constants.LOGIN);
            lv.loadView();
            return lv;
        }
    }
    
    static class LoginViewObservable extends Observable{
        void valuesChanged(){
            setChanged();
            notifyObservers(st);
        }
    }
}
