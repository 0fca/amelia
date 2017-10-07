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
package com.neology.data;

import com.neology.lastdays.TodoTicket;
import java.util.ArrayList;

/**
 *
 * @author obsidiam
 */
public class Frame {
    private boolean success = false;
    private ArrayList<TodoTicket> todo = new ArrayList<>();

    public void setSuccess(boolean success){
        this.success = success;
    }

    public boolean getSuccess(){
        return success;
    }

    public void setTicket(ArrayList<TodoTicket> ticket){
        this.todo = ticket;
    }

    public TodoTicket getTicket(int index){
        return todo.get(index);
    }

    public ArrayList<TodoTicket> getTickets(){
        return todo;
    }
}

