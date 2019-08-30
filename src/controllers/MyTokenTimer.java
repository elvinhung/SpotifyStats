package controllers;

import controllers.API_Data;

import java.util.TimerTask;

public class MyTokenTimer extends TimerTask {

    @Override
    public void run(){
        API_Data.refreshToken();
    }
}

