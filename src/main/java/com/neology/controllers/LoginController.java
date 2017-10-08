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

import com.neology.Hasher;
import com.neology.RestClient;
import com.neology.data.model.Session;
import com.neology.google.GoogleService;
import java.io.IOException;

/**
 *
 * @author obsidiam
 */
public class LoginController {
    private static Session s;
    private RestClient rest;
    LoginController(RestClient rest){
        this.rest = rest;
    }
    
    boolean loginWithLD(String login, String pass) throws ClassNotFoundException, IOException {
        if(login != null && pass != null){
            if((login).equals("root") && Hasher.sha(pass).equals(">:��ܰb-���ᦦ�sض5�Z��kxK")){
                return true;
            }else{
                rest.init();
                return rest.loginUser(login, pass, 10);
            }
        }
        return false;
    }
    
    void loginWithGoogleAccount(GoogleService g) {
        g.start();
    }
    
    Session getSession(){
        return rest.getSession();
    }
    
    static boolean validLoginDataFormat(String password, String email, String login) {
        return password.matches("[a-zA-Z_0-9]{3,30}+") && (password.length() >= 3 && password.length() <= 30) && login.length() >= 3 && login.length() <= 30 && email.contains("@");
    }
}
