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
package com.neology;

import com.neology.data.Session;
import com.neology.lastdays.LastDaysService;
import com.neology.lastdays.TodoTicket;
import com.neology.parsing.JSONController;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 *
 * @author obsidiam
 */
public class RestClient {
    private Retrofit retrofit;
    private LastDaysService service;
    private String output;
    
     public void init(){
        if(!wasInitiated()) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://thelastdays.ct8.pl/")
                    .build();
            service = retrofit.create(LastDaysService.class);
        }
    }
    
    private boolean wasInitiated() {
        return (service != null && retrofit != null);
    }
     
    public boolean loginUser(String userName, String pass, int expires) throws IOException {
        ResponseBody body = service.postCredential(userName,pass,expires+"d").execute().body();
        if(body != null) {
            output = body.string();
            System.out.println(output);
            return JSONController.getInstance().parseLogin(output);
        }
        return false;
    }
    
    public boolean registerUser(String userName, String pass, String mail) throws IOException{
        ResponseBody body = service.postRegisterUser(userName, pass, mail).execute().body();
        if(body != null) {
            output = body.string();
            System.out.println(output);
            return JSONController.getInstance().registerUser(output);
        }
        return false;
    }
    
    public ArrayList<TodoTicket> getTodoTickets(String token) throws IOException{
        ResponseBody body = service.getTodoList(token).execute().body();
        if(body != null){
            output = body.string();
            return JSONController.getInstance().parseFrame(output).getTickets();
        }
        return null;
    }
    
    public Session getSession(){
        return JSONController.getInstance().getSession();
    }
}
