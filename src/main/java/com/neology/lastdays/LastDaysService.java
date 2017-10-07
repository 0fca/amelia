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
package com.neology.lastdays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 *
 * @author obsidiam
 */
public interface LastDaysService {
     @FormUrlEncoded
    @POST("/api/login")
    Call<ResponseBody> postCredential(@Field("username")String userName, @Field("password") String password, @Field("expires") String expires);

    @GET("/api/token")
    Call<ResponseBody> postToken(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("/api/register")
    Call<ResponseBody> postRegisterUser(@Field("username") String userName, @Field("password")String password, @Field("email") String email);

    @FormUrlEncoded
    @POST("/api/todo")
    Call<ResponseBody> postTodo(@Header("Authorization") byte[] token, @Field("name")String name, @Field("priority") TodoTicket.Priority p, @Field("state") boolean state);

    @GET("/api/todo")
    Call<ResponseBody> getTodoList(@Header("Authorization") String token);
}

