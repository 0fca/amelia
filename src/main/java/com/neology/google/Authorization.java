/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.Drive;
import com.neology.environment.LocalEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Authorization {
    /** Application name. */
    private static final String APPLICATION_NAME = "Amelia Server";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/amelia-server-"+System.getProperty("user.name"));

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;
    
    /** Global variable of String type representing user's nickname. */
    private static String NICK = "user";
    /** Global instance of the scopes required by this quickstart.
     *
     */
    private static Credential MAIN;
    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE);

    private static ArrayList<String> NAMES = new ArrayList<>();
    private static ArrayList<String> FILES_IDS = new ArrayList<>();
    private static String DATE;
    private static LocalEnvironment ENV = new LocalEnvironment(){};
    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (IOException | GeneralSecurityException t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    //
    public static void authorize() throws IOException {
        InputStream in = Authorization.class.getClass().getResourceAsStream("/amelia-server/client_id.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize(NICK);
        
        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        MAIN = credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public static Drive getDriveService() throws IOException {
        Credential credential = MAIN;
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    public static void setNick(String nick){
        NICK = nick;
    }
    
    public static ArrayList<String> getFilesList(){
        return NAMES;
    }
    
    public static ArrayList<String> getFilesIds(){
        return FILES_IDS;
    }
}