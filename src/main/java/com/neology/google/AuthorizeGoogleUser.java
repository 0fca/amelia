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
package com.neology.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import com.google.api.services.oauth2.Oauth2;
import com.neology.environment.Local;
import com.neology.environment.LocalEnvironment;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 *
 * @author obsidiam
 */
public class AuthorizeGoogleUser {
      private static final String APPLICATION_NAME = "obsidiam-Amelia Server/2.2";

      private static final java.io.File DATA_STORE_DIR =
          new java.io.File(System.getProperty("user.home"), ".store/amelia-server");


      private static FileDataStoreFactory dataStoreFactory;

      private static HttpTransport httpTransport;

      private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

      private static final List<String> SCOPES = Arrays.asList(
          "https://www.googleapis.com/auth/userinfo.profile",
          "https://www.googleapis.com/auth/userinfo.email");

      private static Oauth2 oauth2;
      private static GoogleClientSecrets clientSecrets;
      static LocalEnvironment env = new LocalEnvironment() {};
      private static String name = "";
      
      public static void doLogin(String user){
          try {
              httpTransport = GoogleNetHttpTransport.newTrustedTransport();
              dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
              Credential credential = authorize(user);
              oauth2 = new Oauth2.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
              String picture = oauth2.userinfo().get().execute().getPicture();
              name = oauth2.userinfo().get().execute().getName();
              downloadProfileImage(picture,name.toLowerCase());
              
        } catch (IOException e) {
          System.err.println(e.getMessage());
        } catch (Exception t) {

        }
      }

      private static Credential authorize(String user) throws Exception {
        clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
            new InputStreamReader(AuthorizeGoogleUser.class.getResourceAsStream("/amelia-server/client_id.json")));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
            || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
          System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
              + "into oauth2-cmdline-sample/src/main/resources/client_secrets.json");
        }

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            httpTransport, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(
            dataStoreFactory).build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(user);
      }
      
      private static void downloadProfileImage(String picture,String name){
            Image image = null;
            try {
                URL url = new URL(picture);
                image = ImageIO.read(url);
                BufferedImage bimg = toBufferedImage(image);
                ImageIO.write(bimg, "PNG", new File(env.getLocalVar(Local.TMP)+File.separator+name+".png"));
            } catch (IOException e) {
                System.err.println(e);
            }
            
      }
      
      private static BufferedImage toBufferedImage(Image img){
            if (img instanceof BufferedImage){
                return (BufferedImage) img;
            }

            BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.SCALE_SMOOTH);
 
            Graphics2D bGr = bimage.createGraphics();
            bGr.drawImage(img, 0, 0, null);
            bGr.dispose();
            return bimage;
      }
      
      public static String getName(){
          return name;
      }
}
