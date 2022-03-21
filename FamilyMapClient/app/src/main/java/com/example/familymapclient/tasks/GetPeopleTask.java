package com.example.familymapclient.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.familymapclient.utils.DataCache;
import com.example.familymapclient.utils.ServerProxy;

import model.Person;
import request.PersonRequest;
import result.PersonResult;

public class GetPeopleTask implements Runnable{
    private final Handler messageHandler;
    private final String authtoken;
    private final String serverHost;
    private final String serverPort;
    private final String personID;
    private PersonResult result;
    private DataCache dataCache = DataCache.getInstance();

    public GetPeopleTask(Handler messageHandler, String serverHost, String serverPort, String authtoken, String personID) {

        this.authtoken = authtoken;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageHandler = messageHandler;
        this.personID = personID;

    }

    @Override
    public void run() {

        ServerProxy proxy = new ServerProxy(serverHost, serverPort);
        result = proxy.getPeople(new PersonRequest(authtoken));

        if (result.isSuccess()) {
            dataCache.setPersons(result.getData());
            sendMessage();
        }
        else {
            sendMessage();
        }

    }

    private void sendMessage() {

        if (authtoken != null) {
            Message message = Message.obtain();
            Person person = dataCache.getPerson(personID);

            Bundle messageBundle = new Bundle();
            messageBundle.putString("AUTHTOKEN", authtoken);
            messageBundle.putBoolean("SUCCESS", true);
            messageBundle.putString("FIRST_NAME", person.getFirstName());
            messageBundle.putString("LAST_NAME", person.getLastName());
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
