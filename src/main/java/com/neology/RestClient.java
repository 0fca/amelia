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

import com.neology.data.model.Frame;
import com.neology.data.model.LoginData;
import com.neology.lastdays.LastDaysService;
import com.neology.lastdays.TodoResult;
import com.neology.lastdays.TodoTicket;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.io.IOException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * @author obsidiam
 */
public class RestClient {
    private Retrofit retrofit;
    private LastDaysService service;
    
     public void init(){
        if(!wasInitiated()) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://thelastdays.ct8.pl/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            service = retrofit.create(LastDaysService.class);
        }
    }
    
    private boolean wasInitiated() {
        return (service != null && retrofit != null);
    }
     
    public LoginData loginUser(String userName, String pass, int expires) throws IOException {
        return service.postCredential(userName,pass,expires+"d").blockingGet();
    }
    
    public boolean registerUser(String userName, String pass, String mail) throws IOException{
       return service.postRegisterUser(userName, pass, mail).blockingGet().getSuccess();
    }
    
    public Observable<Frame> getTodoTickets(String token) throws IOException{
        return service.getTodoList(token);
    }
    
    public boolean postTodo(byte[] token,TodoTicket t){
        Single<TodoResult> result = service.postTodo(new String(token), t);
        
        return result.blockingGet().getSuccess();
    }
}
