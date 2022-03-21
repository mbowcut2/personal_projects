package com.example.familymapclient.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.familymapclient.utils.DataCache;
import com.example.familymapclient.utils.ServerProxy;

import java.io.IOException;

import request.LoginRequest;
import result.LoginResult;

public class LoginTask implements Runnable{

    private final Handler messageHandler;
    private final String username;
    private final String password;
    private final String serverHost;
    private final String serverPort;
    private LoginResult result;
    private String personID;
    private DataCache dataCache = DataCache.getInstance();

    public LoginTask(Handler messageHandler, String serverHost, String serverPort, String username, String password) {
        this.messageHandler = messageHandler;
        this.username = username;
        this.password = password;
        this.serverHost = serverHost;
        this.serverPort = serverPort;

    }

    @Override
    public void run() {

        ServerProxy proxy = new ServerProxy(serverHost, serverPort);
        try {
            result = proxy.login(new LoginRequest(username, password));
            System.out.println(result);
            this.personID = result.getPersonID();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result.isSuccess()) {
            dataCache.setAuthtoken(result.getAuthtoken());
            sendMessage(result.getAuthtoken());
        }
        else {
            sendMessage(null);
        }

    }

    private void sendMessage(String authtoken) {

        if(authtoken != null) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putString("AUTHTOKEN", authtoken);
            messageBundle.putString("PERSONID", personID);
            messageBundle.putBoolean("SUCCESS", true);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
        else {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean("SUCCESS", false);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }
}
