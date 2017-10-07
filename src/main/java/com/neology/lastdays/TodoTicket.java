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

/**
 *
 * @author obsidiam
 */
public class TodoTicket {

   private String state,name;
   private Priority priority;

   public void setState(String state){
       this.state = state;
   }

   public String getState(){
       return state;
   }

   public void setName(String name){
       this.name = name;
   }

   public String getName(){
       return name;
   }

   public Priority getPriority(){
       return priority;
   }

   public void setPriority(String name, String color){
       priority = new Priority();
       priority.setColor(color);
       priority.setName(name);
   }

   public class Priority{
       private String name = "Name", color = "#000000";

       public void setName(String name){
           this.name = name;
       }

       public String getName(){
           return this.name;
       }

       public void setColor(String color){
           this.color = color;
       }

       public String getColor(){
           return this.color;
       }
}
}
