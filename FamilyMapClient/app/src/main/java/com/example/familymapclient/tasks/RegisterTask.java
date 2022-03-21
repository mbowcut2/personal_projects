package com.example.familymapclient.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.familymapclient.utils.DataCache;
import com.example.familymapclient.utils.ServerProxy;

import request.RegisterRequest;
import result.RegisterResult;

public class RegisterTask implements Runnable{

    private final Handler messageHandler;
    private final String username;
    private final String password;
    private final String serverHost;
    private final String serverPort;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String gender;
    private RegisterResult result;
    private DataCache dataCache = DataCache.getInstance();

    public RegisterTask(
            Handler messageHandler,
            String serverHost,
            String serverPort,
            String username,
            String password,
            String firstName,
            String lastName,
            String email,
            String gender) {
        this.messageHandler = messageHandler;
        this.username = username;
        this.password = password;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
    }

    @Override
    public void run() {

        ServerProxy proxy = new ServerProxy(serverHost, serverPort);
        result = proxy.register(
                new RegisterRequest(
                        username,
                        password,
                        email,
                        firstName,
                        lastName,
                        gender));

        //System.out.println(result);

        if (result.isSuccess()) {
            dataCache.setAuthtoken(result.getAuthtoken());
            sendMessage(result.getAuthtoken(), result.getPersonID());
        }
        else {
            sendMessage(null, null);
        }

    }

    private void sendMessage(String authtoken, String personID) {

        if(authtoken != null) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putString("AUTHTOKEN", authtoken);
            messageBundle.putBoolean("SUCCESS", true);
            messageBundle.putString("PERSONID", personID);
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
